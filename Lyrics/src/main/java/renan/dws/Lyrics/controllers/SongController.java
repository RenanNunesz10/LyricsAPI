package renan.dws.Lyrics.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renan.dws.Lyrics.entities.Language;
import renan.dws.Lyrics.entities.Song;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.SongRepository;

import jakarta.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name="Músicas", description = "Lista de Músicas")
@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongRepository songRepository;
    private final PagedResourcesAssembler<Song> pagedResourcesAssembler;

    @Autowired
    public SongController(SongRepository songRepository,
                          PagedResourcesAssembler<Song> pagedResourcesAssembler) {
        this.songRepository = songRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Tag(name = "Get")
    @Operation(summary = "Get all songs", description = """
            Get all songs on the database, 
            even if the route returns one or less 
            items the API still returns a paginated list
            """)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<Song>>> getSongs(@ParameterObject Pageable pageable){
        var songs = songRepository.findAll(pageable);
        PagedModel<EntityModel<Song>> pagedModelSongs = pagedResourcesAssembler.toModel(songs);
        return ResponseEntity.ok(pagedModelSongs);
    }

    @Tag(name = "Get Song by id",
            description = "Get a single song by id, or returns 404 not found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the song",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Song not found",
                    content = @Content) })
    @GetMapping("/{id}")
    public EntityModel<Song> getSongById(@PathVariable(name = "id") long id){

        var song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada com ID: " + id));

        return EntityModel.of(song,
                linkTo(methodOn(SongController.class).getSongById(id)).withSelfRel(),
                linkTo(methodOn(SongController.class).getSongs(Pageable.unpaged())).withRel("songs"));
    }

    @GetMapping("/search/language")
    public ResponseEntity<PagedModel<EntityModel<Song>>> getSongsByLanguage(
            @RequestParam Language language, @ParameterObject Pageable pageable) {
        var songs = songRepository.findByLanguage(language, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(songs));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Song created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class)) }),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Faltou o artista, álbum ou título inválido") })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Song> createSong(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Song to create", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            // 👇 Veja que agora o exemplo inclui o artista e o álbum!
                            examples = @ExampleObject(value = "{ \"title\": \"Bohemian Rhapsody\", \"language\": \"EN_US\", \"artist\": { \"id\": 1 }, \"album\": { \"id\": 1 } }")))
            @Valid @RequestBody Song newSong){

        songRepository.save(newSong);
        return ResponseEntity.created(
                        URI.create("/songs/"+ newSong.getId()))
                .body(newSong);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable long id,
                                           @Valid @RequestBody Song updatedSong){

        return songRepository.findById(id).map(
                song -> {
                    song.setTitle(updatedSong.getTitle());
                    song.setLanguage(updatedSong.getLanguage());
                    song.setArtist(updatedSong.getArtist()); // Atualiza o artista
                    song.setAlbum(updatedSong.getAlbum());   // Atualiza o álbum
                    return ResponseEntity.ok(songRepository.save(song));
                }
        ).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/songs/"+
                            updatedSong.getId()))
                    .body(songRepository.save(updatedSong));
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable long id){
        var song = songRepository.findById(id).orElse(null);
        if(song == null)
            return ResponseEntity.notFound().build();

        songRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}