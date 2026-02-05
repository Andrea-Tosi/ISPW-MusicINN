package org.musicinn.musicinn.model;

import org.junit.jupiter.api.Test;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andrea Tosi
 */
class ApplicationScoreTest {
    /**
     * Test della funzione di sistema per il calcolo del matching score.
     * Verifica che la logica di business calcoli correttamente l'affinità
     * basandosi sulla proporzione tra generi dell'artista e generi richiesti.
     */
    @Test
    void testCalculateAndSetScore() {
        Application application = new Application();

        // Scenario: Annuncio richiede 4 generi, l'artista ne ha 2 in comune
        List<MusicalGenre> requested = List.of(MusicalGenre.ROCK, MusicalGenre.POP, MusicalGenre.METAL, MusicalGenre.JAZZ);
        List<MusicalGenre> artist = List.of(MusicalGenre.ROCK, MusicalGenre.METAL, MusicalGenre.INDIE);

        // Chiamata alla funzione di sistema
        application.calculateAndSetScore(artist, requested);

        // 2 comuni su 4 richiesti = 50.0%
        assertEquals(50.0, application.getScore(), "Il calcolo dell'affinità deve restituire il 50%");
    }
}