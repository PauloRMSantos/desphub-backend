package desphub.pds.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("DespHub API")
                        .description("API do sistema de despachante/oficina (clientes, veículos, serviços, orçamentos e ordens de serviço)")
                        .version("v1"))
                // habilita o botão "Authorize" no Swagger: cole só o token JWT (sem "Bearer ")
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // aplica o esquema a todas as rotas (o login é público, mas mandar o header não atrapalha)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
