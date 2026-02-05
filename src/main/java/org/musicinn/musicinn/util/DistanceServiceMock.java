package org.musicinn.musicinn.util;

/**
 * Mock Service per il calcolo della distanza tra indirizzi.
 * In una versione reale, interfaccerebbe API come Google Maps.
 */
public class DistanceServiceMock {
    /**
     * Simula il calcolo della distanza in chilometri.
     * Restituisce un valore costante per la stessa coppia di indirizzi.
     */
    public int calculateDistance(String city1, String address1, String city2, String address2) {
        if (city1 == null || address1 == null || city2 == null || address2 == null ||
                city1.isBlank() || address1.isBlank() || city2.isBlank() || address2.isBlank()) {
            return 0;
        }

        // Se sono esattamente lo stesso posto, la distanza è 0
        if (city1.equalsIgnoreCase(city2) && address1.equalsIgnoreCase(address2)) {
            return 0;
        }

        // Logica deterministica basata sull'hash delle stringhe
        // Questo garantisce che "Roma, Via del Corso" -> "Milano, Via Brera" dia sempre lo stesso risultato
        String point1 = (city1 + address1).toLowerCase();
        String point2 = (city2 + address2).toLowerCase();

        int seed = Math.abs(point1.hashCode() ^ point2.hashCode());

        // Se le città sono diverse, simuliamo una distanza interurbana (50-250 km)
        if (!city1.equalsIgnoreCase(city2)) {
            return (seed % 200) + 50;
        }

        // Se la città è la stessa, simuliamo una distanza urbana (1-15 km)
        return (seed % 14) + 1;
    }
}

