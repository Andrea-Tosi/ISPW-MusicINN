package org.musicinn.musicinn.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test dedicata alla verifica della logica di validazione interna del rider tecnico dell'artista. L'obiettivo è garantire che le richieste tecniche dell'artista siano coerenti tra loro prima che vengano sottomesse a un locale.
 * @author Andrea Tosi
 */
class ArtistRiderTest {
    /**
     * Verifica che il sistema rilevi un errore di coerenza quando il numero di input fisici (es. microfoni e DI box) è superiore alla capacità dei canali d'ingresso del mixer selezionato.
     */
    @Test
    void testValidateInconsistentChannels() {
        Mixer foh = new Mixer();
        foh.setInputChannels(4);
        foh.setFOH(true);

        ArtistRider rider = new ArtistRider();
        rider.setFohMixer(foh);
        rider.setInputs(List.of(new MicrophoneSet(10, false)));

        ValidationResult result = rider.validate();

        assertFalse(result.isValid(), "Il rider non dovrebbe essere valido (canali insufficienti)");
        assertTrue(result.getErrors().getFirst().contains("canali"), "L'errore dovrebbe riguardare i canali");
    }

    /**
     * Verifica il controllo sulla Phantom Power (48V). Il test fallisce se l'attrezzatura richiede alimentazione phantom ma il mixer scelto dall'artista non la supporta.
     */
    @Test
    void testValidateMissingPhantomPower() {
        Mixer foh = new Mixer();
        foh.setInputChannels(12);
        foh.setHasPhantomPower(false);

        ArtistRider rider = new ArtistRider();
        rider.setFohMixer(foh);
        rider.setInputs(List.of(new MicrophoneSet(1, true)));

        ValidationResult result = rider.validate();

        assertFalse(result.isValid());
        assertTrue(result.toString().contains("Phantom Power"), "Dovrebbe segnalare la mancanza di Phantom Power");
    }
}