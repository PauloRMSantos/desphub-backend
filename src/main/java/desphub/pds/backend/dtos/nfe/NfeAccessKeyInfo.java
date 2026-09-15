package desphub.pds.backend.dtos.nfe;

/** Dados que a própria chave de acesso de 44 dígitos carrega (decompostos). */
public record NfeAccessKeyInfo(
        String accessKey,
        boolean valid,
        String state,
        int stateCode,
        int issueYear,
        int issueMonth,
        String issuerCnpj,       // formatado 00.000.000/0000-00
        String model,            // "55" = NF-e
        String series,
        String invoiceNumber,
        String checkDigit
) {
}
