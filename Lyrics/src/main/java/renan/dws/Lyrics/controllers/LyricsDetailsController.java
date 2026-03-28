package renan.dws.Lyrics.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renan.dws.Lyrics.entities.LyricsDetails;
import renan.dws.Lyrics.exceptions.ResourceNotFoundException;
import renan.dws.Lyrics.repositories.LyricsDetailsRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<LyricsDetails>>> getAllLyricsDetails(@ParameterObject Pageable pageable) {
        var details = lyricsDetailsRepository.findAll(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(details));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes encontrados"),
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

    @GetMapping("/search/writer")
    public ResponseEntity<PagedModel<EntityModel<LyricsDetails>>> getLyricsByWriter(
            @RequestParam String writer, @ParameterObject Pageable pageable) {
        var details = lyricsDetailsRepository.findByOriginalWritersContainingIgnoreCase(writer, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(details));
    }

    @PostMapping
    public ResponseEntity<LyricsDetails> createLyricsDetails(@Valid @RequestBody LyricsDetails newDetails) {
        lyricsDetailsRepository.save(newDetails);
        return ResponseEntity.created(URI.create("/lyrics-details/" + newDetails.getId())).body(newDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LyricsDetails> updateLyricsDetails(@PathVariable long id, @Valid @RequestBody LyricsDetails updatedDetails) {
        return lyricsDetailsRepository.findById(id).map(details -> {
            details.setTextBody(updatedDetails.getTextBody());
            details.setOriginalWriters(updatedDetails.getOriginalWriters());
            return ResponseEntity.ok(lyricsDetailsRepository.save(details));
        }).orElseGet(() -> {
            return ResponseEntity.created(URI.create("/lyrics-details/" + updatedDetails.getId()))
                    .body(lyricsDetailsRepository.save(updatedDetails));
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLyricsDetails(@PathVariable long id) {
        if (!lyricsDetailsRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lyricsDetailsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}