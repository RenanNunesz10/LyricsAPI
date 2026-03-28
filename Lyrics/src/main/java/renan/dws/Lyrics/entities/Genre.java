package renan.dws.Lyrics.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O nome do gênero é obrigatório")
    @Column(unique = true) // Gêneros não devem se repetir
    private String name;

    // Relacionamento M:N (O lado inverso, espelhando o que está em Song)
    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private List<Song> songs;

    public Genre() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id && Objects.equals(name, genre.name) && Objects.equals(songs, genre.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, songs);
    }
}