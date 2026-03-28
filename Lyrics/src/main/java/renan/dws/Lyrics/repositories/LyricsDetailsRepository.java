package renan.dws.Lyrics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import renan.dws.Lyrics.entities.LyricsDetails;

public interface LyricsDetailsRepository extends JpaRepository<LyricsDetails, Long> {
    org.springframework.data.domain.Page<renan.dws.Lyrics.entities.LyricsDetails> findByOriginalWritersContainingIgnoreCase(String writer, org.springframework.data.domain.Pageable pageable);
}