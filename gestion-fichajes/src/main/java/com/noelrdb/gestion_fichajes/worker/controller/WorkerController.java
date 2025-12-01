package com.noelrdb.gestion_fichajes.worker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Controller;

import com.noelrdb.gestion_fichajes.signing.entity.Signing;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.service.WorkerService;


@Controller
public class WorkerController {

    // Primero inyectamos el servicio
    private final WorkerService workerService;
    Scanner teclado = new Scanner(System.in);


    // Luego creamos el contrustor para la inyección
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    // Metodos del controlador que llamará al Service del Worker

    // Crear un nuevo currante
    public Worker createWorker() {

        Worker miWorker = new Worker();

        try {
        System.out.println("--- Bienvenido al creador del trabajador ---");
        System.out.println("1º--------------------------------------------");
        System.out.println("Introduce el nombre del trabajador:");
        miWorker.setName(teclado.nextLine());
        System.out.println("2º--------------------------------------------");
        System.out.println("Introduce los apellidos del trabajador:");
        miWorker.setSurname(teclado.nextLine());
        System.out.println("3º--------------------------------------------");
        System.out.println("Introduce el DNI del trabajador:");
        miWorker.setDni(teclado.nextLine());
        System.out.println("4º--------------------------------------------");
        System.out.println("Introduce el email del trabajador:");
        miWorker.setEmail(teclado.nextLine());
        System.out.println("5º--------------------------------------------");
        System.out.println("Introduce el teléfono del trabajador:");
        miWorker.setPhone(teclado.nextLine());
        
        // Ahora crearle un objeto nuevo de Signing para inicializar la lista
        List<Signing> signingList = new ArrayList<>();
        miWorker.setSignings(signingList);

        }

        catch (Exception e) {
            System.out.println("Error al crear el trabajador");
        }
        // Ver por pantalla el currante
        miWorker.toString();

        System.out.println("Son correctos los datos del trabajador? Si/No");
        String respuesta = teclado.nextLine();

        if (respuesta.equalsIgnoreCase("No")) {
            System.out.println("Operación cancelada. No se ha creado el trabajador.");
            return null;
        }

        else{
            System.out.println("Operación confirmada. Procedo a crear el trabajador.");
        }
        return workerService.createWorker(miWorker);
    }
    
    // Listar todos los currantes
    public List<Worker> listAllWorkers(){
        System.out.println("--- Listado de todos los trabajadores ---");
        List<Worker> workers = workerService.findAllWorkers();

        for (Worker w : workers) {
            System.out.println(w.toString());
        }
        return workers;
    }

    // Buscar un currante por su ID
    public Worker findWorkerById() {
        int id;

        System.out.println("--- Buscar trabajador por ID ---");
        System.out.println("Introduce el ID del trabajador a buscar:");
        id = teclado.nextInt();
        teclado.nextLine(); 

        Worker worker = workerService.findWorkerById(id);

        if (worker == null) {
            System.out.println("Trabajador no encontrado.");
        } else {
            System.out.println("Trabajador encontrado: " + worker.toString());
        }

        return worker;
    }


    // Actualizar datos de un currante
    public Worker updateWorker() {
        int id;
        Worker miWorker = new Worker();

        // Primero pedimos al usuario que currante quiere actualizar
        System.out.println("Introduzca el ID del currante a actualizar y te confirmo si existe");
        id = teclado.nextInt();
        teclado.nextLine();
        Worker existingWorker = workerService.findWorkerById(id);

        if (existingWorker == null) {
            System.out.println("El trabajador con ID " + id + " no existe.");
            return null;
        }

        else {
            System.out.println("El trabajador con ID " + id + " existe. Procede a actualizar sus datos.");
        }

        System.out.println("--- Actualizar datos del trabajador ---");
        System.out.println("Introduce el ID del trabajador a actualizar:");
        id = teclado.nextInt();
        teclado.nextLine(); 

        System.out.println("Introduce el nuevo nombre del trabajador:");
        miWorker.setName(teclado.nextLine());

        System.out.println("Introduce los nuevos apellidos del trabajador:");
        miWorker.setSurname(teclado.nextLine());

        System.out.println("Introduce el nuevo DNI del trabajador:");
        miWorker.setDni(teclado.nextLine());

        System.out.println("Introduce el nuevo email del trabajador:");
        miWorker.setEmail(teclado.nextLine());

        System.out.println("Introduce el nuevo teléfono del trabajador:");
        miWorker.setPhone(teclado.nextLine());

        return workerService.updateWorker(id, miWorker);
    }


    // Eliminar un currante por id
    public void deleteWorker() {
        int id;

        System.out.println("--- Eliminar trabajador por ID ---");
        System.out.println("Introduce el ID del trabajador a eliminar:");
        id = teclado.nextInt();
        teclado.nextLine();
        
        if (workerService.findWorkerById(id) == null) {
            System.out.println("El trabajador con ID " + id + " no existe.");
            return;
        }
        else {
            System.out.println("El trabajador con ID " + id + " existe. Procedo a eliminarlo.");
        }

        workerService.deleteWorker(id);
        System.out.println("Trabajador eliminado correctamente.");
    }

    // Activar el estado de un currante a que está trabajando
    public Boolean activateWorker() {
        int id;

        System.out.println("--- Activar trabajador por ID ---");
        System.out.println("Introduce el ID del trabajador a activar:");
        id = teclado.nextInt();
        teclado.nextLine();

        if (workerService.findWorkerById(id) == null){
            System.out.println("El trabajador con ID " + id + " no existe.");
            return false;
        }

        else {
            System.out.println("El trabajador con ID " + id + " existe. Procedo a activarlo.");
        }

         workerService.activateWorker(id);
         System.out.println("Trabajador activado correctamente.");
        return true;
    }
    // Desactivar el estado de un currante a que no está trabajando
    public Boolean desactivateWorker() {
        int id;

        System.out.println("--- Desactivar trabajador por ID ---");
        System.out.println("Introduce el ID del trabajador a desactivar:");
        id = teclado.nextInt();
        teclado.nextLine();

        if (workerService.findWorkerById(id) == null){
            System.out.println("El trabajador con ID " + id + " no existe.");
            return false;
        }

        else {
            System.out.println("El trabajador con ID " + id + " existe. Procedo a desactivarlo.");
        }

         workerService.desactivateWorker(id);
         System.out.println("Trabajador desactivado correctamente.");
         return true;
    }
}
