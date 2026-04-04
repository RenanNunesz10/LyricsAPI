# 🎵 API de Catálogo Musical (Lyrics API)

Uma API RESTful desenvolvida em **Spring Boot** para gerenciar um catálogo musical completo. O sistema permite o gerenciamento de artistas, álbuns, músicas, gêneros musicais e letras, aplicando os principais conceitos de arquitetura REST, validação de dados, paginação e documentação.

## 🔗 Acesso Rápido
* **Aplicação em Produção (Render):** https://lyricsapi-ksfc.onrender.com/
* **Documentação Swagger (Online):** https://lyricsapi-ksfc.onrender.com/swagger-ui/index.html#/

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework:** Spring Boot (Web, Data JPA, Validation, HATEOAS)
* **Banco de Dados:** H2 Database (In-Memory)
* **Documentação:** Springdoc OpenAPI (Swagger UI)
* **Hospedagem/Deploy:** Render

---

## ✨ Funcionalidades e Requisitos Atendidos

Este projeto foi feito com o objetivo de ser um backend completo, seguindo os conceitos abaixo:

* **Arquitetura em Camadas:** Código organizado de forma limpa (`controllers`, `entities`, `repositories`, `exceptions`).
* **CRUD Completo:** Operações de Create, Read, Update e Delete para 5 entidades principais: `Artist`, `Album`, `Song`, `Genre` e `LyricsDetails`.
* **Relacionamento de entidades:** Uso do Spring Data JPA para mapear relações `One-to-One`, `One-to-Many` e `Many-to-Many`.
* **Tratamento de Erros e Validação:** Uso rigoroso de **Bean Validation** (`@NotNull`, `@NotBlank`, etc.) blindando a API contra dados inválidos. Erros são interceptados por um `GlobalExceptionHandler` que retorna status HTTP adequados (ex: `400 Bad Request`, `404 Not Found`) e mensagens em JSON limpo.
* **Consultas Customizadas e Paginação:** Uso de `Pageable` em todas as rotas de listagem, além de endpoints de busca customizados (por nome, ano de lançamento, nacionalidade, etc).
* **HATEOAS:** As respostas da API incluem links navegáveis gerados dinamicamente para facilitar a interação do cliente com os recursos.
* **Documentação Completa:** Interface gráfica (Swagger) detalhando rotas, parâmetros, exemplos práticos de JSON e respostas esperadas.