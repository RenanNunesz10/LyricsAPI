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
import renan.dws.Lyrics.entities.LyricsDetails;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.LyricsDetailsRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name="Detalhes das Letras", description = "Gerenciamento das letras das músicas")
@RestController
@RequestMapping("/lyrics-details")
public class LyricsDetailsController {

    private final LyricsDetailsRepository lyricsDetailsRepository;
    private final PagedResourcesAssembler<LyricsDetails> pagedResourcesAssembler;

    @Autowired
    public LyricsDetailsController(LyricsDetailsRepository lyricsDetailsRepository, PagedResourcesAssembler<LyricsDetails> pagedResourcesAssembler) {
        this.lyricsDetailsRepository = lyricsDetailsRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Lista todas as letras", description = "Retorna uma lista paginada de todas as letras cadastradas.")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<LyricsDetails>>> getAllLyricsDetails(@ParameterObject Pageable pageable) {
        var details = lyricsDetailsRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(details));
    }

    @Operation(summary = "Busca uma letra por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes encontrados",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LyricsDetails.class)) }),
            @ApiResponse(responseCode = "404", description = "Detalhes não encontrados", content = @Content)
    })
    @GetMapping("/{id}")
    public EntityModel<LyricsDetails> getLyricsDetailsById(@PathVariable long id) {
        var details = lyricsDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalhes de letra não encontrados com ID: " + id));

        return EntityModel.of(details,
                linkTo(methodOn(LyricsDetailsController.class).getLyricsDetailsById(id)).withSelfRel(),
                linkTo(methodOn(LyricsDetailsController.class).getAllLyricsDetails(Pageable.unpaged())).withRel("lyrics-details"));
    }

    @Operation(summary = "Busca letras pelo compositor original")
    @GetMapping("/search/writer")
    public ResponseEntity<PagedModel<EntityModel<LyricsDetails>>> getLyricsByWriter(
            @RequestParam String writer, @ParameterObject Pageable pageable) {
        var details = lyricsDetailsRepository.findByOriginalWritersContainingIgnoreCase(writer, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(details));
    }

    @Operation(summary = "Adiciona uma nova letra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Letra cadastrada com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LyricsDetails.class)) }),
            @ApiResponse(responseCode = "400", description = "Erro de validação: Letra vazia ou música ausente", content = @Content) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LyricsDetails> createLyricsDetails(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da letra a ser cadastrada", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LyricsDetails.class),
                            examples = @ExampleObject(value = "{ \"textBody\": \"Is this the real life? Is this just fantasy?\", \"originalWriters\": \"Freddie Mercury\", \"song\": { \"id\": 1 } }")))
            @Valid @RequestBody LyricsDetails newDetails) {
        lyricsDetailsRepository.save(newDetails);
        return ResponseEntity.created(URI.create("/lyrics-details/" + newDetails.getId())).body(newDetails);
    }

    @Operation(summary = "Atualiza uma letra existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Letra atualizada com sucesso"),
            @ApiResponse(responseCode = "201", description = "Nova letra criada com sucesso (ID não existia)"),
            @ApiResponse(responseCode = "400", description = "Erro de validação", content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<LyricsDetails> updateLyricsDetails(
            @PathVariable long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da letra para atualizar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LyricsDetails.class),
                            examples = @ExampleObject(value = "{ \"textBody\": \"Nova versão da letra...\", \"originalWriters\": \"Freddie Mercury\", \"song\": { \"id\": 1 } }")))
            @Valid @RequestBody LyricsDetails updatedDetails) {
        return lyricsDetailsRepository.findById(id).map(details -> {
            details.setTextBody(updatedDetails.getTextBody());
            details.setOriginalWriters(updatedDetails.getOriginalWriters());
            details.setSong(updatedDetails.getSong()); // Adicionado para manter consistência
            return ResponseEntity.ok(lyricsDetailsRepository.save(details));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/lyrics-details/" + updatedDetails.getId()))
                    .body(lyricsDetailsRepository.save(updatedDetails));
        });
    }

    @Operation(summary = "Deleta uma letra pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Letra deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Letra não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLyricsDetails(@PathVariable long id) {
        if (!lyricsDetailsRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lyricsDetailsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}