package ru.yandex.practicum.filmorate.storage.film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.RatingMpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isFilmExisting(Film film) {
        Film existingFilm = DataAccessUtils.singleResult(jdbcTemplate.query("select * from film where film_id = ?",
                (resultSet, rowNum) -> mapRowToFilm(resultSet), film.getId()));
        return existingFilm != null;
    }

    private Film mapRowToFilm(ResultSet resultSet) throws SQLException {
        int filmId = resultSet.getInt("film_id");
        int ratingMpaId = resultSet.getInt("rating_id");
        RatingMpa mpa = new RatingMpa(ratingMpaId, getRatingName(ratingMpaId));

        Film film = new Film(
                filmId,
                resultSet.getString("name"),
                mpa,
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"),
                getGenres(filmId));
        film.setLikes(getLikes(filmId));
        return film;
    }

    private String getRatingName(int ratingMpaId) {
        return Objects.requireNonNull(jdbcTemplate.queryForObject("select name from rating where rating_id =" +
                " ?", String.class, ratingMpaId));
    }

    private Genre mapRowToGenre(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }

    private RatingMpa mapRowToRatingMpa(ResultSet resultSet) throws SQLException {
        return new RatingMpa(
                resultSet.getInt("rating_id"),
                resultSet.getString("name")
        );
    }

    private List<Genre> getGenres(int filmId) {
        String sqlQuery = "select g.genre_id, g.name from film_genre as f join genre as g on f.genre_id = g.genre_id " +
                "where film_id = ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToGenre(resultSet), filmId);
    }

    private Set<Integer> getLikes(int filmId) {
        String sqlQuery = "select user_id from film_like where film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId));
    }

    private Film addGenre(int filmId, int genreId) {
        String sqlQuery = "insert into film_genre (film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
        return getFilm(filmId);
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("delete from film_genre where film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            addGenre(film.getId(), genre.getId());
        }
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "insert into film (name, rating_id, description, release_date, duration) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setInt(2, film.getMpa().getId());
            statement.setString(3, film.getDescription());
            statement.setDate(4, Date.valueOf(film.getReleaseDate()));
            statement.setInt(5, film.getDuration());

            return statement;
        }, keyHolder);

        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        if (film.getGenres() != null) {
            updateFilmGenres(film);
        }
        return getFilm(filmId);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update film set name = ?, rating_id = ?, description = ?, release_date = ?, duration = ? " +
                "where film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getMpa().getId(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        if (film.getGenres() != null) {
            updateFilmGenres(film);
        }
        return getFilm(film.getId());
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select * from film";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToFilm(resultSet));
    }

    @Override
    public void clear() {
        String sqlQuery = "delete from film";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Film getFilm(int id) {
        String sqlQuery = "select * from film where film_id = ?";
        Film film = DataAccessUtils.singleResult(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> mapRowToFilm(resultSet), id));
        if (film == null) {
            throw new FilmNotFoundException(id);
        }
        return film;
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        String sqlQueryForTopFilms =
                "select f.* from film as f join film_like as fl on f.film_id = fl.film_id group by f" +
                        ".film_id order by count(fl.user_id) desc limit ?;";
        List<Film> popularFilms = jdbcTemplate.query(sqlQueryForTopFilms,
                (resultSet, rowNum) -> mapRowToFilm(resultSet), count);
        if (popularFilms.isEmpty()) {
            String sqlQueryForFilms = "select * from film limit ?";
            return jdbcTemplate.query(sqlQueryForFilms, (resultSet, rowNum) -> mapRowToFilm(resultSet), count);
        }
        return popularFilms;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        getFilm(filmId);
        String sqlQuery = "insert into film_like (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        return getFilm(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        boolean isLikeExists = Boolean.TRUE.equals(jdbcTemplate.queryForObject("select exists(select from film_like " +
                "where film_id = ? and user_id = ?)", Boolean.class, filmId, userId));
        if (!isLikeExists) {
            throw new LikeNotFoundException(filmId, userId);
        }
        String sqlQuery = "delete from film_like where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        return getFilm(filmId);
    }

    @Override
    public List<Genre> findAllGenres() {
        String sqlQuery = "select * from genre";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToGenre(resultSet));
    }

    @Override
    public Genre findGenreById(int genreId) {
        String sqlQuery = "select * from genre where genre_id = ?";
        Genre genre = DataAccessUtils.singleResult(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> mapRowToGenre(resultSet), genreId));
        if (genre == null) {
            throw new GenreNotFoundException(genreId);
        }
        return genre;
    }

    @Override
    public List<RatingMpa> findAllRatingMpa() {
        String sqlQuery = "select * from rating";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToRatingMpa(resultSet));
    }

    @Override
    public RatingMpa findRatingMpaById(int ratingMpaId) {
        String sqlQuery = "select * from rating where rating_id = ?";
        RatingMpa mpa = DataAccessUtils.singleResult(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> mapRowToRatingMpa(resultSet), ratingMpaId));
        if (mpa == null) {
            throw new RatingMpaNotFoundException(ratingMpaId);
        }
        return mpa;
    }
}
