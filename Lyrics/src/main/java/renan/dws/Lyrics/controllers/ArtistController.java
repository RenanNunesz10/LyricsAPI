package renan.dws.Lyrics.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

@Tag(name="artists", description = "Rotas de Artistas")
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

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Artist>>> getAllArtists(@ParameterObject Pageable pageable) {
        var artists = artistRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(artists));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista encontrado"),
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

    @GetMapping("/search/nationality")
    public ResponseEntity<PagedModel<EntityModel<Artist>>> getArtistsByNationality(
            @RequestParam String nationality, @ParameterObject Pageable pageable) {
        var artists = artistRepository.findByNationalityContainingIgnoreCase(nationality, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(artists));
    }

    @PostMapping
    public ResponseEntity<Artist> createArtist(@Valid @RequestBody Artist newArtist) {
        artistRepository.save(newArtist);
        return ResponseEntity.created(URI.create("/artists/" + newArtist.getId())).body(newArtist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable long id, @Valid @RequestBody Artist updatedArtist) {
        return artistRepository.findById(id).map(artist -> {
            artist.setName(updatedArtist.getName());
            artist.setNationality(updatedArtist.getNationality());
            return ResponseEntity.ok(artistRepository.save(artist));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/artists/" + updatedArtist.getId()))
                    .body(artistRepository.save(updatedArtist));
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable long id) {
        if (!artistRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        artistRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}