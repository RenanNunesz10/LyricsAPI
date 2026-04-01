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
import renan.dws.Lyrics.entities.Album;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.AlbumRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="Álbum", description = "Lista de álbuns")
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final PagedResourcesAssembler<Album> pagedResourcesAssembler;

    @Autowired
    public AlbumController(AlbumRepository albumRepository, PagedResourcesAssembler<Album> pagedResourcesAssembler) {
        this.albumRepository = albumRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todos os álbuns", description = "Retorna uma lista paginada de todos os álbuns cadastrados no banco.")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Album>>> getAllAlbums(@ParameterObject Pageable pageable) {
        var albums = albumRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(albums));
    }

    @Operation(summary = "Busca um álbum por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Album.class)) }),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public EntityModel<Album> getAlbumById(@PathVariable long id) {
        var album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));

        return EntityModel.of(album,
                linkTo(methodOn(AlbumController.class).getAlbumById(id)).withSelfRel(),
                linkTo(methodOn(AlbumController.class).getAllAlbums(Pageable.unpaged())).withRel("albums"));
    }

    @Operation(summary = "Busca álbuns pelo ano de lançamento")
    @GetMapping("/search/year")
    public ResponseEntity<PagedModel<EntityModel<Album>>> getAlbumsByYear(
            @RequestParam int year, @ParameterObject Pageable pageable) {
        var albums = albumRepository.findByReleaseYear(year, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(albums));
    }

    @Operation(summary = "Cria um novo álbum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Álbum criado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Album.class)) }),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Título vazio ou ano inválido", content = @Content) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Album> createAlbum(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do álbum a ser criado", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Album.class),
                            examples = @ExampleObject(value = "{ \"title\": \"A Night at the Opera\", \"releaseYear\": 1975 }")))
            @Valid @RequestBody Album newAlbum) {

        albumRepository.save(newAlbum);
        return ResponseEntity.created(URI.create("/albums/" + newAlbum.getId())).body(newAlbum);
    }

    @Operation(summary = "Atualiza um álbum existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum atualizado com sucesso"),
            @ApiResponse(responseCode = "201", description = "Novo álbum criado com sucesso (ID não existia)"),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Título vazio ou ano inválido", content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do álbum para atualizar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Album.class),
                            examples = @ExampleObject(value = "{ \"title\": \"A Night at the Opera (Remastered)\", \"releaseYear\": 1975 }")))
            @Valid @RequestBody Album updatedAlbum) {

        return albumRepository.findById(id).map(album -> {
            album.setTitle(updatedAlbum.getTitle());
            album.setReleaseYear(updatedAlbum.getReleaseYear());
            return ResponseEntity.ok(albumRepository.save(album));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/albums/" + updatedAlbum.getId()))
                    .body(albumRepository.save(updatedAlbum));
        });
    }

    @Operation(summary = "Deleta um álbum pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Álbum deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable long id) {
        if (!albumRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        albumRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}