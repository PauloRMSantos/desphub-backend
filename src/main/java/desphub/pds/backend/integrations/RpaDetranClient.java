package desphub.pds.backend.integrations;

import desphub.pds.backend.dtos.detran.ConsultaRequest;
import desphub.pds.backend.dtos.detran.ConsultaResponse;
import desphub.pds.backend.exceptions.PortalException;
import desphub.pds.backend.exceptions.SessaoExpiradaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Component
public class RpaDetranClient {

    private static final List<String> TIPOS_PADRAO =
            List.of("DADOS_CADASTRAIS", "DEBITOS", "RESTRICOES", "LICENCIAMENTO");

    private final RestClient http;

    public RpaDetranClient(@Value("${rpa.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(60));

        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public ConsultaResponse consultar(String placa, String renavam) {
        return http.post()
                .uri("/api/detran/consultas")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ConsultaRequest(placa, renavam, TIPOS_PADRAO))
                .exchange((request, response) -> {
                    HttpStatusCode status = response.getStatusCode();

                    if (status.value() == 503) {
                        throw new SessaoExpiradaException("Conexão com o DETRAN expirou — reconecte o gov.br");
                    }

                    if (status.isError()) {
                        ConsultaResponse corpoErro;
                        try {
                            corpoErro = response.bodyTo(ConsultaResponse.class);
                        } catch (RuntimeException e) {
                            corpoErro = null;
                        }
                        throw new PortalException(corpoErro);
                    }

                    return response.bodyTo(ConsultaResponse.class);
                });
    }
}
