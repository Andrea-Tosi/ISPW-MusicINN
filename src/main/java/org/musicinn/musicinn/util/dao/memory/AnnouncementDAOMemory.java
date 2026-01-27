package org.musicinn.musicinn.util.dao.memory;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.interfaces.AnnouncementDAO;
import org.musicinn.musicinn.util.enumerations.AnnouncementState;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.enumerations.TypeVenue;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnouncementDAOMemory implements AnnouncementDAO {
    @Override
    public void save(Announcement announcement) {
        System.out.println("Annuncio" + announcement + "salvato");
    }

    public List<SchedulableEvent> getEventsByDate(LocalDate date) {
        List<SchedulableEvent> mockEvents = new ArrayList<>();

        // Simuliamo che nel database ci siano già 2 eventi per la data richiesta
        // Nota: Usiamo Announcement perché estende SchedulableEvent

        // Evento 1: dalle 18:00 alle 20:00
        Announcement event1 = new Announcement();
        event1.setStartEventDay(date);
        event1.setStartEventTime(LocalTime.of(18, 0));
        event1.setDuration(Duration.ofHours(2));
        mockEvents.add(event1);

        // Evento 2: dalle 21:30 alle 23:00
        Announcement event2 = new Announcement();
        event2.setStartEventDay(date);
        event2.setStartEventTime(LocalTime.of(21, 30));
        event2.setDuration(Duration.ofHours(1).plusMinutes(30));
        mockEvents.add(event2);

        System.out.println("[MOCK DAO] Recuperati " + mockEvents.size() + " eventi simulati per la data: " + date);

        return mockEvents;
    }

    @Override
    public List<SchedulableEvent> getConfirmedEventsByDate(LocalDate startingDate) throws DatabaseException {
        return List.of();
    }
//    public boolean save(Announcement a) throws SQLException {
//        String sql = "INSERT INTO Announcements (event_date, start_time, duration, cachet) VALUES (?, ?, ?, ?)";
//
//        // Utilizziamo l'opzione RETURN_GENERATED_KEYS per ottenere l'ID dell'annuncio appena creato
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            pstmt.setDate(1, Date.valueOf(a.getDate()));
//            pstmt.setTime(2, Time.valueOf(a.getStartTime()));
//            pstmt.setLong(3, a.getDuration().toMinutes()); // Salviamo la durata in minuti
//            pstmt.setDouble(4, a.getCachet());
//
//            int affectedRows = pstmt.executeUpdate();
//
//            if (affectedRows > 0) {
//                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        long announcementId = generatedKeys.getLong(1);
//                        // Importante: Salviamo i generi musicali nella tabella di join
//                        saveGenres(announcementId, a.getGenres(), conn);
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // Metodo per recuperare eventi in una data (per il controllo isAvailable)
//    public List<SchedulableEvent> getEventsByDate(LocalDate date) throws SQLException {
//        List<SchedulableEvent> events = new ArrayList<>();
//        String sql = "SELECT * FROM Announcements WHERE event_date = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setDate(1, Date.valueOf(date));
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                Announcement a = new Announcement();
//                a.setDate(rs.getDate("event_date").toLocalDate());
//                a.setStartTime(rs.getTime("start_time").toLocalTime());
//                a.setDuration(Duration.ofMinutes(rs.getLong("duration")));
//                events.add(a);
//            }
//        }
//        return events;
//    }
//
//    private void saveGenres(long announcementId, List<MusicalGenre> genres, Connection conn) throws SQLException {
//        String sql = "INSERT INTO Announcement_Genres (announcement_id, genre_name) VALUES (?, ?)";
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            for (MusicalGenre g : genres) {
//                pstmt.setLong(1, announcementId);
//                pstmt.setString(2, g.name()); // Salviamo il nome dell'Enum
//                pstmt.addBatch();
//            }
//            pstmt.executeBatch();
//        }
//    }

    @Override
    public List<Announcement> findActiveByGenres(List<MusicalGenre> artistGenres, int page, int pageSize) {
        List<Announcement> allAnnouncements = generateMockData(); // Simula la tabella DB
        List<Announcement> filteredResults = new ArrayList<>();

        for (Announcement ann : allAnnouncements) {
            // 1. Controllo lo stato (Solo quelli OPEN)
            if (ann.getState() != AnnouncementState.OPEN) {
                continue;
            }

            // 2. Controllo l'intersezione dei generi (Strategia A)
            if (hasCommonGenre(artistGenres, ann.getRequestedGenres())) {
                filteredResults.add(ann);
            }
        }

        // 3. Simulazione Paginazione (Prendiamo solo i 10 richiesti per la pagina)
        int fromIndex = Math.min(page * pageSize, filteredResults.size());
        int toIndex = Math.min(fromIndex + pageSize, filteredResults.size());

        return filteredResults.subList(fromIndex, toIndex);
    }

    private boolean hasCommonGenre(List<MusicalGenre> artistGenres, List<MusicalGenre> requestedGenres) {
        // Ritorna true se almeno un genere coincide
        for (MusicalGenre g : artistGenres) {
            if (requestedGenres.contains(g)) return true;
        }
        return false;
    }

    private List<Announcement> generateMockData() {
        List<Announcement> list = new ArrayList<>();

        Venue venue = new Venue("The Rock Club", "Roma", "Via del Corso 10", TypeVenue.CLUB);
        TechnicalRiderDAOMemory dao = new TechnicalRiderDAOMemory();
        ManagerRider rider = (ManagerRider) dao.read(Session.getSingletonInstance().getUsername(), Session.UserRole.MANAGER);
        venue.setRider(rider);

        // Esempio Annuncio 1: Compatibile
        Announcement ann1 = new Announcement();
        ann1.setState(AnnouncementState.OPEN);
        ann1.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
        ann1.setStartEventDay(LocalDate.now().plusDays(7));
        ann1.setStartEventTime(LocalTime.of(21, 30));
        ann1.setDuration(Duration.ofMinutes(100));
        ann1.setCachet(200.0);
        ann1.setDeposit(100.0);
        ann1.setDescription(".");
        ann1.setVenue(venue);
        ann1.setDoesUnreleased(true);
        ann1.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
        list.add(ann1);

        // Esempio Annuncio 2: Chiuso (Verrà scartato)
        Announcement ann2 = new Announcement();
        ann2.setState(AnnouncementState.CLOSED);
        ann2.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
        ann2.setStartEventDay(LocalDate.now().plusDays(8));
        ann2.setStartEventTime(LocalTime.of(21, 30));
        ann2.setDuration(Duration.ofMinutes(100));
        ann2.setCachet(200.0);
        ann2.setDeposit(100.0);
        ann2.setDescription(".");
        ann2.setVenue(venue);
        ann2.setDoesUnreleased(true);
        ann2.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
        list.add(ann2);

        // Esempio Annuncio 3: Genere diverso (Verrà scartato se l'artista non fa JAZZ)
        Announcement ann3 = new Announcement();
        ann3.setState(AnnouncementState.OPEN);
        ann3.setRequestedGenres(List.of(MusicalGenre.JAZZ));
        ann3.setStartEventDay(LocalDate.now().plusDays(9));
        ann3.setStartEventTime(LocalTime.of(21, 30));
        ann3.setDuration(Duration.ofMinutes(100));
        ann3.setCachet(200.0);
        ann3.setDeposit(100.0);
        ann3.setDescription(".");
        ann3.setVenue(venue);
        ann3.setDoesUnreleased(true);
        ann3.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
        list.add(ann3);

        for (int i = 0; i < 30; i++) {
            Announcement ann = new Announcement();
            ann.setState(AnnouncementState.OPEN);
            ann.setRequestedGenres(Arrays.asList(MusicalGenre.ROCK, MusicalGenre.R_B));
            ann.setStartEventDay(LocalDate.now().plusDays(10));
            ann.setStartEventTime(LocalTime.of(21, 30));
            ann.setDuration(Duration.ofMinutes(100));
            ann.setCachet(200.0);
            ann.setDeposit(100.0);
            ann.setDescription(".");
            ann.setVenue(venue);
            ann.setDoesUnreleased(true);
            ann.setRequestedTypesArtist(Arrays.asList(TypeArtist.SINGER, TypeArtist.BAND));
            list.add(ann);
        }

        return list;
    }

    @Override
    public void updateAnnouncementState(Announcement ann) throws DatabaseException {

    }

    @Override
    public List<Announcement> findClosedByIdVenue(int venueId) throws DatabaseException {
        return List.of();
    }

    @Override
    public Announcement findByApplicationId(int id) throws DatabaseException {
        return null;
    }
}
