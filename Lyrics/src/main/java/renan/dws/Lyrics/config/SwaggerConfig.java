package renan.dws.Lyrics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lyrics API")
                        .version("v1.0.0")
                        .description("""
                                 **API RESTful desenvolvida em Spring Boot para o gerenciamento completo de um catálogo musical.**
                                \s
                                 ---
                                \s
                                 **Arquitetura HATEOAS**
                                
                                 * **Links Contextuais:** Cada recurso devolve sua própria URI (`self`) e atalhos lógicos para ações relacionadas.
                                 * **Paginação Automática:** Endpoints de listagem injetam os links de navegação (`first`, `prev`, `next`, `last`) direto na resposta.
                                 * **Baixo Acoplamento:** Elimina a necessidade de *hardcoding* de URLs no front-end, já que a própria API guia os próximos passos.\s
                                 ---
                                \s
                                 **CRUD completo:**
                                \s
                                  Todas as entidades possuem suporte completo a operações CRUD (POST, GET, PUT, DELETE).
                                  \s""")
                        .contact(new Contact()
                                .name("Renan Nunes")
                                .email("renan5248@gmail.com")
                                .url("https://github.com/RenanNunesz10"))
                );
    }
}