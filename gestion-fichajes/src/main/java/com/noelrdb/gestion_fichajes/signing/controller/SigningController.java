package com.noelrdb.gestion_fichajes.signing.controller;

import java.util.Scanner;

import org.springframework.stereotype.Controller;

import com.noelrdb.gestion_fichajes.signing.service.SigningService;
import com.noelrdb.gestion_fichajes.worker.controller.WorkerController;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;

@Controller
public class SigningController {
    

    private final SigningService signingService;
    public Scanner teclado = new Scanner (System.in);

    public WorkerController workerController;
    public Worker worker;

    public SigningController(SigningService signingService) {
        this.signingService = signingService;
    }

    // Metodo para realizar el fichaje de entrada

    public void SingIn() {

        // Llamamos al metodo de WorkerController para buscar el trabajador por su ID
        Worker foundWorker = workerController.findWorkerById();

        // Antes de ralizar el signIN, comprobar que el trabajador existe
        if (foundWorker == null){
            System.out.println("El trabajador no existe. No se puede realizar el fichaje.");
        }
                // Una vez comprobado de que existe el trabajador, llamar al metodo signIn del SigningService pasandole el id del currante
        else {
            signingService.signIn(foundWorker.getId());
        }
        System.out.println(
            "Fichaje de entrada realizado con exito al " + foundWorker.getName()
            + " con identificador " + foundWorker.getId() + ".");
    }


    // Metodo para realizar el fichaje de salida
    public void SingOut() {
        
        // Llamamos al metodo de WorkerController para buscar el trabajador por su ID
        Worker foundWorker = workerController.findWorkerById();

        // Antes de ralizar el signOUT, comprobar que el trabajador existe
        if (foundWorker == null){
            System.out.println("El trabajador no existe. No se puede realizar el fichaje.");
        }
                // Una vez comprobado de que existe el trabajador, llamar al metodo signOut del SigningService pasandole el id del currante
        else {
            signingService.signOut(foundWorker.getId());
        }
        System.out.println(
            "Fichaje de salida realizado con exito al " + foundWorker.getName()
            + " con identificador " + foundWorker.getId() + ".");
    }

}
