package renan.dws.Lyrics.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Entity
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O nome do artista é obrigatório")
    private String name;

    private String nationality;

    // 1:N (Um artista tem várias músicas)
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    @JsonIgnore // Evita um loop infinito do json
    private List<Song> songs;

    public Artist() {}

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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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
        Artist artist = (Artist) o;
        return id == artist.id && Objects.equals(name, artist.name) && Objects.equals(nationality, artist.nationality) && Objects.equals(songs, artist.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nationality, songs);
    }
}