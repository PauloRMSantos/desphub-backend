package desphub.pds.backend.exceptions;

import desphub.pds.backend.dtos.detran.VehicleQueryResponse;

public class PortalException extends RuntimeException {

    private final transient VehicleQueryResponse response;

    public PortalException(VehicleQueryResponse response) {
        super(buildMessage(response));
        this.response = response;
    }

    public VehicleQueryResponse getResponse() {
        return response;
    }

    private static String buildMessage(VehicleQueryResponse response) {
        if (response != null && response.errors() != null && !response.errors().isEmpty()) {
            var error = response.errors().get(0);
            return "Falha no portal DETRAN (" + error.step() + "): " + error.message();
        }
        return "Falha ao consultar o portal DETRAN";
    }
}
