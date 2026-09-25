package desphub.pds.backend.dtos.detran;

/** Característica especial do veículo (ex.: "Recuperado de sinistro"). Valores vêm do portal (PT). */
public record SpecialCharacteristic(
        String description,
        String origin,
        String code,
        String startDate
) {
}
