package renan.dws.Lyrics.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renan.dws.Lyrics.entities.Album;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.AlbumRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="albums", description = "Rotas de Álbuns")
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

    @Operation(summary = "Buscar todos os álbuns")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Album>>> getAllAlbums(@ParameterObject Pageable pageable) {
        var albums = albumRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(albums));
    }

    @Operation(summary = "Buscar álbum por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum encontrado"),
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

    // --- REQUISITO: CONSULTA PERSONALIZADA ---
    @Operation(summary = "Buscar álbuns por ano de lançamento")
    @GetMapping("/search/year")
    public ResponseEntity<PagedModel<EntityModel<Album>>> getAlbumsByYear(
            @RequestParam int year, @ParameterObject Pageable pageable) {
        var albums = albumRepository.findByReleaseYear(year, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(albums));
    }

    @Operation(summary = "Criar um novo álbum")
    @PostMapping
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody Album newAlbum) {
        albumRepository.save(newAlbum);
        return ResponseEntity.created(URI.create("/albums/" + newAlbum.getId())).body(newAlbum);
    }

    @Operation(summary = "Atualizar um álbum existente")
    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable long id, @Valid @RequestBody Album updatedAlbum) {
        return albumRepository.findById(id).map(album -> {
            album.setTitle(updatedAlbum.getTitle());
            album.setReleaseYear(updatedAlbum.getReleaseYear());
            return ResponseEntity.ok(albumRepository.save(album));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/albums/" + updatedAlbum.getId()))
                    .body(albumRepository.save(updatedAlbum));
        });
    }

    @Operation(summary = "Deletar um álbum")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable long id) {
        if (!albumRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        albumRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}