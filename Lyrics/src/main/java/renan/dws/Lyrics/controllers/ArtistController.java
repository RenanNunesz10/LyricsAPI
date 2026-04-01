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
import renan.dws.Lyrics.entities.Artist;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.ArtistRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="Artistas", description = "Lista de Artistas")
@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistRepository artistRepository;
    private final PagedResourcesAssembler<Artist> pagedResourcesAssembler;

    @Autowired
    public ArtistController(ArtistRepository artistRepository, PagedResourcesAssembler<Artist> pagedResourcesAssembler) {
        this.artistRepository = artistRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todos os artistas", description = "Retorna uma lista paginada de todos os artistas cadastrados.")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Artist>>> getAllArtists(@ParameterObject Pageable pageable) {
        var artists = artistRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(artists));
    }

    @Operation(summary = "Busca um artista por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class)) }),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public EntityModel<Artist> getArtistById(@PathVariable long id) {
        var artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com ID: " + id));

        return EntityModel.of(artist,
                linkTo(methodOn(ArtistController.class).getArtistById(id)).withSelfRel(),
                linkTo(methodOn(ArtistController.class).getAllArtists(Pageable.unpaged())).withRel("artists"));
    }

    @Operation(summary = "Busca artistas pela nacionalidade")
    @GetMapping("/search/nationality")
    public ResponseEntity<PagedModel<EntityModel<Artist>>> getArtistsByNationality(
            @RequestParam String nationality, @ParameterObject Pageable pageable) {
        var artists = artistRepository.findByNationalityContainingIgnoreCase(nationality, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(artists));
    }

    @Operation(summary = "Cria um novo artista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artista criado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class)) }),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Nome do artista vazio", content = @Content) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Artist> createArtist(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do artista a ser criado", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = @ExampleObject(value = "{ \"name\": \"Metallica\", \"nationality\": \"Estadunidense\" }")))
            @Valid @RequestBody Artist newArtist) {
        artistRepository.save(newArtist);
        return ResponseEntity.created(URI.create("/artists/" + newArtist.getId())).body(newArtist);
    }

    @Operation(summary = "Atualiza um artista existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso"),
            @ApiResponse(responseCode = "201", description = "Novo artista criado com sucesso (ID não existia)"),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Nome do artista vazio", content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do artista para atualizar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = @ExampleObject(value = "{ \"name\": \"Metallica\", \"nationality\": \"EUA\" }")))
            @Valid @RequestBody Artist updatedArtist) {
        return artistRepository.findById(id).map(artist -> {
            artist.setName(updatedArtist.getName());
            artist.setNationality(updatedArtist.getNationality());
            return ResponseEntity.ok(artistRepository.save(artist));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/artists/" + updatedArtist.getId()))
                    .body(artistRepository.save(updatedArtist));
        });
    }

    @Operation(summary = "Deleta um artista pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artista deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable long id) {
        if (!artistRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        artistRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}