package com.noelrdb.gestion_fichajes.signing.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Controller;

import com.noelrdb.gestion_fichajes.signing.entity.Signing;
import com.noelrdb.gestion_fichajes.signing.service.SigningService;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.service.WorkerService;

@Controller
public class SigningController {
    
    private final SigningService signingService;
    private final WorkerService workerService;
    public Scanner teclado = new Scanner(System.in);

    public SigningController(SigningService signingService, WorkerService workerService) {
        this.signingService = signingService;
        this.workerService = workerService;
    }

    // Método para autenticar trabajador
    private Worker authenticateWorker() {
        System.out.println("\n=== AUTENTICACIÓN DE TRABAJADOR ===");
        System.out.print("Introduce tu DNI: ");
        String dni = teclado.nextLine().trim();
        
        System.out.print("Introduce tu código: ");
        int code = teclado.nextInt();
        teclado.nextLine(); // Limpiar buffer
        
        Worker worker = workerService.authenticate(dni, code);
        
        if (worker == null) {
            System.out.println("DNI o código incorrectos.");
        }
        
        return worker;
    }

    // Metodo para realizar el fichaje de entrada
    public void signIn() {
        // 1. Autenticar trabajador
        Worker worker = authenticateWorker();
        
        if (worker == null) {
            return;
        }
        
        // 2. Realizar fichaje
        try {
            signingService.signIn(worker.getId());
            System.out.println("Fichaje de ENTRADA realizado con éxito");
            System.out.println("   Trabajador: " + worker.getName() + " " + worker.getSurname());
            System.out.println("   Hora: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Metodo para realizar el fichaje de salida
    public void signOut() {
        // 1. Autenticar trabajador
        Worker worker = authenticateWorker();
        
        if (worker == null) {
            return;
        }
        
        // 2. Realizar fichaje
        try {
            signingService.signOut(worker.getId());
            System.out.println("Fichaje de SALIDA realizado con éxito");
            System.out.println("   Trabajador: " + worker.getName() + " " + worker.getSurname());
            System.out.println("   Hora: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // Método para ver trabajadores activos (los que están trabajando ahora)
    public void showActiveWorkers() {
        System.out.println("\n=== TRABAJADORES EN ACTIVO ===");
        List<Worker> activeWorkers = workerService.findActiveWorkers();
        
        if (activeWorkers.isEmpty()) {
            System.out.println("No hay trabajadores activos en este momento.");
            return;
        }
        
        System.out.println("Total: " + activeWorkers.size() + " trabajador(es) trabajando");
        System.out.println("─────────────────────────────────────────────────");
        for (Worker w : activeWorkers) {
            System.out.println("• " + w.getName() + " " + w.getSurname() + " (ID: " + w.getId() + ")");
        }
    }
    
    // Método para ver todos los fichajes del día
    public void showAllSignings() {
        System.out.println("\n=== REGISTRO DE FICHAJES ===");
        List<Signing> signings = signingService.getAllSignings();
        
        if (signings.isEmpty()) {
            System.out.println("No hay fichajes registrados.");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("Total: " + signings.size() + " fichaje(s)");
        System.out.println("─────────────────────────────────────────────────");
        
        for (Signing s : signings) {
            Worker worker = s.getMiWorker();
            String entrada = s.getSignIn() != null ? s.getSignIn().format(formatter) : "N/A";
            String salida = s.getSignOut() != null ? s.getSignOut().format(formatter) : "Aún trabajando";
            
            System.out.println("Fichaje #" + s.getId());
            System.out.println("  Trabajador: " + worker.getName() + " " + worker.getSurname());
            System.out.println("  Entrada: " + entrada);
            System.out.println("  Salida: " + salida);
            System.out.println("─────────────────────────────────────────────────");
        }
    }
    
    // Método para ver fichajes de un trabajador específico
    public void showWorkerSignings() {
        Worker worker = authenticateWorker();
        
        if (worker == null) {
            return;
        }
        
        System.out.println("\n=== FICHAJES DE " + worker.getName().toUpperCase() + " " + worker.getSurname().toUpperCase() + " ===");
        List<Signing> signings = signingService.getSigningsByWorkerId(worker.getId());
        
        if (signings.isEmpty()) {
            System.out.println("No tienes fichajes registrados.");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("Total: " + signings.size() + " fichaje(s)");
        System.out.println("─────────────────────────────────────────────────");
        
        for (Signing s : signings) {
            String entrada = s.getSignIn() != null ? s.getSignIn().format(formatter) : "N/A";
            String salida = s.getSignOut() != null ? s.getSignOut().format(formatter) : "Aún trabajando";
            
            System.out.println("Fichaje #" + s.getId());
            System.out.println("  Entrada: " + entrada);
            System.out.println("  Salida: " + salida);
            System.out.println("─────────────────────────────────────────────────");
        }
    }
}
