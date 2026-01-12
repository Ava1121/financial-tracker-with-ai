package com.piems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
public class PiemsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiemsApplication.class, args);
    }

}