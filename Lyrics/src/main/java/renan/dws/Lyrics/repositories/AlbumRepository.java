package renan.dws.Lyrics.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import renan.dws.Lyrics.entities.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findByReleaseYear(int year, Pageable pageable);
}