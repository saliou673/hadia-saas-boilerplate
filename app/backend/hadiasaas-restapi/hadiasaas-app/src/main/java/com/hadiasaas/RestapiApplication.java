package com.hadiasaas;

import com.hadiasaas.util.EnvironmentFileLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;

@SpringBootApplication
public class RestapiApplication {

    public static void main(String[] args) throws FileNotFoundException {
        EnvironmentFileLoader.load(); // The .env file should be loaded before the spring boot app starts running.
        SpringApplication.run(RestapiApplication.class, args);
    }

}
