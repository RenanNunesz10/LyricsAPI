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
                        .title("🎵 Lyrics API - Catálogo Musical")
                        .version("v1.0.0")
                        .description("""
                                **API RESTful desenvolvida em Spring Boot para o gerenciamento completo de um catálogo musical.**
                                
                                O sistema permite realizar operações de CRUD (Create, Read, Update, Delete) com paginação e buscas personalizadas para Artistas, Álbuns, Músicas, Gêneros e Letras.
                                
                                ---
                                
                                ### 🚀 Nível 3 de Maturidade Richardson (HATEOAS)
                                
                                O grande diferencial desta API é a sua aderência estrita ao padrão arquitetural **HATEOAS** (*Hypermedia as the Engine of Application State*).
                                
                                Diferente de APIs REST convencionais que retornam apenas dados brutos, esta API fornece **controles hipermídia (`_links`)** em todas as suas respostas, permitindo que o cliente (front-end) navegue dinamicamente pelos recursos disponíveis.
                                
                                **Características da Implementação HATEOAS:**
                                * **Auto-descritiva e Navegável:** Toda consulta a um recurso único (ex: buscar um álbum por ID) retorna não apenas os dados do álbum, mas também um link `self` apontando para a própria URI do recurso, além de links de contexto (ex: link para voltar à lista geral de álbuns).
                                * **Paginação Dinâmica:** Listagens de dados utilizam o `PagedResourcesAssembler` do Spring HATEOAS. Isso injeta automaticamente links de navegação entre as páginas (como `first`, `prev`, `next`, e `last`), abolindo a necessidade do cliente calcular a paginação manualmente.
                                * **Desacoplamento de Rotas:** Como os endpoints fornecem as URIs exatas do que o cliente pode fazer a seguir, evita-se o "hardcode" de URLs no lado do consumidor da API, tornando o sistema altamente resiliente a futuras mudanças de rotas.
                                """)
                        .contact(new Contact()
                                .name("Renan Nunes")
                                .email("seu.email@exemplo.com")
                                .url("https://github.com/RenanNunesz10"))
                );
    }
}