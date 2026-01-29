package org.musicinn.musicinn.util.dao.database;

import org.musicinn.musicinn.model.Artist;
import org.musicinn.musicinn.util.DBConnectionManager;
import org.musicinn.musicinn.util.dao.DAOFactory;
import org.musicinn.musicinn.util.dao.interfaces.ArtistDAO;
import org.musicinn.musicinn.util.dao.interfaces.UserDAO;
import org.musicinn.musicinn.util.enumerations.MusicalGenre;
import org.musicinn.musicinn.util.enumerations.TypeArtist;
import org.musicinn.musicinn.util.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAODatabase implements ArtistDAO {
    @Override
    public void create(Artist artist) {
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            // 1. Delega la creazione dell'utente base
            UserDAO userDAO = DAOFactory.getUserDAO();
            userDAO.insertBaseUser(artist, conn);

            // 2. Esegue l'inserimento specifico dell'artista
            String sqlArtist = "INSERT INTO artists (username, stage_name, city, address, does_unreleased) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlArtist)) {
                ps.setString(1, artist.getUsername());
                ps.setString(2, artist.getStageName());
                ps.setString(3, artist.getCity());
                ps.setString(4, artist.getAddress());
                ps.setBoolean(5, artist.getDoesUnreleased());
                ps.executeUpdate();
            }

            // 3. Inserimento generi (Responsabilità specifica)
            insertGenres(artist, conn);

            insertType(artist, conn);

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void insertGenres(Artist artist, Connection conn) throws SQLException {
        String sql = "INSERT INTO artists_has_genres (artists_username, genres_genre) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (MusicalGenre genre : artist.getGenresList()) {
                ps.setString(1, artist.getUsername());
                ps.setString(2, genre.toString());
                ps.executeUpdate();
            }
        }
    }

    private void insertType(Artist artist, Connection conn) throws SQLException {
        String sql = "INSERT INTO artists_has_artist_types (artists_username, artist_types_type) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artist.getUsername());
            ps.setString(2, artist.getTypeArtist().toString());
            ps.executeUpdate();
        }
    }

    public Artist read(String username) throws DatabaseException {
        Artist artist = null;
        String query = "SELECT a.stage_name, a.address, a.city, a.does_unreleased, t.artist_types_type " +
                "FROM artists a " +
                "JOIN artists_has_artist_types t ON a.username = t.artists_username " +
                "WHERE a.username = ?";

        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    artist = new Artist();
                    artist.setUsername(username);
                    artist.setStageName(rs.getString("stage_name"));
                    artist.setAddress(rs.getString("address"));
                    artist.setCity(rs.getString("city"));
                    artist.setDoesUnreleased(rs.getBoolean("does_unreleased"));
                    artist.setTypeArtist(TypeArtist.valueOf(rs.getString("artist_types_type")));

                    // Carichiamo i generi associati all'artista
                    artist.setGenresList(loadArtistGenres(username));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
        return artist;
    }

    public List<MusicalGenre> loadArtistGenres(String username) throws DatabaseException {
        List<MusicalGenre> genres = new ArrayList<>();
        String query = "SELECT genres_genre FROM artists_has_genres WHERE artists_username = ?";
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Convertiamo la stringa del DB nel valore dell'Enum
                    genres.add(MusicalGenre.valueOf(rs.getString("genres_genre")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore: Annuncio non trovato. Impossibile completare la candidatura.");
        }
        return genres;
    }

    @Override
    public String findStageNameByAnnouncementId(int announcementId) throws DatabaseException {
        String sql = "SELECT a.stage_name FROM artists a " +
                "JOIN applications app ON a.username = app.artists_username " +
                "WHERE app.announcements_id = ? AND app.state = 'ACCEPTED'";

        String stageName = null;
        Connection conn = DBConnectionManager.getSingletonInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, announcementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stageName = rs.getString("stage_name");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore nella ricerca del nome d'arte dell'artista.");
        }

        return stageName;
    }
}
