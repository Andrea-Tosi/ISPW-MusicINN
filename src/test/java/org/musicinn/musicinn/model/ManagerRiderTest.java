package org.musicinn.musicinn.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe che testa l'algoritmo di compatibilità tra l'attrezzatura disponibile nel locale (Manager) e i requisiti minimi richiesti dall'artista. Permette all'artista di vedere se può suonare o meno in un determinato locale.
 * @author Andrea Tosi
 */
class ManagerRiderTest {
    /**
     * Accerta che la validazione fallisca correttamente quando il locale non possiede un numero sufficiente di componenti (es. meno microfoni di quelli necessari all'artista).
     */
    @Test
    void testContainsIncompatibleEquipment() {
        ManagerRider venueRider = new ManagerRider();
        venueRider.setInputs(List.of(new MicrophoneSet(5, false)));

        ArtistRider artistRider = new ArtistRider();
        artistRider.setInputs(List.of(new MicrophoneSet(10, false)));

        ValidationResult result = venueRider.contains(artistRider);

        assertFalse(result.isValid(), "Il locale non ha abbastanza microfoni");
    }

    /**
     * Verifica che un mixer del locale venga considerato compatibile se e solo se soddisfa o supera le specifiche dell'artista (numero canali, mandate AUX, tecnologia digitale/analogica).
     */
    @Test
    void testCompatibleMixerFeatures() {
        Mixer venueMixer = new Mixer();
        venueMixer.setInputChannels(24);
        venueMixer.setDigital(true);
        ManagerRider venueRider = new ManagerRider();
        venueRider.setMixers(List.of(venueMixer));

        Mixer requestedMixer = new Mixer();
        requestedMixer.setInputChannels(12);
        requestedMixer.setDigital(true);
        ArtistRider artistRider = new ArtistRider();
        artistRider.setFohMixer(requestedMixer);

        ValidationResult result = venueRider.contains(artistRider);

        assertTrue(result.isValid(), "Il mixer del locale dovrebbe essere compatibile");
    }
}
