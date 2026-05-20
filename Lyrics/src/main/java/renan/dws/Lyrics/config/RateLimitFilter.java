package renan.dws.Lyrics.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1) // Executa primeiro, protegendo a API
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final long CAPACIDADE_MAXIMA = 5; // 5 requisições

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(CAPACIDADE_MAXIMA)
                .refillGreedy(CAPACIDADE_MAXIMA, Duration.ofMinutes(1)) // Recarrega a cada 1 minuto
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignora o Swagger e a geração de chaves para não gastar o limite
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/api-keys")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // Headers de "uso e limite" que o professor pediu (enviados em TODAS as requisições)
        response.addHeader("X-RateLimit-Limit", String.valueOf(CAPACIDADE_MAXIMA));
        response.addHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));

        System.out.println("⏱️ [Rate Limit] IP: " + ip + " | Restantes: " + probe.getRemainingTokens());

        if (probe.isConsumed()) {
            // Requisição liberada!
            filterChain.doFilter(request, response);
        } else {
            // Limite excedido!
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            System.out.println("🚨 [Rate Limit] BLOQUEADO! Aguarde " + waitForRefill + "s");

            // Header extra exigido no erro 429
            response.addHeader("Retry-After", String.valueOf(waitForRefill));

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // Status 429
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"erro\": \"Too Many Requests. Limite de requisições excedido. Tente novamente em " + waitForRefill + " segundos.\"}");
        }
    }
}