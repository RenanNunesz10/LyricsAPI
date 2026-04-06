package renan.dws.Lyrics.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renan.dws.Lyrics.entities.Genre;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.GenreRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="Gêneros Musicais", description = "Lista de Gêneros Musicais")
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreRepository genreRepository;
    private final PagedResourcesAssembler<Genre> pagedResourcesAssembler;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public GenreController(GenreRepository genreRepository, PagedResourcesAssembler<Genre> pagedResourcesAssembler) {
        this.genreRepository = genreRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
            summary = "Lista todos os gêneros",
            description = "Retorna uma lista paginada de gêneros musicais. A resposta inclui links de navegação **HATEOAS** para transitar entre as páginas de resultados."
    )
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Genre>>> getAllGenres(@ParameterObject Pageable pageable) {
        var genres = genreRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(genres));
    }

    @Operation(
            summary = "Busca um gênero por ID",
            description = "Recupera os detalhes de um gênero musical pelo seu identificador único. Inclui links **HATEOAS** apontando para o próprio recurso (`self`) e para a lista geral de gêneros (`genres`)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gênero encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Genre.class)) }),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public EntityModel<Genre> getGenreById(@PathVariable long id) {
        var genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado com ID: " + id));

        return EntityModel.of(genre,
                linkTo(methodOn(GenreController.class).getGenreById(id)).withSelfRel(),
                linkTo(methodOn(GenreController.class).getAllGenres(Pageable.unpaged())).withRel("genres"));
    }

    @Operation(
            summary = "Busca um gênero específico pelo nome",
            description = "Realiza uma busca exata pelo nome do gênero (ignorando maiúsculas e minúsculas). Como a regra de negócio define nomes únicos no banco, retorna um único recurso com controles **HATEOAS**."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gênero encontrado"),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado", content = @Content)
    })
    @GetMapping("/search/name")
    public EntityModel<Genre> getGenreByName(@RequestParam String name) {
        var genre = genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado com o nome: " + name));

        return EntityModel.of(genre,
                linkTo(methodOn(GenreController.class).getGenreByName(name)).withSelfRel(),
                linkTo(methodOn(GenreController.class).getAllGenres(Pageable.unpaged())).withRel("genres"));
    }

    @Operation(
            summary = "Cria um novo gênero",
            description = "Cadastra um novo gênero musical no banco de dados. Retorna o objeto criado e a URI de acesso no cabeçalho `Location`."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gênero criado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Genre.class)) }),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Nome vazio", content = @Content) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Genre> createGenre(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do gênero a ser criado", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Genre.class),
                            examples = @ExampleObject(value = "{ \"name\": \"Rock\" }")))
            @Valid @RequestBody Genre newGenre) {
        genreRepository.save(newGenre);
        return ResponseEntity.created(URI.create("/genres/" + newGenre.getId())).body(newGenre);
    }

    @Operation(
            summary = "Atualiza um gênero existente",
            description = "Atualiza o nome de um gênero musical. Utiliza a lógica de **Upsert**: caso o ID informado não exista, um novo gênero será criado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gênero atualizado com sucesso"),
            @ApiResponse(responseCode = "201", description = "Novo gênero criado com sucesso (ID não existia)"),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Nome vazio", content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do gênero para atualizar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Genre.class),
                            examples = @ExampleObject(value = "{ \"name\": \"Rock Clássico\" }")))
            @Valid @RequestBody Genre updatedGenre) {
        return genreRepository.findById(id).map(genre -> {
            genre.setName(updatedGenre.getName());
            return ResponseEntity.ok(genreRepository.save(genre));
        }).orElseGet(() -> ResponseEntity.created(URI.create("/genres/" + updatedGenre.getId()))
                .body(genreRepository.save(updatedGenre)));
    }

    @Operation(
            summary = "Deleta um gênero pelo ID",
            description = "Remove um gênero do banco de dados pelo seu ID. Retorna status `204 No Content` em caso de sucesso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gênero deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable long id) {
        if (!genreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        genreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}