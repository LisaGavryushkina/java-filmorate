package ru.yandex.practicum.filmorate.storage.user;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isUserExisting(User user) {
        User existingUser = DataAccessUtils.singleResult(jdbcTemplate.query("select * from user_ where email = ?",
                (resultSet, rowNum) -> mapRowToUser(resultSet), user.getEmail()));
        return existingUser != null;
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        User user = new User(
                id,
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());
        user.setFriends(getIdAllFriends(id));
        return user;
    }

    private List<Integer> getIdAllFriends(int userId) {
        String sqlQuery = "select user_to_id from friendship_request where user_from_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }

    @Override
    public User add(User user) {
        if (isUserExisting(user)) {
            return getUser(user.getId());
        }
        String sqlQuery = "insert into user_ (email, login, name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return getUser(id);
    }

    @Override
    public User update(User user) {
        getUser(user.getId());
        String sqlQuery = "update user_ set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return getUser(user.getId());
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from user_";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToUser(resultSet));
    }

    @Override
    public User getUser(int id) {
        String sqlQuery = "select * from user_ where user_id = ?";
        User user = DataAccessUtils.singleResult(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> mapRowToUser(resultSet), id));
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    @Override
    public List<User> getAllFriends(int userId) {
        String sqlQuery = "select * from user_ where user_id in (select user_to_id from friendship_request where " +
                "user_from_id = ?)";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> mapRowToUser(resultSet), userId);
    }

    @Override
    public List<User> getAllCommonFriends(int userId, int otherId) {
        getUser(userId);
        getUser(otherId);
        List<Integer> userFriends = getIdAllFriends(userId);
        List<Integer> otherFriends = getIdAllFriends(otherId);

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public User addFriend(int userId, int friendId) {
        getUser(userId);
        getUser(friendId);

        String sqlQuery = "insert into friendship_request (user_from_id, user_to_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);

        return getUser(userId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        if (!getIdAllFriends(userId).contains(friendId)) {
            throw new UserNotFoundException(friendId);
        }
        String sqlQuery = "delete from friendship_request where user_from_id = ? and " +
                "user_to_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);

        return getUser(userId);
    }
}
