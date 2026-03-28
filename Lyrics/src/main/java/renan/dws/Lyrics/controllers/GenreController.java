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
import renan.dws.Lyrics.entities.Genre;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.GenreRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="genres", description = "Rotas de Gêneros Musicais")
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreRepository genreRepository;
    private final PagedResourcesAssembler<Genre> pagedResourcesAssembler;

    @Autowired
    public GenreController(GenreRepository genreRepository, PagedResourcesAssembler<Genre> pagedResourcesAssembler) {
        this.genreRepository = genreRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Genre>>> getAllGenres(@ParameterObject Pageable pageable) {
        var genres = genreRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(genres));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gênero encontrado"),
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

    @GetMapping("/search/name")
    public EntityModel<Genre> getGenreByName(@RequestParam String name) {
        var genre = genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado com o nome: " + name));

        return EntityModel.of(genre,
                linkTo(methodOn(GenreController.class).getGenreByName(name)).withSelfRel(),
                linkTo(methodOn(GenreController.class).getAllGenres(Pageable.unpaged())).withRel("genres"));
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody Genre newGenre) {
        genreRepository.save(newGenre);
        return ResponseEntity.created(URI.create("/genres/" + newGenre.getId())).body(newGenre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable long id, @Valid @RequestBody Genre updatedGenre) {
        return genreRepository.findById(id).map(genre -> {
            genre.setName(updatedGenre.getName());
            return ResponseEntity.ok(genreRepository.save(genre));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/genres/" + updatedGenre.getId()))
                    .body(genreRepository.save(updatedGenre));
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable long id) {
        if (!genreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        genreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}