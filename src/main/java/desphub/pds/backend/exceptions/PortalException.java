package desphub.pds.backend.exceptions;

import desphub.pds.backend.dtos.detran.ConsultaResponse;

public class PortalException extends RuntimeException {

    private final transient ConsultaResponse resposta;

    public PortalException(ConsultaResponse resposta) {
        super(mensagemDe(resposta));
        this.resposta = resposta;
    }

    public ConsultaResponse getResposta() {
        return resposta;
    }

    private static String mensagemDe(ConsultaResponse r) {
        if (r != null && r.erros() != null && !r.erros().isEmpty()) {
            var erro = r.erros().get(0);
            return "Falha no portal DETRAN (" + erro.etapa() + "): " + erro.mensagem();
        }
        return "Falha ao consultar o portal DETRAN";
    }
}
