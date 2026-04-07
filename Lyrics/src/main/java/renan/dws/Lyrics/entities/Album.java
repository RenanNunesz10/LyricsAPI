package renan.dws.Lyrics.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O título do álbum é obrigatório")
    @Column(nullable = false)
    private String title;

    @Min(value = 1900, message = "O ano de lançamento deve ser maior ou igual a 1900")
    @Max(value = 2026, message = "O ano de lançamento não pode estar no futuro")
    private int releaseYear;

    // 1:N (Um álbum tem várias músicas)
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Song> songs;

    public Album() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return id == album.id && releaseYear == album.releaseYear && Objects.equals(title, album.title) && Objects.equals(songs, album.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, releaseYear, songs);
    }
}