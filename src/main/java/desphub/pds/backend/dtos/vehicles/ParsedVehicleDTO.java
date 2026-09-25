package desphub.pds.backend.dtos.vehicles;

/**
 * Dados de veículo extraídos de um CRLV-e ou ATPV-e (documentos nacionais SENATRAN).
 * Serve para pré-preencher o formulário de cadastro; o usuário confere antes de salvar.
 */
public record ParsedVehicleDTO(
        String documentType, // "CRLV_E" ou "ATPV_E"
        String plate,
        String brand,
        String model,
        String fabricationAndModel,
        String color,
        String renavam,
        String chassis
) {
}
