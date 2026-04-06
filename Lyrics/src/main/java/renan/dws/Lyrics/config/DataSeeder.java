package renan.dws.Lyrics.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import renan.dws.Lyrics.entities.*;
import renan.dws.Lyrics.repositories.*;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(ArtistRepository artistRepository,
                                   AlbumRepository albumRepository,
                                   GenreRepository genreRepository,
                                   SongRepository songRepository,
                                   LyricsDetailsRepository lyricsDetailsRepository) {
        return _ -> {
            if (artistRepository.count() == 0) {
                System.out.println("🌱 Semeando dados iniciais no banco de dados...");

                // ==========================
                // 1. GÊNEROS MUSICAIS
                // ==========================
                Genre rockClassic = new Genre();
                rockClassic.setName("Rock Clássico");
                genreRepository.save(rockClassic);

                Genre nuMetal = new Genre();
                nuMetal.setName("Nu Metal");
                genreRepository.save(nuMetal);

                Genre forro = new Genre();
                forro.setName("Forró Romântico");
                genreRepository.save(forro);


                // ==========================
                // 2. QUEEN (Bohemian Rhapsody)
                // ==========================
                Artist queen = new Artist();
                queen.setName("Queen");
                queen.setNationality("Britânica");
                artistRepository.save(queen);

                Album nightAtTheOpera = new Album();
                nightAtTheOpera.setTitle("A Night at the Opera");
                nightAtTheOpera.setReleaseYear(1975);
                albumRepository.save(nightAtTheOpera);

                Song bohemianRhapsody = new Song();
                bohemianRhapsody.setTitle("Bohemian Rhapsody");
                bohemianRhapsody.setLanguage(Language.EN_US);
                bohemianRhapsody.setArtist(queen);
                bohemianRhapsody.setAlbum(nightAtTheOpera);
                bohemianRhapsody.setGenres(List.of(rockClassic));
                songRepository.save(bohemianRhapsody);

                LyricsDetails lyricsQueen = new LyricsDetails();
                lyricsQueen.setOriginalWriters("Freddie Mercury");
                lyricsQueen.setTextBody("Is this the real life?\nIs this just fantasy?\nCaught in a landslide,\nNo escape from reality.");
                lyricsQueen.setSong(bohemianRhapsody);
                lyricsDetailsRepository.save(lyricsQueen);


                // ==========================
                // 3. LINKIN PARK (In The End)
                // ==========================
                Artist linkinPark = new Artist();
                linkinPark.setName("Linkin Park");
                linkinPark.setNationality("Estadunidense");
                artistRepository.save(linkinPark);

                Album hybridTheory = new Album();
                hybridTheory.setTitle("Hybrid Theory");
                hybridTheory.setReleaseYear(2000);
                albumRepository.save(hybridTheory);

                Song inTheEnd = new Song();
                inTheEnd.setTitle("In The End");
                inTheEnd.setLanguage(Language.EN_US);
                inTheEnd.setArtist(linkinPark);
                inTheEnd.setAlbum(hybridTheory);
                inTheEnd.setGenres(List.of(nuMetal));
                songRepository.save(inTheEnd);

                LyricsDetails lyricsLinkin = new LyricsDetails();
                lyricsLinkin.setOriginalWriters("Chester Bennington, Mike Shinoda");
                lyricsLinkin.setTextBody("It starts with one thing\nI don't know why\nIt doesn't even matter how hard you try\nKeep that in mind, I designed this rhyme");
                lyricsLinkin.setSong(inTheEnd);
                lyricsDetailsRepository.save(lyricsLinkin);


                // ==========================
                // 4. CALCINHA PRETA (Mágica)
                // ==========================
                Artist calcinhaPreta = new Artist();
                calcinhaPreta.setName("Calcinha Preta");
                calcinhaPreta.setNationality("Brasileira");
                artistRepository.save(calcinhaPreta);

                Album vol13 = new Album();
                vol13.setTitle("Volume 13: Mágica");
                vol13.setReleaseYear(2005);
                albumRepository.save(vol13);

                Song magica = new Song();
                magica.setTitle("Mágica");
                magica.setLanguage(Language.PT_BR);
                magica.setArtist(calcinhaPreta);
                magica.setAlbum(vol13);
                magica.setGenres(List.of(forro));
                songRepository.save(magica);

                LyricsDetails lyricsCalcinha = new LyricsDetails();
                lyricsCalcinha.setOriginalWriters("Chrystian Lima / Ivo Lima");
                lyricsCalcinha.setTextBody("Me diz o que é que eu faço com essa solidão\nTô tentando te esquecer, mas meu coração\nSó quer saber de você, só quer saber de você");
                lyricsCalcinha.setSong(magica);
                lyricsDetailsRepository.save(lyricsCalcinha);

                System.out.println("✅ Banco de dados populado com sucesso!");
            } else {
                System.out.println("👍 Banco de dados já possui registros. Pulando a semeadura.");
            }
        };
    }
}