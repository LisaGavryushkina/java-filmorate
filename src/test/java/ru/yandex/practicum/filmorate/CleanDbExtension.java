package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class CleanDbExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        JdbcTemplate jdbcTemplate = SpringExtension.getApplicationContext(extensionContext).getBean(JdbcTemplate.class);
        jdbcTemplate.update("delete from film_genre");
        jdbcTemplate.update("delete from film_like");
        jdbcTemplate.update("delete from film");
        jdbcTemplate.update("delete from friendship_request");
        jdbcTemplate.update("delete from user_");

    }
}
