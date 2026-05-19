package renan.dws.Lyrics.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Cache em memória que guarda um "balde" (Bucket) para cada IP diferente
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Configuração da regra: Cria um balde com 5 requisições por minuto
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5) // Capacidade máxima
                .refillGreedy(5, Duration.ofMinutes(1)) // Recarrega 5 fichas a cada 1 minuto
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Busca o balde do IP, ou cria um novo se for o primeiro acesso dele
    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Pega o IP do cliente
        String ip = request.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);

        // 2. Tenta consumir 1 ficha do balde e retorna os dados dessa operação (ConsumptionProbe)
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Sucesso! A requisição passou.
            // Adicionamos um Header opcional mostrando quantas requisições o IP ainda tem
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // 3. Limite estourou! Pega os segundos restantes para o balde recarregar
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            // 4. Retorna os requisitos exatos pedidos pelo professor (Header Retry-After e Status 429)
            response.addHeader("Retry-After", String.valueOf(waitForRefill));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Muitas requisições (Rate Limit). Tente novamente em " + waitForRefill + " segundos.\"}");
        }
    }
}