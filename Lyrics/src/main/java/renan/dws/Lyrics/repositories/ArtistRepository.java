package renan.dws.Lyrics.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import renan.dws.Lyrics.entities.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Page<Artist> findByNationalityContainingIgnoreCase(String nationality, Pageable pageable);
}