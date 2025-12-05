package com.noelrdb.gestion_fichajes.menu;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.noelrdb.gestion_fichajes.signing.controller.SigningController;
import com.noelrdb.gestion_fichajes.worker.controller.WorkerController;

@Component
public class MenuController {

    private final WorkerController workerController;
    private final SigningController signingController;
    private final Scanner scanner;

    public MenuController(WorkerController workerController, SigningController signingController) {
        this.workerController = workerController;
        this.signingController = signingController;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenuPrincipal() {
        int opcion = 0;

        do {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    SISTEMA DE GESTIÓN DE FICHAJES     ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Fichaje de Entrada");
            System.out.println("2. Fichaje de Salida");
            System.out.println("3. Ver Trabajadores Activos");
            System.out.println("4. Ver Todos los Fichajes");
            System.out.println("5. Ver Mis Fichajes");
            System.out.println("6. Gestión de Trabajadores (Admin)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1 -> signingController.signIn();
                    case 2 -> signingController.signOut();
                    case 3 -> signingController.showActiveWorkers();
                    case 4 -> signingController.showAllSignings();
                    case 5 -> signingController.showWorkerSignings();
                    case 6 -> menuTrabajadores();
                    case 0 -> System.out.println("Saliendo del programa...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer en caso de error
            }
        } while (opcion != 0);

        scanner.close();
    }

    private void menuTrabajadores() {
        int opcion = 0;

        do {
            System.out.println("\n=== GESTIÓN DE TRABAJADORES ===");
            System.out.println("1. Crear trabajador");
            System.out.println("2. Listar trabajadores");
            System.out.println("3. Buscar trabajador por ID");
            System.out.println("4. Actualizar trabajador");
            System.out.println("5. Eliminar trabajador");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> workerController.createWorker();
                case 2 -> workerController.listAllWorkers();
                case 3 -> workerController.findWorkerById();
                case 4 -> workerController.updateWorker();
                case 5 -> workerController.deleteWorker();
                case 0 -> {
                }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }
}