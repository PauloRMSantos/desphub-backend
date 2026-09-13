package desphub.pds.backend.dtos.detran;

public record Veiculo(
        String placa,
        String renavam,
        String chassi,
        String marcaModelo,
        Integer anoFabricacao,
        Integer anoModelo,
        String cor,
        String tipo,
        String especie,
        String categoria,
        String municipio,
        String ufPlaca,
        String combustivel,
        String situacaoRenavam,
        String cpfProprietario
) {
}
