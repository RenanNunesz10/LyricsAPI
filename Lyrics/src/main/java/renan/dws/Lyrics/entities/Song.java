package renan.dws.Lyrics.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "O título é obrigatório")
    @NotBlank(message = "O título não pode estar em branco")
    @Size(min=1, max=255)
    private String title;

    @NotNull(message = "O idioma é obrigatório")
    @Enumerated(EnumType.STRING)
    private Language language;

    // N:1 (Várias músicas pertencem a um Artista)
    @NotNull(message = "O artista é obrigatório para criar uma música")
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    // N:1 (Várias músicas pertencem a um Álbum)
    @NotNull(message = "O álbum é obrigatório")
    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    // N:M (Várias músicas têm vários gêneros)
    @ManyToMany
    @JoinTable(
            name = "song_genre",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    // 1:1 (Uma música tem uma letra)
    @OneToOne(mappedBy = "song", cascade = CascadeType.ALL)
    @JsonIgnore
    private LyricsDetails lyricsDetails;

    public Song() {
    }

    public Song(String title, Language language) {
        this.title = title;
        this.language = language;
    }

    public Song(long id, String title, Language language) {
        this.id = id;
        this.title = title;
        this.language = language;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }

    public LyricsDetails getLyricsDetails() { return lyricsDetails; }
    public void setLyricsDetails(LyricsDetails lyricsDetails) { this.lyricsDetails = lyricsDetails; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && Objects.equals(title, song.title) && language == song.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, language);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", language=" + language +
                '}';
    }
}