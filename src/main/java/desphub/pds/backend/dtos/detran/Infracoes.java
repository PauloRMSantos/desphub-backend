package desphub.pds.backend.dtos.detran;

public record Infracoes(
        ResumoInfracao aVencer,
        ResumoInfracao vencidas,
        ResumoInfracao suspensas,
        ResumoInfracao aguardandoPrazoDefesa,
        ResumoInfracao aguardandoJulgamento
) {
}
