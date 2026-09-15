package desphub.pds.backend.dtos.detran;

public record VehicleData(
        String plate,
        String renavam,
        String chassis,
        String makeModel,
        Integer manufactureYear,
        Integer modelYear,
        String color,
        String type,
        String species,
        String category,
        String city,
        String plateState,
        String fuel,
        String renavamStatus,
        String ownerCpf
) {
}
