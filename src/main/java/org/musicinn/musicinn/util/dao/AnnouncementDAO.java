package org.musicinn.musicinn.util.dao;

import org.musicinn.musicinn.model.Announcement;
import org.musicinn.musicinn.model.SchedulableEvent;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
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
}
