package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {


    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
