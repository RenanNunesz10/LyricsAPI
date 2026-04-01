package renan.dws.Lyrics.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
public class LyricsDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "A letra da música não pode estar vazia")
    @Column(columnDefinition = "TEXT", nullable = false) // <-- Adicionamos nullable = false
    private String textBody;

    private String originalWriters;

    // 1:1 (A letra pertence a uma música)
    @NotNull(message = "A letra deve estar obrigatoriamente vinculada a uma música") // <-- Proteção da API
    @OneToOne
    @JoinColumn(name = "song_id", referencedColumnName = "id", nullable = false) // <-- Proteção do Banco
    private Song song;

    public LyricsDetails() {}

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