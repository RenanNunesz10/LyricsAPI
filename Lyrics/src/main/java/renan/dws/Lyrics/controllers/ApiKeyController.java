package renan.dws.Lyrics.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renan.dws.Lyrics.config.ApiKeyService;

import java.util.Set;

@RestController
@RequestMapping("/api-keys")
@Tag(name = "API Keys", description = "Endpoints for managing API Keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    @Operation(summary = "List all API Keys")
    public ResponseEntity<Set<String>> listKeys() {
        return ResponseEntity.ok(apiKeyService.getAllKeys());
    }

    @PostMapping
    @Operation(summary = "Create a new API Key")
    public ResponseEntity<String> createKey() {
        String novaChave = apiKeyService.createKey();
        return ResponseEntity.status(HttpStatus.CREATED).body(novaChave);
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Delete an API Key")
    public ResponseEntity<Void> deleteKey(@PathVariable String key) {
        boolean removida = apiKeyService.deleteKey(key);
        if (removida) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}