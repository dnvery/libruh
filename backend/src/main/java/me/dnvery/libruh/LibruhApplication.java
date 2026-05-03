package me.dnvery.libruh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LibruhApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibruhApplication.class, args);
    }
}