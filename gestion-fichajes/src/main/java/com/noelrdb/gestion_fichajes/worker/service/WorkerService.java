package com.noelrdb.gestion_fichajes.worker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.repository.WorkerRepository;

@Service
public class WorkerService{
    
    // Primero inyectamos el repositorio
    private final WorkerRepository workerRepository;

    // Luego creamos el contrustor para la inyección
    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    // Crear un nuevo currante
    public Worker createWorker(Worker miWorker) {
        return workerRepository.save(miWorker);
    }

    // Listar todos los currantes
    public List<Worker> findAllWorkers(){

        try {
            List <Worker> workers = workerRepository.findAll();
            return workers;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Buscar un currante por su ID - retorna Worker directamente
    public Worker findWorkerById(int id) {
        return workerRepository.findById(id).orElse(null);
    }

    // Actualizar datos de un currante
    public Worker updateWorker(int id, Worker worker) {
        Worker existingWorker = workerRepository.findById(id).orElse(null);
        if (existingWorker != null) {
            existingWorker.setName(worker.getName());
            existingWorker.setSurname(worker.getSurname());
            existingWorker.setDni(worker.getDni());
            existingWorker.setEmail(worker.getEmail());
            existingWorker.setPhone(worker.getPhone());
            existingWorker.setCode(worker.getCode());
            existingWorker.setIsAdmin(worker.getIsAdmin());
            existingWorker.setActive(worker.getActive());
        }
        return workerRepository.save(existingWorker);
    }


    // Metodo para eliminar un currante por id
    public void deleteWorker(int id) {
        workerRepository.deleteById(id);
    }


    // Metodo para activar el estado de un currante a que está trabajando
    public Worker activateWorker(int id) {

        Worker existingWorker = findWorkerById(id);

        if (existingWorker != null) {
            existingWorker.setActive(true);
            return workerRepository.save(existingWorker);
        }
        else {
            return null;
        }
    }

    // Metodo para desactivar el estado de un currante a que no está trabajando
    public Worker desactivateWorker(int id) {

        Worker existingWorker = findWorkerById(id);

        if (existingWorker != null){
            existingWorker.setActive(false);
            return workerRepository.save(existingWorker);
        }
        else {
            return null;
        }
    }
    
    // Método para autenticar trabajador con DNI y código
    public Worker authenticate(String dni, int code) {
        return workerRepository.findByDniAndCode(dni, code);
    }
    
    // Método para listar trabajadores activos (los que están trabajando ahora)
    public List<Worker> findActiveWorkers() {
        return workerRepository.findByActiveTrue();
    }

}
