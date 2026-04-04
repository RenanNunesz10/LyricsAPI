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
                               \s
                                Este projeto acadêmico implementa as melhores práticas de desenvolvimento back-end, incluindo arquitetura em camadas, relacionamentos complexos de banco de dados (ORM via JPA), validação de dados rigorosa (Bean Validation) e tratamento global de exceções.
                               \s
                                ---
                               \s
                                ### 🚀 Nível 3 de Maturidade de Richardson (HATEOAS)
                               \s
                                O grande diferencial arquitetural desta API é a sua aderência estrita ao Padrão **HATEOAS** (*Hypermedia as the Engine of Application State*), atingindo a glória máxima do Modelo de Maturidade de Richardson para Web Services RESTful.
                               \s
                                Diferente de APIs transacionais simples, esta aplicação é **Descobrível (Discoverable)**. Ela fornece **controles hipermídia (`_links`)** em todas as suas respostas, permitindo que o front-end navegue dinamicamente pela API.
                               \s
                                **Detalhes da Implementação:**
                                * **Links Contextuais (`self` e `rel`):** Cada recurso consultado devolve a sua própria URI (`self`) e links lógicos associados (ex: retornar à listagem geral `albums`, `artists`).
                                * **Paginação Hipermídia:** A API utiliza o `PagedResourcesAssembler` em todas as rotas `GET` de listagem. Além de paginar o banco de dados via `Pageable` para alta performance, o Spring injeta automaticamente links de paginação (`first`, `prev`, `next`, `last`) na resposta.
                                * **Desacoplamento Front/Back:** O cliente não precisa construir URLs localmente (hardcoding). A própria API dita os caminhos possíveis, tornando o sistema resiliente a mudanças de rotas futuras.
                               \s
                                ---
                               \s
                                ### 🗂️ Entidades e Operações CRUD
                               \s
                                O domínio da aplicação é composto por 5 entidades principais. Todas possuem suporte completo a operações **CRUD** (`POST`, `GET`, `PUT`, `DELETE`), proteção contra dados nulos/inválidos e rotas de busca customizadas:
                               \s
                                * 👨‍🎤 **Artist (Artistas):** Representa os intérpretes. Possui relacionamento `1:N` com Músicas.
                                  * *Busca Customizada:* `/artists/search/nationality` (Busca filtrada por nacionalidade, ignorando *case sensitive*).
                                 \s
                                * 💿 **Album (Álbuns):** Agrupa diversas músicas. Relacionamento `1:N` com Músicas.
                                  * *Busca Customizada:* `/albums/search/year` (Busca álbuns pelo ano de lançamento).
                                 \s
                                * 🎸 **Genre (Gêneros Musicais):** Classificação das músicas. Possui restrição de unicidade no banco (nomes únicos) e relacionamento `N:M` (Muitos para Muitos) com Músicas.
                                  * *Busca Customizada:* `/genres/search/name` (Busca exata por nome do gênero).
                                 \s
                                * 🎶 **Song (Músicas):** A entidade central que liga Artistas, Álbuns e Gêneros. Contém validação por `Enum` para o idioma.
                                  * *Busca Customizada:* `/songs/search/language` (Filtra músicas por idioma definido no Enum).
                                 \s
                                * 📝 **LyricsDetails (Letras):** Armazena os textos longos (`TEXT` no banco) das letras e compositores originais. Possui relacionamento `1:1` exclusivo com Músicas.
                                  * *Busca Customizada:* `/lyrics-details/search/writer` (Busca trechos de compositores originais).
                               \s""")
                        .contact(new Contact()
                                .name("Renan Nunes")
                                .email("seu.email@exemplo.com")
                                .url("https://github.com/RenanNunesz10"))
                );
    }
}