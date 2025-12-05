package com.noelrdb.gestion_fichajes.worker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noelrdb.gestion_fichajes.worker.entity.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {

    // Método para buscar un trabajador por su DNI
    Worker findByDni(String dni);
    
    // Método para autenticar trabajador por DNI y código
    Worker findByDniAndCode(String dni, int code);

    // Metodo para saber si existe un currante por su id
    boolean existsById(int id);
    
    // Metodo para saber si está trabajando ahora
    boolean existsByIdAndActiveTrue(int id);

    // Metodo para listar los trabajadores activos
    List<Worker> findByActiveTrue();
}
