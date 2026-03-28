package renan.dws.Lyrics.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import renan.dws.Lyrics.entities.Language;
import renan.dws.Lyrics.entities.Song;

public interface SongRepository extends JpaRepository<Song, Long> {
    // Consulta personalizada exigida pelo professor
    Page<Song> findByLanguage(Language language, Pageable pageable);
}