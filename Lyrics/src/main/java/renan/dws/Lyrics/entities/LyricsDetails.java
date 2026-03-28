package renan.dws.Lyrics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
public class LyricsDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "A letra da música não pode estar vazia")
    @Column(columnDefinition = "TEXT") // Permite textos bem longos no banco H2
    private String textBody;

    private String originalWriters;

    // Relacionamento 1:1 (A letra pertence a uma música)
    @OneToOne
    @JoinColumn(name = "song_id", referencedColumnName = "id")
    private Song song;

    public LyricsDetails() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getOriginalWriters() {
        return originalWriters;
    }

    public void setOriginalWriters(String originalWriters) {
        this.originalWriters = originalWriters;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LyricsDetails that = (LyricsDetails) o;
        return id == that.id && Objects.equals(textBody, that.textBody) && Objects.equals(originalWriters, that.originalWriters) && Objects.equals(song, that.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, textBody, originalWriters, song);
    }
}