package renan.dws.Lyrics.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    // Para fins acadêmicos, deixamos fixo. Em produção, isso viria do banco de dados ou .env
    private static final String VALID_API_KEY = "lyrics-secreta-123";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Libera o Swagger para não precisar de chave para ler a documentação
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extrai o header da requisição
        String reqApiKey = request.getHeader(API_KEY_HEADER);

        // 3. Valida a chave
        if (VALID_API_KEY.equals(reqApiKey)) {
            // Chave correta! Deixa a requisição seguir para o Controller
            filterChain.doFilter(request, response);
        } else {
            // Chave incorreta ou ausente! Bloqueia e retorna 401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\": \"Acesso negado. Chave de API ('X-API-Key') ausente ou invalida.\"}");
        }
    }
}