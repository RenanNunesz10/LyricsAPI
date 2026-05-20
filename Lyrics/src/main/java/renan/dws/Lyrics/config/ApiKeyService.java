package renan.dws.Lyrics.config;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiKeyService {

    // Lista thread-safe para guardar as chaves em memória
    private final Set<String> validKeys = ConcurrentHashMap.newKeySet();

    public ApiKeyService() {
        // Já deixamos a nossa chave padrão salva para você não perder o acesso ao testar!
        validKeys.add("lyrics-secreta-123");
    }

    public Set<String> getAllKeys() {
        return validKeys;
    }

    public String createKey() {
        // Gera uma chave única (ex: 550e8400-e29b-41d4-a716-446655440000)
        String newKey = UUID.randomUUID().toString();
        validKeys.add(newKey);
        return newKey;
    }

    public boolean deleteKey(String key) {
        return validKeys.remove(key);
    }

    public boolean isValid(String key) {
        return validKeys.contains(key);
    }
}