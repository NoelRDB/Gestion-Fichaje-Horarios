package com.noelrdb.gestion_fichajes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.noelrdb.gestion_fichajes.menu.MenuController;

@SpringBootApplication
public class GestionFichajesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionFichajesApplication.class, args);
    }

    @Bean
    CommandLineRunner run(MenuController menuController) {
        return args -> {
            menuController.mostrarMenuPrincipal();
        };
    }
}
