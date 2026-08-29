package desphub.pds.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("DespHub API")
                .description("API do sistema de despachante/oficina (clientes, veículos, serviços, orçamentos e ordens de serviço)")
                .version("v1"));
    }
}
