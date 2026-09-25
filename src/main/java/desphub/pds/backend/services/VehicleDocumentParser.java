package desphub.pds.backend.services;

import desphub.pds.backend.dtos.vehicles.ParsedVehicleDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai dados de veículo de CRLV-e e ATPV-e (layouts nacionais SENATRAN, versão 2.1).
 *
 * <p>Estratégia determinística ancorada por posição: nesses documentos cada rótulo fica
 * diretamente ACIMA do seu valor, com sobreposição no eixo x. Para cada campo, achamos o
 * rótulo e pegamos o valor na primeira linha abaixo cujo x se sobrepõe ao do rótulo. Isso é
 * necessário porque valores puramente por regex são ambíguos (ex.: no ATPV-e o RENAVAM e o
 * código de segurança do CRV têm ambos 11 dígitos).
 */
@Service
public class VehicleDocumentParser {

    /** Distância vertical máxima (pt) entre um rótulo e o valor logo abaixo. */
    private static final double MAX_GAP = 30;
    /** Tolerância (pt) para considerar dois fragmentos na mesma linha. */
    private static final double SAME_ROW = 3;

    // Chassi: VIN de 17 caracteres (sem I, O, Q). Fallback quando o rótulo falhar.
    private static final Pattern VIN = Pattern.compile("\\b[A-HJ-NPR-Z0-9]{17}\\b");

    private record Item(String text, double y, double xStart, double xEnd) {
    }

    public ParsedVehicleDTO parse(byte[] pdfBytes) {
        List<Item> items = extractFirstPage(pdfBytes);

        boolean isAtpv = items.stream().anyMatch(i -> norm(i.text()).contains("AUTORIZACAO PARA TRANSFERENCIA"));
        boolean isCrlv = items.stream().anyMatch(i -> norm(i.text()).contains("CERTIFICADO DE REGISTRO E LICENCIAMENTO"));

        if (!isAtpv && !isCrlv) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Documento não reconhecido. Envie um CRLV-e ou ATPV-e (PDF digital do SENATRAN).");
        }

        String type = isAtpv ? "ATPV_E" : "CRLV_E";

        String plate = clean(valueBelow(items, exact("PLACA")));
        String renavam = digits(valueBelow(items, contains("CODIGO RENAVAM")));
        String makeModel = trimOrNull(valueBelow(items, contains("MARCA") .and(contains("MODELO"))));
        String color = trimOrNull(valueBelow(items, contains("COR PREDOMINANTE")));
        String fabYear = digits(valueBelow(items, contains("ANO FABRICACAO")));
        String modelYear = digits(valueBelow(items, contains("ANO MODELO")));
        String chassis = cleanChassis(valueBelow(items, exact("CHASSI")));

        if (chassis == null) {
            chassis = fallbackChassis(items);
        }

        String brand = null;
        String model = null;
        if (makeModel != null) {
            int slash = makeModel.indexOf('/');
            if (slash >= 0) {
                brand = trimOrNull(makeModel.substring(0, slash));
                model = trimOrNull(makeModel.substring(slash + 1));
            } else {
                brand = makeModel;
            }
        }

        String fabricationAndModel = null;
        if (fabYear != null && modelYear != null) {
            fabricationAndModel = fabYear + "/" + modelYear;
        } else if (fabYear != null) {
            fabricationAndModel = fabYear;
        } else if (modelYear != null) {
            fabricationAndModel = modelYear;
        }

        return new ParsedVehicleDTO(type, plate, brand, model, fabricationAndModel, color, renavam, chassis);
    }

    // ---- localização rótulo -> valor abaixo -----------------------------------------------------

    private String valueBelow(List<Item> items, Predicate<String> labelMatches) {
        Optional<Item> label = items.stream()
                .filter(i -> labelMatches.test(norm(i.text())))
                .findFirst();
        if (label.isEmpty()) {
            return null;
        }
        Item l = label.get();

        // candidatos: fragmentos abaixo do rótulo, dentro do gap, com sobreposição em x
        List<Item> below = items.stream()
                .filter(i -> i.y() > l.y() + 1 && i.y() <= l.y() + MAX_GAP)
                .filter(i -> overlapsX(i, l))
                .sorted((a, b) -> Double.compare(a.y(), b.y()))
                .toList();
        if (below.isEmpty()) {
            return null;
        }

        double rowY = below.get(0).y();
        return below.stream()
                .filter(i -> Math.abs(i.y() - rowY) <= SAME_ROW)
                .sorted((a, b) -> Double.compare(a.xStart(), b.xStart()))
                .map(Item::text)
                .reduce((a, b) -> a + " " + b)
                .map(String::trim)
                .orElse(null);
    }

    private static boolean overlapsX(Item value, Item label) {
        return value.xEnd() > label.xStart() && value.xStart() < label.xEnd();
    }

    private String fallbackChassis(List<Item> items) {
        for (Item i : items) {
            Matcher m = VIN.matcher(i.text().replaceAll("\\s", "").toUpperCase());
            if (m.find()) {
                return m.group();
            }
        }
        return null;
    }

    // ---- PDF -> fragmentos com coordenadas ------------------------------------------------------

    private List<Item> extractFirstPage(byte[] pdfBytes) {
        List<Item> items = new ArrayList<>();
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String text, List<TextPosition> tps) {
                    if (tps.isEmpty() || text.isBlank()) {
                        return;
                    }
                    TextPosition first = tps.get(0);
                    TextPosition last = tps.get(tps.size() - 1);
                    items.add(new Item(
                            text.trim(),
                            first.getYDirAdj(),
                            first.getXDirAdj(),
                            last.getXDirAdj() + last.getWidthDirAdj()));
                }
            };
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(1); // tudo que precisamos está na primeira página
            stripper.getText(doc);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Não foi possível ler o PDF enviado.", e);
        }
        return items;
    }

    // ---- normalização / limpeza -----------------------------------------------------------------

    private static Predicate<String> contains(String normNeedle) {
        return s -> s.contains(normNeedle);
    }

    private static Predicate<String> exact(String normValue) {
        return s -> s.equals(normValue);
    }

    /** Uppercase, sem acento, espaços colapsados. */
    private static String norm(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toUpperCase().replaceAll("\\s+", " ").trim();
    }

    private static String trimOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() || t.equals("***") ? null : t;
    }

    /** Placa: uppercase sem espaços/hífens. */
    private static String clean(String s) {
        String t = trimOrNull(s);
        return t == null ? null : t.toUpperCase().replaceAll("[\\s-]", "");
    }

    private static String cleanChassis(String s) {
        String t = trimOrNull(s);
        return t == null ? null : t.toUpperCase().replaceAll("\\s", "");
    }

    /** Mantém só dígitos (preserva zeros à esquerda do RENAVAM); null se não sobrar nada. */
    private static String digits(String s) {
        String t = trimOrNull(s);
        if (t == null) {
            return null;
        }
        String d = t.replaceAll("\\D", "");
        return d.isEmpty() ? null : d;
    }
}
