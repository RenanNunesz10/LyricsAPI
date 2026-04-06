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
import renan.dws.Lyrics.entities.Language;
import renan.dws.Lyrics.entities.Song;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.SongRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="Músicas", description = "Gerenciamento de músicas do catálogo")
@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongRepository songRepository;
    private final PagedResourcesAssembler<Song> pagedResourcesAssembler;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public SongController(SongRepository songRepository,
                          PagedResourcesAssembler<Song> pagedResourcesAssembler) {
        this.songRepository = songRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
            summary = "Lista todas as músicas",
            description = "Retorna uma lista paginada de todas as músicas cadastradas. A resposta inclui links de navegação **HATEOAS** para transitar entre as páginas de resultados."
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<Song>>> getSongs(@ParameterObject Pageable pageable){
        var songs = songRepository.findAll(pageable);
        PagedModel<EntityModel<Song>> pagedModelSongs = pagedResourcesAssembler.toModel(songs);
        return ResponseEntity.ok(pagedModelSongs);
    }

    @Operation(
            summary = "Busca uma música por ID",
            description = "Recupera os detalhes de uma música pelo identificador único. Inclui links **HATEOAS** apontando para o próprio recurso (`self`) e para a lista geral de músicas (`songs`)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Música encontrada",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class)) }),
            @ApiResponse(responseCode = "404", description = "Música não encontrada", content = @Content) })
    @GetMapping("/{id}")
    public EntityModel<Song> getSongById(@PathVariable long id){

        var song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada com ID: " + id));

        return EntityModel.of(song,
                linkTo(methodOn(SongController.class).getSongById(id)).withSelfRel(),
                linkTo(methodOn(SongController.class).getSongs(Pageable.unpaged())).withRel("songs"));
    }

    @Operation(
            summary = "Busca músicas pelo idioma",
            description = "Filtra as músicas utilizando o idioma especificado no `Enum`. Retorna uma lista paginada e navegável com controles **HATEOAS**."
    )
    @GetMapping("/search/language")
    public ResponseEntity<PagedModel<EntityModel<Song>>> getSongsByLanguage(
            @RequestParam Language language, @ParameterObject Pageable pageable) {
        var songs = songRepository.findByLanguage(language, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(songs));
    }

    @Operation(
            summary = "Cria uma nova música",
            description = "Cadastra uma nova música no banco de dados, vinculando-a a um artista e um álbum. Retorna o objeto criado e a URI de acesso no cabeçalho `Location`."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Música criada com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class)) }),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Faltou o artista, álbum ou título inválido", content = @Content) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Song> createSong(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da música a ser criada", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = @ExampleObject(value = "{ \"title\": \"Bohemian Rhapsody\", \"language\": \"EN_US\", \"artist\": { \"id\": 1 }, \"album\": { \"id\": 1 } }")))
            @Valid @RequestBody Song newSong){

        songRepository.save(newSong);
        return ResponseEntity.created(
                        URI.create("/songs/"+ newSong.getId()))
                .body(newSong);
    }

    @Operation(
            summary = "Atualiza uma música existente",
            description = "Atualiza os dados de uma música. Utiliza a lógica de **Upsert**: caso o ID informado não exista, uma nova música será criada com esses dados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Música atualizada com sucesso"),
            @ApiResponse(responseCode = "201", description = "Nova música criada com sucesso (ID não existia)"),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Faltou o artista, álbum ou título inválido", content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da música para atualizar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = @ExampleObject(value = "{ \"title\": \"Bohemian Rhapsody (Remastered)\", \"language\": \"EN_US\", \"artist\": { \"id\": 1 }, \"album\": { \"id\": 1 } }")))
            @Valid @RequestBody Song updatedSong){

        return songRepository.findById(id).map(
                song -> {
                    song.setTitle(updatedSong.getTitle());
                    song.setLanguage(updatedSong.getLanguage());
                    song.setArtist(updatedSong.getArtist());
                    song.setAlbum(updatedSong.getAlbum());
                    return ResponseEntity.ok(songRepository.save(song));
                }
        ).orElseGet(() -> ResponseEntity.created(URI.create("/songs/"+
                        updatedSong.getId()))
                .body(songRepository.save(updatedSong)));
    }

    @Operation(
            summary = "Deleta uma música pelo ID",
            description = "Remove uma música do banco de dados pelo seu ID. Retorna status `204 No Content` em caso de sucesso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Música deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Música não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable long id){
        if (!songRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        songRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}