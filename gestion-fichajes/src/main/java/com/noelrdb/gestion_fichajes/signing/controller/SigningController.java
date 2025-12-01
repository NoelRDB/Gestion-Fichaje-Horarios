package com.noelrdb.gestion_fichajes.signing.controller;

import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Controller;

import com.noelrdb.gestion_fichajes.signing.service.SigningService;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.service.WorkerService;

@Controller
public class SigningController {
    
    private final SigningService signingService;
    private final WorkerService workerService; // ✅ Inyectar WorkerService directamente
    public Scanner teclado = new Scanner(System.in);

    public SigningController(SigningService signingService, WorkerService workerService) {
        this.signingService = signingService;
        this.workerService = workerService;
    }

    // Metodo para realizar el fichaje de entrada
    public void signIn() {
        System.out.println("--- Fichaje de Entrada ---");
        
        // 1. Listar trabajadores activos
        List<Worker> workers = workerService.findAllWorkers();
        
        if (workers.isEmpty()) {
            System.out.println("No hay trabajadores registrados.");
            return;
        }
        
        System.out.println("Trabajadores disponibles:");
        for (Worker w : workers) {
            System.out.println("ID: " + w.getId() + " - " + w.getName() + " " + w.getSurname());
        }
        
        // 2. Pedir ID del trabajador
        System.out.print("\nIntroduce el ID del trabajador: ");
        int workerId = teclado.nextInt();
        teclado.nextLine(); // Limpiar buffer
        
        // 3. Buscar trabajador
        Worker workerSeleccionado = workerService.findWorkerById(workerId);
        
        if (workerSeleccionado == null) {
            System.out.println(" Trabajador no encontrado.");
            return;
        }
        
        Worker worker = workerSeleccionado;
        
        // 4. Realizar fichaje
        try {
            signingService.signIn(workerId);
            System.out.println("Fichaje de entrada realizado con éxito para: " 
                + worker.getName() + " " + worker.getSurname());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    // Metodo para realizar el fichaje de salida
    public void signOut() {
        System.out.println("--- Fichaje de Salida ---");
        
        // 1. Listar trabajadores activos
        List<Worker> workers = workerService.findAllWorkers();
        
        if (workers.isEmpty()) {
            System.out.println("No hay trabajadores registrados.");
            return;
        }
        
        System.out.println("Trabajadores disponibles:");
        for (Worker w : workers) {
            System.out.println("ID: " + w.getId() + " - " + w.getName() + " " + w.getSurname());
        }
        
        // 2. Pedir ID del trabajador
        System.out.print("\nIntroduce el ID del trabajador: ");
        int workerId = teclado.nextInt();
        teclado.nextLine(); // Limpiar buffer
        
        // 3. Buscar trabajador
        Worker workerSeleccionado = workerService.findWorkerById(workerId);
        
        if (workerSeleccionado == null) {
            System.out.println("❌ Trabajador no encontrado.");
            return;
        }
        
        Worker worker = workerSeleccionado;
        
        // 4. Realizar fichaje
        try {
            signingService.signOut(workerId);
            System.out.println("✅ Fichaje de salida realizado con éxito para: " 
                + worker.getName() + " " + worker.getSurname());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
