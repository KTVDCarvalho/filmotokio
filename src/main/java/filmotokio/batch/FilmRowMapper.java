package filmotokio.batch;

import filmotokio.domain.Film;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

// Maps database rows to Film objects
// Used by Spring Batch for data conversion
public class FilmRowMapper implements RowMapper<Film> {

    // Maps ResultSet row to Film object
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        // Map database columns to Film fields
        film.setId(rs.getLong("id"));
        film.setTitle(rs.getString("title"));
        film.setYear(rs.getInt("year"));
        film.setDuration(rs.getInt("duration"));
        film.setSynopsis(rs.getString("synopsis"));
        film.setPoster(rs.getString("poster"));
        return film;
    }
}
