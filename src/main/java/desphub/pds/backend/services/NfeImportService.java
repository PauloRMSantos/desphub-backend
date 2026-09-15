package desphub.pds.backend.services;

import desphub.pds.backend.dtos.nfe.NfeAccessKeyInfo;
import desphub.pds.backend.dtos.nfe.NfeImportResponseDTO;
import desphub.pds.backend.dtos.vehicles.CreateVehicleDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Importa dados de NF-e para PRÉ-PREENCHER um veículo (não persiste nada).
 *
 * - Chave de acesso: só carrega metadados (emitente, UF, data, número).
 * - PDF (DANFE): extrai a chave e, quando o emissor imprime o bloco de dados do
 *   veículo com rótulos (MARCA/MODELO, CHASSI, RENAVAM, COR, ANO...), preenche
 *   também marca, modelo, chassi, cor, renavam e ano. Layouts variam, então o
 *   que não casar vira aviso.
 */
@Service
public class NfeImportService {

    // Chave de 44 dígitos (aplicado ao texto já sem espaços e pontos)
    private static final Pattern ACCESS_KEY_44 = Pattern.compile("\\d{44}");
    // VIN/chassi tem 17 caracteres e não usa I, O, Q
    private static final Pattern CHASSIS = Pattern.compile("(?i)chassi[^A-Z0-9]{0,12}([A-HJ-NPR-Z0-9]{17})");
    private static final Pattern RENAVAM = Pattern.compile("(?i)renavam[^0-9]{0,12}(\\d{5,11})");
    private static final Pattern BRAND_MODEL = Pattern.compile("(?i)marca\\s*/\\s*modelo\\s*[:\\-]?\\s*([^\\r\\n]+)");
    private static final Pattern COLOR = Pattern.compile("(?im)^\\s*cor\\s*[:\\-]\\s*([^\\r\\n]+)");
    private static final Pattern YEAR_MODEL_FAB =
            Pattern.compile("(?i)ano\\s*modelo\\s*/\\s*fabricacao\\s*[:\\-]?\\s*(\\d{4})\\s*/\\s*(\\d{4})");

    private static final Map<Integer, String> STATE_BY_CODE = Map.ofEntries(
            Map.entry(11, "RO"), Map.entry(12, "AC"), Map.entry(13, "AM"), Map.entry(14, "RR"),
            Map.entry(15, "PA"), Map.entry(16, "AP"), Map.entry(17, "TO"), Map.entry(21, "MA"),
            Map.entry(22, "PI"), Map.entry(23, "CE"), Map.entry(24, "RN"), Map.entry(25, "PB"),
            Map.entry(26, "PE"), Map.entry(27, "AL"), Map.entry(28, "SE"), Map.entry(29, "BA"),
            Map.entry(31, "MG"), Map.entry(32, "ES"), Map.entry(33, "RJ"), Map.entry(35, "SP"),
            Map.entry(41, "PR"), Map.entry(42, "SC"), Map.entry(43, "RS"), Map.entry(50, "MS"),
            Map.entry(51, "MT"), Map.entry(52, "GO"), Map.entry(53, "DF")
    );

    public NfeImportResponseDTO importByPdf(byte[] pdf) {
        String text = extractText(pdf);
        List<String> warnings = new ArrayList<>();

        String accessKey = findAccessKey(text);
        NfeAccessKeyInfo info = null;
        if (accessKey != null) {
            info = decompose(accessKey);
            if (!info.valid()) {
                warnings.add("Uma sequência de 44 dígitos foi encontrada, mas o dígito verificador não confere.");
            }
        } else {
            warnings.add("Não foi possível localizar a chave de acesso (44 dígitos) no PDF.");
        }

        CreateVehicleDTO vehicle = new CreateVehicleDTO();

        firstGroup(CHASSIS, text).ifPresent(v -> vehicle.setChassis(v.toUpperCase()));
        firstGroup(RENAVAM, text).ifPresent(vehicle::setRenavam);
        firstGroup(COLOR, text).ifPresent(v -> vehicle.setColor(v.trim()));
        firstGroup(BRAND_MODEL, text).ifPresent(v -> applyBrandModel(vehicle, v.trim()));

        Matcher yearMatcher = YEAR_MODEL_FAB.matcher(text);
        if (yearMatcher.find()) {
            String modelYear = yearMatcher.group(1);
            String manufactureYear = yearMatcher.group(2);
            vehicle.setFabricationAndModel(manufactureYear + "/" + modelYear); // fabricação/modelo
        }

        // avisa só sobre o que ficou faltando
        List<String> missing = new ArrayList<>();
        if (vehicle.getBrand() == null) missing.add("marca");
        if (vehicle.getModel() == null) missing.add("modelo");
        if (vehicle.getChassis() == null) missing.add("chassi");
        if (vehicle.getColor() == null) missing.add("cor");
        if (vehicle.getFabricationAndModel() == null) missing.add("ano fab/modelo");
        if (vehicle.getRenavam() == null) missing.add("renavam");
        if (!missing.isEmpty()) {
            warnings.add("Não localizado no PDF (confira e preencha): " + String.join(", ", missing) + ".");
        }
        warnings.add("Dados extraídos de forma automática — revise antes de salvar.");

        return new NfeImportResponseDTO(info, vehicle, warnings);
    }

    // ----- helpers -----

    // "CHEVROLET/ONIX 1.0 MT - 5A48AV" -> marca "CHEVROLET", modelo "ONIX 1.0 MT - 5A48AV"
    private void applyBrandModel(CreateVehicleDTO vehicle, String value) {
        String[] parts = value.split("/", 2);
        vehicle.setBrand(parts[0].trim());
        if (parts.length > 1 && !parts[1].isBlank()) {
            vehicle.setModel(parts[1].trim());
        }
    }

    private Optional<String> firstGroup(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    private String extractText(byte[] pdf) {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(doc);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo PDF inválido ou ilegível");
        }
    }

    /**
     * Procura uma sequência de 44 dígitos; prefere a que tem DV válido.
     * Remove espaços E pontos, porque a DANFE costuma imprimir a chave em grupos
     * separados por ponto (ex.: 4326.0903.3597...).
     */
    private String findAccessKey(String text) {
        String digits = text.replaceAll("[\\s.]", "");
        Matcher matcher = ACCESS_KEY_44.matcher(digits);
        String first = null;
        while (matcher.find()) {
            String candidate = matcher.group();
            if (first == null) {
                first = candidate;
            }
            if (isCheckDigitValid(candidate)) {
                return candidate;
            }
        }
        return first; // nenhuma com DV válido; devolve a primeira (será marcada como inválida)
    }

    private NfeAccessKeyInfo decompose(String accessKey) {
        if (accessKey == null || !accessKey.matches("\\d{44}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A chave de acesso deve ter exatamente 44 dígitos numéricos");
        }
        int stateCode = Integer.parseInt(accessKey.substring(0, 2));
        int year = 2000 + Integer.parseInt(accessKey.substring(2, 4));
        int month = Integer.parseInt(accessKey.substring(4, 6));
        String cnpj = accessKey.substring(6, 20);
        String model = accessKey.substring(20, 22);
        String series = accessKey.substring(22, 25);
        String number = accessKey.substring(25, 34);
        String checkDigit = accessKey.substring(43, 44);

        return new NfeAccessKeyInfo(
                accessKey,
                isCheckDigitValid(accessKey),
                STATE_BY_CODE.getOrDefault(stateCode, "??"),
                stateCode,
                year,
                month,
                formatCnpj(cnpj),
                model,
                series,
                String.valueOf(Integer.parseInt(number)), // remove zeros à esquerda
                checkDigit
        );
    }

    /** Dígito verificador da chave: módulo 11 sobre os 43 primeiros dígitos. */
    private boolean isCheckDigitValid(String accessKey) {
        if (accessKey.length() != 44) {
            return false;
        }
        int sum = 0;
        int weight = 2;
        for (int i = 42; i >= 0; i--) {
            sum += (accessKey.charAt(i) - '0') * weight;
            weight = (weight == 9) ? 2 : weight + 1;
        }
        int rest = sum % 11;
        int checkDigit = (rest == 0 || rest == 1) ? 0 : 11 - rest;
        return checkDigit == (accessKey.charAt(43) - '0');
    }

    private String formatCnpj(String cnpj) {
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8)
                + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
    }
}
