package renan.dws.Lyrics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "ApiKeyAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Lyrics API")
                        .version("v1.0.0")
                        .description("""
                                 RESTful API for Comprehensive Music Catalog Management.
                                 
                                 Este projeto foi desenvolvido como requisito acadêmico, aplicando modelagem de dados avançada e boas práticas estritas de arquitetura REST.
                                \s
                                 🚀 **Key Features**
                                \s
                                 * **Richardson Maturity Model Level 3:** Implementação completa de HATEOAS para descobrimento de recursos.
                                 * **Pagination:** Recuperação de dados otimizada utilizando Spring Data Pageable.
                                 * **Relational Integrity:** Mapeamentos relacionais gerenciados pelo Hibernate (Songs, Artists, Albums).
                                \s
                                 🛠️ **Tech Stack**
                                \s
                                 **Java 25 | Spring Boot 4.0 | Spring Data JPA | H2 Database | Springdoc OpenAPI**
                                \s
                                 🔐 **Authentication (API Key)**
                                \s
                                 Esta API opera de forma *stateless*. Todos os endpoints operacionais exigem o envio do cabeçalho `X-API-Key` em cada requisição. Endpoints públicos, como a documentação do Swagger ou a rota de geração `/api-keys`, estão isentos. Requisições não autenticadas receberão uma resposta `401 Unauthorized`.
                                \s
                                 🔄 **Idempotency**
                                \s
                                 Para garantir retentativas seguras em redes distribuídas, rotas de criação (`POST`) aplicam lógica estrita de idempotência. Os clientes devem fornecer um `X-Idempotency-Key` único no cabeçalho da requisição. Se o mesmo payload for submetido com uma chave existente, o servidor retornará `409 Conflict`.
                                \s
                                 🔀 **API Versioning**
                                \s
                                 A API utiliza cabeçalhos HTTP para versionamento (`X-API-Version`). Por padrão, as requisições são processadas como versão 1. Certos endpoints (como `/songs`) possuem uma implementação de Versão 2 oferecendo estruturas de payload diferentes. Envie `X-API-Version: 2` nos seus cabeçalhos para acessar essas representações.
                                \s
                                 ⏱️ **Rate Limits (Bucket4j)**
                                \s
                                 A Lyrics API implementa *rate limiting* estrito baseado em IP para prevenir abusos e garantir alta disponibilidade. A aplicação utiliza um algoritmo de *token bucket*:
                                \s
                                 * **Global Limits:** 5 requisições por minuto por IP.
                                \s
                                 Submeter requisições excessivas ao servidor resultará em um código de status HTTP `429 Too Many Requests`. Continuar a sobrecarregar a API fará com que o acesso permaneça bloqueado até o tempo expirar (indicado pelo cabeçalho `Retry-After`).
                                 """)
                        .contact(new Contact()
                                .name("Renan Nunes")
                                .email("renan5248@gmail.com")
                                .url("https://github.com/RenanNunesz10"))
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("X-API-Key")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}