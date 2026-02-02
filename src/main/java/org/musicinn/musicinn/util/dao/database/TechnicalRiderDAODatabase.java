package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.*;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.Session;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.TechnicalRiderDAO;
import org.musicinn.musicinn.util.enumerations.CablePurpose;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicalRiderDAODatabase implements TechnicalRiderDAO {
    private static final String QUANTITY_COLUMN = "quantity";

    @Override
    public void create(TechnicalRider rider) throws DatabaseException {
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();
        String username = Session.getSingletonInstance().getUser().getUsername();

        try {
            conn.setAutoCommit(false);

            String artistUser = null;
            Integer venueId = null;

            // 1. Salvataggio Testata (Header) e identificazione proprietario
            if (rider instanceof ArtistRider ar) {
                artistUser = username;
                deleteExistingComponents(conn, artistUser, null);
                saveArtistRiderHeader(conn, ar, artistUser);

                // Salvataggio componenti specifici Artist
                if (ar.getFohMixer() != null) saveMixer(conn, ar.getFohMixer(), artistUser, null);
                if (ar.getStageMixer() != null) saveMixer(conn, ar.getStageMixer(), artistUser, null);
                if (ar.getStageBox() != null) saveStageBox(conn, ar.getStageBox(), artistUser, null);

            } else if (rider instanceof ManagerRider mr) {
                // Assumiamo che il ManagerRider sappia a quale venue si riferisce
                venueId = ((VenueDAODatabase) DAOFactory.getVenueDAO()).getActiveVenueIdByManager(username);
                deleteExistingComponents(conn, null, venueId);
                saveManagerRiderHeader(conn, mr, venueId);

                // Salvataggio componenti specifici Manager
                for (Mixer m : mr.getMixers()) saveMixer(conn, m, null, venueId);
                for (StageBox sb : mr.getStageBoxes()) saveStageBox(conn, sb, null, venueId);
            }

            // 2. Salvataggio Equipaggiamento Comune
            saveMicrophones(conn, rider.getInputs(), artistUser, venueId);
            saveDIBoxes(conn, rider.getInputs(), artistUser, venueId);
            saveMonitors(conn, rider.getOutputs(), artistUser, venueId);
            saveMicStands(conn, rider.getOthers(), artistUser, venueId);
            saveCables(conn, rider.getOthers(), artistUser, venueId);

            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        } finally {
            setAutoCommit(conn);
        }
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void setAutoCommit(Connection conn) {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // Nuovo metodo per evitare duplicati quando si aggiorna
    private void deleteExistingComponents(Connection conn, String artistUser, Integer venueId) throws SQLException {
        String[] tables = {"mixers", "stage_boxes", "mic_sets", "di_box_sets", "monitor_sets", "mic_stand_sets", "cable_sets"};
        String column = (artistUser != null) ? "artist_username" : "manager_riders_venues_id";
        Object param = (artistUser != null) ? artistUser : venueId;
        for (String table : tables) {
            String sql = "DELETE FROM " + table + " WHERE " + column + " = ?";
            executeDelete(conn, sql, param);
        }
    }

    private void executeDelete(Connection conn, String sql, Object param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, param);
            ps.executeUpdate();
        }
    }

    // --- METODI PER LE TESTATE ---

    private void saveArtistRiderHeader(Connection conn, ArtistRider ar, String username) throws SQLException {
        String sql = "INSERT INTO artist_riders (artist_username, min_length_stage, min_width_stage) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE min_length_stage=VALUES(min_length_stage), min_width_stage=VALUES(min_width_stage)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, ar.getMinLengthStage());
            ps.setInt(3, ar.getMinWidthStage());
            ps.executeUpdate();
        }
    }

    private void saveManagerRiderHeader(Connection conn, ManagerRider mr, int venueId) throws SQLException {
        String sql = "INSERT INTO manager_riders (venues_id, min_length_stage, min_width_stage) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE min_length_stage=VALUES(min_length_stage), min_width_stage=VALUES(min_width_stage)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, venueId);
            ps.setInt(2, mr.getMinLengthStage());
            ps.setInt(3, mr.getMinWidthStage());
            ps.executeUpdate();
        }
    }

    // --- METODI PER I COMPONENTI ---

    private void saveMixer(Connection conn, Mixer m, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO mixers (input_channels, aux_sends, digital, phantom_power, is_foh, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getInputChannels());
            ps.setInt(2, m.getAuxSends());
            ps.setBoolean(3, m.getDigital());
            ps.setBoolean(4, m.getHasPhantomPower());
            ps.setBoolean(5, m.isFOH());
            setOwner(ps, 6, artistUser, venueId);
            ps.executeUpdate();
        }
    }

    private void saveStageBox(Connection conn, StageBox sb, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO stage_boxes (input_channels, digital, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sb.getInputChannels());
            ps.setBoolean(2, sb.getDigital());
            setOwner(ps, 3, artistUser, venueId);
            ps.executeUpdate();
        }
    }

    private void saveMicrophones(Connection conn, List<InputEquipment> inputs, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO mic_sets (phantom, quantity, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?)";
        for (InputEquipment in : inputs) {
            if (in instanceof MicrophoneSet ms) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setBoolean(1, ms.getNeedsPhantomPower());
                    ps.setInt(2, ms.getQuantity());
                    setOwner(ps, 3, artistUser, venueId);
                    ps.executeUpdate();
                }
            }
        }
    }

    private void saveDIBoxes(Connection conn, List<InputEquipment> inputs, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO di_box_sets (active, quantity, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?)";
        for (InputEquipment in : inputs) {
            if (in instanceof DIBoxSet di) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setBoolean(1, di.getActive());
                    ps.setInt(2, di.getQuantity());
                    setOwner(ps, 3, artistUser, venueId);
                    ps.executeUpdate();
                }
            }
        }
    }

    private void saveMonitors(Connection conn, List<OutputEquipment> outputs, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO monitor_sets (powered, quantity, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?)";
        for (OutputEquipment out : outputs) {
            if (out instanceof MonitorSet ms) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setBoolean(1, ms.getPowered());
                    ps.setInt(2, ms.getQuantity());
                    setOwner(ps, 3, artistUser, venueId);
                    ps.executeUpdate();
                }
            }
        }
    }

    private void saveMicStands(Connection conn, List<OtherEquipment> others, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO mic_stand_sets (tall, quantity, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?)";
        for (OtherEquipment ot : others) {
            if (ot instanceof MicStandSet ss) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setBoolean(1, ss.getTall());
                    ps.setInt(2, ss.getQuantity());
                    setOwner(ps, 3, artistUser, venueId);
                    ps.executeUpdate();
                }
            }
        }
    }

    private void saveCables(Connection conn, List<OtherEquipment> others, String artistUser, Integer venueId) throws SQLException {
        String sql = "INSERT INTO cable_sets (purpose, quantity, artist_username, manager_riders_venues_id) VALUES (?, ?, ?, ?)";
        for (OtherEquipment ot : others) {
            if (ot instanceof CableSet cs) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, cs.getPurpose().toString());
                    ps.setInt(2, cs.getQuantity());
                    setOwner(ps, 3, artistUser, venueId);
                    ps.executeUpdate();
                }
            }
        }
    }

    // Metodo utility per settare le due chiavi esterne (una a valore, una a NULL)
    private void setOwner(PreparedStatement ps, int startIndex, String artistUser, Integer venueId) throws SQLException {
        if (artistUser != null) {
            ps.setString(startIndex, artistUser);
            ps.setNull(startIndex + 1, java.sql.Types.INTEGER);
        } else {
            ps.setNull(startIndex, java.sql.Types.VARCHAR);
            ps.setInt(startIndex + 1, venueId);
        }
    }

    @Override
    public TechnicalRider read(String username, Session.UserRole role) throws DatabaseException {
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();
        TechnicalRider rider = null;

        try {
            String artistUser = null;
            Integer venueId = null;

            // 1. Identifichiamo il proprietario e istanziamo il Bean corretto
            if (role == Session.UserRole.ARTIST) {
                artistUser = username;
                rider = new ArtistRider();
                loadArtistHeader(conn, (ArtistRider) rider, artistUser);
            } else {
                venueId = ((VenueDAODatabase) DAOFactory.getVenueDAO()).getActiveVenueIdByManager(username);
                rider = new ManagerRider();
                // Inseriamo l'id della venue nel bean manager (utile per futuri salvataggi)
                loadManagerHeader(conn, (ManagerRider) rider, venueId);
            }

            // 2. Carichiamo i componenti comuni (liste inputs, outputs, others)
            rider.setInputs(loadInputs(conn, artistUser, venueId));
            rider.setOutputs(loadOutputs(conn, artistUser, venueId));
            rider.setOthers(loadOthers(conn, artistUser, venueId));

            // 3. Carichiamo i Mixer (Logica diversa per Artist/Manager)
            loadMixers(conn, rider, artistUser, venueId);

            // 4. Carichiamo la StageBox
            loadStageBox(conn, rider, artistUser, venueId);

            return rider;

        } catch (SQLException e) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
    }

    private void loadArtistHeader(Connection conn, ArtistRider ar, String username) throws SQLException {
        String sql = "SELECT min_length_stage, min_width_stage FROM artist_riders WHERE artist_username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se hai aggiunto questi setter nel bean ArtistRider:
                    ar.setMinLengthStage(rs.getInt("min_length_stage"));
                    ar.setMinWidthStage(rs.getInt("min_width_stage"));
                }
            }
        }
    }

    private void loadManagerHeader(Connection conn, ManagerRider mr, int venueId) throws SQLException {
        String sql = "SELECT min_length_stage, min_width_stage FROM manager_riders WHERE venues_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se hai aggiunto questi setter nel bean ManagerRider:
                    mr.setMinLengthStage(rs.getInt("min_length_stage"));
                    mr.setMinWidthStage(rs.getInt("min_width_stage"));
                }
            }
        }
    }

    private void loadMixers(Connection conn, TechnicalRider rider, String artistUser, Integer venueId) throws SQLException {
        String sql = "SELECT * FROM mixers WHERE " + getOwnerColumn(artistUser) + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setOwnerParam(ps, artistUser, venueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Mixer m = new Mixer();
                m.setFOH(rs.getBoolean("is_foh"));
                m.setInputChannels(rs.getInt("input_channels"));
                m.setAuxSends(rs.getInt("aux_sends"));
                m.setDigital(rs.getBoolean("digital"));
                m.setHasPhantomPower(rs.getBoolean("phantom_power"));

                if (rider instanceof ArtistRider ar) {
                    if (m.isFOH()) ar.setFohMixer(m); else ar.setStageMixer(m);
                } else if (rider instanceof ManagerRider mr){
                    mr.getMixers().add(m);
                }
            }
        }
    }

    private void loadStageBox(Connection conn, TechnicalRider rider, String artistUser, Integer venueId) throws SQLException {
        String sql = "SELECT * FROM stage_boxes WHERE " + getOwnerColumn(artistUser) + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setOwnerParam(ps, artistUser, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                // Usiamo while perché il Manager potrebbe averne più di una
                while (rs.next()) {
                    StageBox sb = new StageBox();
                    sb.setInputChannels(rs.getInt("input_channels"));
                    sb.setDigital(rs.getBoolean("digital"));

                    if (rider instanceof ArtistRider ar) {
                        ar.setStageBox(sb); // L'artista solitamente ne ha una specifica
                    } else if (rider instanceof ManagerRider mr) {
                        mr.getStageBoxes().add(sb); // Il manager ha una lista di disponibilità
                    }
                }
            }
        }
    }

    private List<InputEquipment> loadInputs(Connection conn, String artistUser, Integer venueId) throws SQLException {
        List<InputEquipment> inputs = new ArrayList<>();
        String ownerCol = getOwnerColumn(artistUser);

        // Query Microfoni
        String sqlMic = "SELECT * FROM mic_sets WHERE " + ownerCol + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlMic)) {
            setOwnerParam(ps, artistUser, venueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                inputs.add(new MicrophoneSet(rs.getInt(QUANTITY_COLUMN), rs.getBoolean("phantom")));
            }
        }

        // Query DI Boxes
        String sqlDi = "SELECT * FROM di_box_sets WHERE " + ownerCol + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDi)) {
            setOwnerParam(ps, artistUser, venueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                inputs.add(new DIBoxSet(rs.getInt(QUANTITY_COLUMN), rs.getBoolean("active")));
            }
        }
        return inputs;
    }

    private List<OutputEquipment> loadOutputs(Connection conn, String artistUser, Integer venueId) throws SQLException {
        List<OutputEquipment> outputs = new ArrayList<>();
        String ownerCol = getOwnerColumn(artistUser);

        String sql = "SELECT * FROM monitor_sets WHERE " + ownerCol + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setOwnerParam(ps, artistUser, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // powered nel DB, quantity nel DB -> MonitorSet(quantity, powered)
                    outputs.add(new MonitorSet(rs.getInt(QUANTITY_COLUMN), rs.getBoolean("powered")));
                }
            }
        }
        return outputs;
    }

    private List<OtherEquipment> loadOthers(Connection conn, String artistUser, Integer venueId) throws SQLException {
        List<OtherEquipment> others = new ArrayList<>();
        String ownerCol = getOwnerColumn(artistUser);

        // 1. Caricamento Aste Microfoniche (mic_stand_sets)
        String sqlStands = "SELECT * FROM mic_stand_sets WHERE " + ownerCol + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlStands)) {
            setOwnerParam(ps, artistUser, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    others.add(new MicStandSet(rs.getInt(QUANTITY_COLUMN), rs.getBoolean("tall")));
                }
            }
        }

        // 2. Caricamento Set di Cavi (cable_sets)
        String sqlCables = "SELECT * FROM cable_sets WHERE " + ownerCol + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlCables)) {
            setOwnerParam(ps, artistUser, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Recuperiamo la stringa dell'ENUM dal DB e la convertiamo nell'oggetto Java
                    String purposeStr = rs.getString("purpose");
                    CablePurpose func = CablePurpose.valueOf(purposeStr);

                    others.add(new CableSet(rs.getInt(QUANTITY_COLUMN), func));
                }
            }
        }
        return others;
    }

    private String getOwnerColumn(String artistUser) {
        return (artistUser != null) ? "artist_username" : "manager_riders_venues_id";
    }

    private void setOwnerParam(PreparedStatement ps, String artistUser, Integer venueId) throws SQLException {
        if (artistUser != null) ps.setString(1, artistUser);
        else ps.setInt(1, venueId);
    }
}
