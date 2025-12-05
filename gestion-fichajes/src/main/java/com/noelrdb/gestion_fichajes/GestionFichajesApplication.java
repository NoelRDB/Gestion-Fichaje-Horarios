package com.noelrdb.gestion_fichajes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionFichajesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionFichajesApplication.class, args);
    }

    // Para ejecutar el menú desde consola, descomentar:
    // @Bean
    // CommandLineRunner run(MenuController menuController) {
    //     return args -> {
    //         menuController.mostrarMenuPrincipal();
    //     };
    // }
}

