package renan.dws.Lyrics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import renan.dws.Lyrics.entities.Genre;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Busca um gênero exato pelo nome
    Optional<Genre> findByNameIgnoreCase(String name);
}