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

    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod())) {

            String idempotencyKey = request.getHeader("X-Idempotency-Key");

            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {

                boolean isNewKey = processedKeys.add(idempotencyKey);

                if (!isNewKey) {
                    // Bloqueia a requisição para não duplicar no banco.
                    response.setStatus(HttpStatus.CONFLICT.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"erro\": \"Requisição duplicada. Esta operação já foi processada anteriormente.\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}