package renan.dws.Lyrics.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    // Uma lista thread-safe para guardar as chaves que já foram processadas
    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // A idempotência faz sentido principalmente em requisições de criação (POST)
        if ("POST".equalsIgnoreCase(request.getMethod())) {

            String idempotencyKey = request.getHeader("X-Idempotency-Key");

            // Verifica se o Header foi enviado
            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {

                // O método .add() retorna 'true' se a chave for nova, e 'false' se ela JÁ EXISTIR na lista
                boolean isNewKey = processedKeys.add(idempotencyKey);

                if (!isNewKey) {
                    // Chave repetida! Bloqueia a requisição para não duplicar no banco.
                    response.setStatus(HttpStatus.CONFLICT.value()); // Status 409
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"erro\": \"Requisição duplicada. Esta operação já foi processada anteriormente.\"}");
                    return; // Para a execução aqui, não deixa chegar no Controller
                }
            }
        }

        // Se for um GET, DELETE, ou um POST com uma chave nova, deixa a requisição seguir normalmente
        filterChain.doFilter(request, response);
    }
}