package org.musicinn.musicinn.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test focalizzata sulla logica di business della gestione temporale. Assicura che il sistema di booking prevenga conflitti di programmazione all'interno del calendario di un locale.
 * @author Andrea Tosi
 */
class CalendarTest {
    /**
     * Valuta l'algoritmo di rilevamento sovrapposizioni. Testa due scenari:
     * <ul>
     * <li>Il fallimento della pubblicazione se un nuovo evento si sovrappone anche solo parzialmente a uno esistente.</li>
     * <li>Il successo se l'evento è pianificato in un intervallo di tempo libero.</li>
     * </ul>
     */
    @Test
    void testIsAvailableOverlap() {
        Calendar calendar = new Calendar();
        LocalDate date = LocalDate.now().plusDays(10);

        Announcement existing = new Announcement();
        existing.setStartEventDay(date);
        existing.setStartEventTime(LocalTime.of(21, 0));
        existing.setDuration(Duration.ofMinutes(120));

        calendar.setEvents(List.of(existing));

        boolean result = calendar.isAvailable(date, LocalTime.of(22, 0), Duration.ofMinutes(60));
        assertFalse(result, "Dovrebbe risultare occupato per sovrapposizione");

        boolean resultOk = calendar.isAvailable(date, LocalTime.of(23, 30), Duration.ofMinutes(60));
        assertTrue(resultOk, "Dovrebbe risultare disponibile");
    }
}
