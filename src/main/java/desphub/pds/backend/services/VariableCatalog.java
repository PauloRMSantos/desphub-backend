package desphub.pds.backend.services;

import desphub.pds.backend.dtos.templates.VariableCatalogEntryDTO;
import desphub.pds.backend.enums.VariableSource;
import desphub.pds.backend.models.Client;
import desphub.pds.backend.models.Office;
import desphub.pds.backend.models.User;
import desphub.pds.backend.models.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VariableCatalog {

    private static final List<VariableCatalogEntryDTO> CATALOG = List.of(
            new VariableCatalogEntryDTO(VariableSource.CLIENT, "name", "cliente.nome", "Nome do cliente"),
            new VariableCatalogEntryDTO(VariableSource.CLIENT, "cpfCnpj", "cliente.cpfCnpj", "CPF/CNPJ do cliente"),
            new VariableCatalogEntryDTO(VariableSource.CLIENT, "telephone", "cliente.telefone", "Telefone do cliente"),
            new VariableCatalogEntryDTO(VariableSource.CLIENT, "address", "cliente.endereco", "Endereço do cliente"),

            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "plate", "veiculo.placa", "Placa"),
            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "brand", "veiculo.marca", "Marca"),
            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "model", "veiculo.modelo", "Modelo"),
            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "fabricationAndModel", "veiculo.anoFabModelo", "Ano fab./modelo"),
            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "color", "veiculo.cor", "Cor"),
            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "renavam", "veiculo.renavam", "RENAVAM"),
            new VariableCatalogEntryDTO(VariableSource.VEHICLE, "chassis", "veiculo.chassi", "Chassi"),

            new VariableCatalogEntryDTO(VariableSource.OFFICE, "name", "escritorio.nome", "Nome do escritório"),
            new VariableCatalogEntryDTO(VariableSource.OFFICE, "cpfCnpj", "escritorio.cpfCnpj", "CPF/CNPJ do escritório"),

            new VariableCatalogEntryDTO(VariableSource.USER, "name", "despachante.nome", "Nome do despachante"),
            new VariableCatalogEntryDTO(VariableSource.USER, "email", "despachante.email", "E-mail do despachante")
    );

    public List<VariableCatalogEntryDTO> catalog() {
        return CATALOG;
    }

    public boolean isValidAutoField(VariableSource source, String field) {
        if (field == null) {
            return false;
        }
        return CATALOG.stream().anyMatch(e -> e.source() == source && e.sourceField().equals(field));
    }

    public String resolveAuto(VariableSource source, String field, Client client, Vehicle vehicle,
                              Office office, User user) {
        return switch (source) {
            case CLIENT -> client == null ? null : switch (field) {
                case "name" -> client.getName();
                case "cpfCnpj" -> client.getCpfCnpj();
                case "telephone" -> client.getTelephone();
                case "address" -> client.getAddress();
                default -> null;
            };
            case VEHICLE -> vehicle == null ? null : switch (field) {
                case "plate" -> vehicle.getPlate();
                case "brand" -> vehicle.getBrand();
                case "model" -> vehicle.getModel();
                case "fabricationAndModel" -> vehicle.getFabricationAndModel();
                case "color" -> vehicle.getColor();
                case "renavam" -> vehicle.getRenavam();
                case "chassis" -> vehicle.getChassis();
                default -> null;
            };
            case OFFICE -> office == null ? null : switch (field) {
                case "name" -> office.getName();
                case "cpfCnpj" -> office.getCpfCnpj();
                default -> null;
            };
            case USER -> user == null ? null : switch (field) {
                case "name" -> user.getName();
                case "email" -> user.getEmail();
                default -> null;
            };
            case MANUAL -> null;
        };
    }
}
