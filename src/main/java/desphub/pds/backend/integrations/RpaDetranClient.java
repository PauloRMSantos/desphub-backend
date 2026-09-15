package desphub.pds.backend.integrations;

import desphub.pds.backend.dtos.detran.VehicleQueryRequest;
import desphub.pds.backend.dtos.detran.VehicleQueryResponse;
import desphub.pds.backend.exceptions.PortalException;
import desphub.pds.backend.exceptions.SessionExpiredException;
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

    private static final List<String> DEFAULT_TYPES =
            List.of("REGISTRATION", "DEBTS", "RESTRICTIONS", "LICENSING");

    private final RestClient http;

    public RpaDetranClient(@Value("${rpa.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(60)); // a consulta pode demorar

        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public VehicleQueryResponse query(String plate, String renavam) {
        return http.post()
                .uri("/api/detran/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new VehicleQueryRequest(plate, renavam, DEFAULT_TYPES))
                .exchange((request, response) -> {
                    HttpStatusCode status = response.getStatusCode();

                    if (status.value() == 503) {
                        throw new SessionExpiredException("Conexão com o DETRAN expirou — reconecte o gov.br");
                    }

                    if (status.isError()) {
                        VehicleQueryResponse errorBody;
                        try {
                            errorBody = response.bodyTo(VehicleQueryResponse.class);
                        } catch (RuntimeException e) {
                            errorBody = null;
                        }
                        throw new PortalException(errorBody);
                    }

                    return response.bodyTo(VehicleQueryResponse.class);
                });
    }
}
