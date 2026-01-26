package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.AnnouncementDAO;
import org.musicinn.musicinn.util.dao.interfaces.VenueDAO;
import org.musicinn.musicinn.util.enumerations.*;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnnouncementDAODatabase implements AnnouncementDAO {
    @Override
    public List<SchedulableEvent> getEventsByDate(LocalDate startingDate) throws DatabaseException {
        List<SchedulableEvent> events = new ArrayList<>();
        String managerUser = Session.getSingletonInstance().getUsername();

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        String query = "SELECT start_time, duration FROM announcements WHERE start_day = ? AND venues_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            VenueDAO venueDAO = DAOFactory.getVenueDAO();
            int venueId = ((VenueDAODatabase) venueDAO).getActiveVenueIdByManager(managerUser);

            pstmt.setDate(1, java.sql.Date.valueOf(startingDate));
            pstmt.setInt(2, venueId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Announcement announcement = new Announcement();
                    announcement.setStartEventDay(startingDate);
                    announcement.setStartEventTime(rs.getTime("start_time").toLocalTime());
                    announcement.setDuration(Duration.ofMinutes(rs.getLong("duration")));

                    // SchedulableEvent è l'interfaccia o classe base per Calendar
                    events.add(announcement);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
        return events;
    }

    @Override
    public void save(Announcement announcement) throws DatabaseException {
        String managerUser = Session.getSingletonInstance().getUsername();

        String insertAnnouncement = "INSERT INTO announcements (start_day, start_time, duration, cachet, deposit, does_unreleased, description, state, venues_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();
        try {
            conn.setAutoCommit(false); // Inizio Transazione

            VenueDAO venueDAO = DAOFactory.getVenueDAO();
            int venueId = ((VenueDAODatabase) venueDAO).getActiveVenueIdByManager(managerUser);

            try (PreparedStatement pstmt = conn.prepareStatement(insertAnnouncement, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDate(1, java.sql.Date.valueOf(announcement.getStartEventDay()));
                pstmt.setTime(2, java.sql.Time.valueOf(announcement.getStartEventTime()));
                pstmt.setLong(3, announcement.getDuration().toMinutes()); // Salviamo in minuti
                pstmt.setDouble(4, announcement.getCachet());
                pstmt.setDouble(5, announcement.getDeposit());

                if (announcement.getDoesUnreleased() == null) pstmt.setNull(6, java.sql.Types.BOOLEAN);
                else pstmt.setBoolean(6, announcement.getDoesUnreleased());

                pstmt.setString(7, announcement.getDescription());
                pstmt.setString(8, announcement.getState().toString());
                pstmt.setInt(9, venueId);

                pstmt.executeUpdate();

                // Recuperiamo l'ID generato per inserire i generi e i tipi
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    saveGenres(id, announcement.getRequestedGenres(), conn);
                    saveTypes(id, announcement.getRequestedTypesArtist(), conn);
                }

                conn.commit(); // Fine Transazione con successo
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
    }

    private void saveGenres(int announcementId, List<MusicalGenre> genres, Connection conn) throws SQLException {
        String query = "INSERT INTO announcements_has_genres (announcements_id, genres_genre) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (MusicalGenre g : genres) {
                pstmt.setInt(1, announcementId);
                pstmt.setString(2, g.name());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void saveTypes(int announcementId, List<TypeArtist> types, Connection conn) throws SQLException {
        String query = "INSERT INTO announcements_has_artist_types (announcements_id, artist_types_type) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (TypeArtist t : types) {
                pstmt.setInt(1, announcementId);
                pstmt.setString(2, t.name());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public List<Announcement> findActiveByGenres(List<MusicalGenre> artistGenres, int page, int pageSize) throws DatabaseException {
        List<Announcement> announcements = new ArrayList<>();
        // Usiamo l'ID della Venue come chiave della mappa
        Map<Integer, ManagerRider> riderMapByVenueId = new HashMap<>();
        // Mappa per associare gli ID annuncio agli oggetti Announcement per il batch loading dei generi/tipi
        Map<Integer, Announcement> announcementMap = new HashMap<>();

        String placeholders = artistGenres.stream().map(g -> "?").collect(Collectors.joining(", "));
        // Notare che usiamo r.venues_id invece di r.id
        String query = "SELECT a.*, a.id as ann_id, v.*, r.min_length_stage, r.min_width_stage " +
                "FROM announcements a " +
                "JOIN venues v ON a.venues_id = v.id " +
                "JOIN manager_riders r ON v.id = r.venues_id " +
                "WHERE a.state = 'OPEN' " +
                "AND EXISTS (SELECT 1 FROM announcements_has_genres ag WHERE ag.announcements_id = a.id AND ag.genres_genre IN (" + placeholders + ")) " +
                "LIMIT ? OFFSET ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            int i = 1;
            for (MusicalGenre genre : artistGenres) pstmt.setString(i++, genre.name());
            pstmt.setInt(i++, pageSize);
            pstmt.setInt(i, page * pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Announcement ann = new Announcement();
                    mapAnnouncement(ann, rs);
                    int annId = rs.getInt("ann_id");

                    Venue venue = new Venue();
                    mapVenue(venue, rs);

                    // Recuperiamo l'ID della venue (che è la PK del rider)
                    int venueId = rs.getInt("venues_id");

                    ManagerRider rider = new ManagerRider();
                    mapRider(rider, rs);

                    // Associamo il rider alla venueId nella mappa
                    riderMapByVenueId.put(venueId, rider);
                    // Registriamo l'annuncio nella mappa
                    announcementMap.put(annId, ann);

                    venue.setRider(rider);
                    ann.setVenue(venue);
                    announcements.add(ann);
                }
            }

            if (!riderMapByVenueId.isEmpty()) {
                // 1. Carichiamo l'attrezzatura del locale
                loadAllEquipments(conn, riderMapByVenueId);
            }

            if (!announcementMap.isEmpty()) {
                // 2. Carichiamo i requisiti dell'annuncio (Generi e Tipi Artista)
                loadAnnouncementRequirements(conn, announcementMap);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
        return announcements;
    }

    public List<Announcement> findByManager(String managerUsername) throws DatabaseException {
        List<Announcement> announcements = new ArrayList<>();
        Map<Integer, Announcement> announcementMap = new HashMap<>();

        // Query che recupera gli annunci del manager e conta le candidature tramite subquery
        String query = "SELECT a.*, a.id as ann_id, v.*, " +
                "(SELECT COUNT(*) FROM applications WHERE announcements_id = a.id) as app_count " +
                "FROM announcements a " +
                "JOIN venues v ON a.venues_id = v.id " +
                "WHERE v.manager_username = ? " +
                "ORDER BY a.start_day DESC";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, managerUsername);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Announcement ann = new Announcement();
                    mapAnnouncement(ann, rs);

                    // Impostiamo il conteggio delle candidature (aggiungi il campo in Announcement se non c'è)
                    ann.setNumOfApplications(rs.getInt("app_count"));

                    Venue venue = new Venue();
                    mapVenue(venue, rs);
                    ann.setVenue(venue);

                    announcements.add(ann);
                    announcementMap.put(ann.getId(), ann);
                }
            }

            // RIUTILIZZO: Carichiamo generi e tipi richiesti usando il metodo che hai già scritto
            if (!announcementMap.isEmpty()) {
                loadAnnouncementRequirements(conn, announcementMap);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero degli annunci del manager.");
        }
        return announcements;
    }

    private void mapAnnouncement(Announcement ann, ResultSet rs) throws SQLException {
        ann.setId(rs.getInt("ann_id"));
        ann.setStartEventDay(rs.getDate("start_day").toLocalDate());
        ann.setStartEventTime(rs.getTime("start_time").toLocalTime());
        ann.setDuration(Duration.ofMinutes(rs.getLong("duration")));
        ann.setCachet(rs.getDouble("cachet"));
        ann.setDeposit(rs.getDouble("deposit"));
        ann.setDescription(rs.getString("description"));
        ann.setState(AnnouncementState.valueOf(rs.getString("state")));
    }

    private void mapVenue(Venue venue, ResultSet rs) throws SQLException {
        venue.setName(rs.getString("name"));
        venue.setAddress(rs.getString("address"));
        venue.setCity(rs.getString("city"));
        venue.setTypeVenue(TypeVenue.valueOf(rs.getString("type_venue")));
    }

    private void mapRider(ManagerRider rider, ResultSet rs) throws SQLException {
        rider.setMinLengthStage(rs.getInt("min_length_stage"));
        rider.setMinWidthStage(rs.getInt("min_width_stage"));
        // Liste inizializzate vuote pronte per il batch loading
        rider.setMixers(new ArrayList<>());
        rider.setInputs(new ArrayList<>());
        rider.setOutputs(new ArrayList<>());
        rider.setOthers(new ArrayList<>());
    }

    private void loadAnnouncementRequirements(Connection conn, Map<Integer, Announcement> announcementMap) throws SQLException {
        String ids = announcementMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(","));

        // Caricamento Generi richiesti dall'annuncio
        String sqlGenres = "SELECT announcements_id, genres_genre FROM announcements_has_genres WHERE announcements_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlGenres)) {
            while (rs.next()) {
                Announcement ann = announcementMap.get(rs.getInt("announcements_id"));
                if (ann != null) {
                    ann.getRequestedGenres().add(MusicalGenre.valueOf(rs.getString("genres_genre")));
                }
            }
        }

        // Caricamento Tipi di Artista richiesti (es. BAND, SOLO, DUO)
        String sqlTypes = "SELECT announcements_id, artist_types_type FROM announcements_has_artist_types WHERE announcements_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlTypes)) {
            while (rs.next()) {
                Announcement ann = announcementMap.get(rs.getInt("announcements_id"));
                if (ann != null) {
                    ann.getRequestedTypesArtist().add(TypeArtist.valueOf(rs.getString("artist_types_type")));
                }
            }
        }
    }

    private void loadAllEquipments(Connection conn, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String ids = riderMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(","));

        loadMixers(conn, ids, riderMap);
        loadStageBoxes(conn, ids, riderMap);
        loadMics(conn, ids, riderMap);
        loadDIBoxes(conn, ids, riderMap);
        loadMonitors(conn, ids, riderMap);
        loadMicStands(conn, ids, riderMap);
        loadCables(conn, ids, riderMap);
    }

    private void loadMixers(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM mixers WHERE manager_riders_venues_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    Mixer mixer = new Mixer();
                    mixer.setInputChannels(rs.getInt("input_channels"));
                    mixer.setAuxSends(rs.getInt("aux_sends"));
                    mixer.setDigital(rs.getBoolean("digital"));
                    mixer.setHasPhantomPower(rs.getBoolean("phantom_power"));
                    r.getMixers().add(mixer);
                }
            }
        }
    }

    private void loadStageBoxes(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM Stage_Boxes WHERE manager_riders_venues_id IN (" + ids + ")";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    StageBox sb = new StageBox();
                    sb.setInputChannels(rs.getInt("input_channels"));
                    sb.setDigital(rs.getBoolean("digital"));
                    r.getStageBoxes().add(sb);
                }
            }
        }
    }

    private void loadMics(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM mic_sets WHERE manager_riders_venues_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    r.getInputs().add(new MicrophoneSet(rs.getInt("quantity"), rs.getBoolean("phantom")));
                }
            }
        }
    }

    private void loadDIBoxes(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM di_box_sets WHERE manager_riders_venues_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    r.getInputs().add(new DIBoxSet(rs.getInt("quantity"), rs.getBoolean("active")));
                }
            }
        }
    }

    private void loadMonitors(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM monitor_sets WHERE manager_riders_venues_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    r.getOutputs().add(new MonitorSet(rs.getInt("quantity"), rs.getBoolean("powered")));
                }
            }
        }
    }

    private void loadMicStands(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM mic_stand_sets WHERE manager_riders_venues_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    r.getOthers().add(new MicStandSet(rs.getInt("quantity"), rs.getBoolean("tall")));
                }
            }
        }
    }

    private void loadCables(Connection conn, String ids, Map<Integer, ManagerRider> riderMap) throws SQLException {
        String sql = "SELECT * FROM cable_sets WHERE manager_riders_venues_id IN (" + ids + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ManagerRider r = riderMap.get(rs.getInt("manager_riders_venues_id"));
                if (r != null) {
                    r.getOthers().add(new CableSet(rs.getInt("quantity"), CableFunction.valueOf(rs.getString("function"))));
                }
            }
        }
    }

    @Override
    public void updateAnnouncementState(Announcement ann) throws DatabaseException {
        String query = "UPDATE announcements SET state = 'CLOSED' WHERE id = ? AND state = 'OPEN'";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, ann.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Impossibile chiudere l'annuncio: ID non trovato o già chiuso.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento dello stato dell'annuncio");
        }
    }

    @Override
    public List<Announcement> findClosedByIdVenue(int venueId) throws DatabaseException {
        List<Announcement> announcements = new ArrayList<>();

        // Query per selezionare gli annunci chiusi di una specifica venue
        String sql = "SELECT * FROM announcements WHERE venues_id = ? AND state = 'CLOSED'";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, venueId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Announcement ann = new Announcement();

                    // Mappatura dei campi della tabella announcements
                    ann.setId(rs.getInt("id"));
                    ann.setCachet(rs.getDouble("cachet"));
                    ann.setDeposit(rs.getDouble("deposit"));

                    // Conversione date e orari SQL -> Java Time
                    ann.setStartEventDay(rs.getDate("start_day").toLocalDate());
                    ann.setStartEventTime(rs.getTime("start_time").toLocalTime());

                    announcements.add(ann);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero degli annunci chiusi: " + e.getMessage());
        }

        return announcements;
    }

    @Override
    public Announcement findByApplicationId(int id) throws DatabaseException {
        Announcement announcement = null;

        // Partiamo dall'ID dell'applicazione per trovare l'annuncio corrispondente
        String sql = "SELECT a.* FROM announcements a " +
                "JOIN applications app ON a.id = app.announcements_id " +
                "WHERE app.id = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    announcement = new Announcement();
                    announcement.setId(rs.getInt("id"));
                    announcement.setCachet(rs.getDouble("cachet"));
                    announcement.setDeposit(rs.getDouble("deposit"));
                    announcement.setStartEventDay(rs.getDate("start_day").toLocalDate());
                    announcement.setStartEventTime(rs.getTime("start_time").toLocalTime());
                    announcement.setState(AnnouncementState.valueOf(rs.getString("state")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero dell'annuncio tramite applicazione: " + e.getMessage());
        }

        return announcement;
    }
}
