package com.noelrdb.gestion_fichajes.signing.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noelrdb.gestion_fichajes.signing.entity.Signing;
import com.noelrdb.gestion_fichajes.signing.repository.SigningRepository;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.repository.WorkerRepository;

@Service
public class SigningService {

    private final SigningRepository signingRepository;
    private final WorkerRepository workerRepository;

    public SigningService(SigningRepository signingRepository, WorkerRepository workerRepository) {
        this.signingRepository = signingRepository;
        this.workerRepository = workerRepository;
    }

    public void signIn(int workerId) {
        // 1. Verificar que el trabajador existe
        Worker worker = workerRepository.findById(workerId)
            .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));
        
        // 2. Verificar que NO tenga fichaje activo
        Optional<Signing> activeSigning = signingRepository
            .findTopByMiWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId);
        
        if (activeSigning.isPresent()) { 
            throw new IllegalStateException("Ya existe un fichaje de entrada sin salida");
        }
        
        // 3. Crear nuevo fichaje
        Signing signing = new Signing();
        signing.setMiWorker(worker);
        signing.setSignIn(LocalDateTime.now());
        
        signingRepository.save(signing);
    }

    public void signOut(int workerId) {
        // 1. Buscar fichaje activo (devuelve Signing, no Worker)
        Signing activeSigning = signingRepository
            .findTopByMiWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId)
            .orElseThrow(() -> new IllegalStateException("No hay fichaje de entrada activo"));
        
        // 2. Registrar salida directamente
        activeSigning.setSignOut(LocalDateTime.now());
        
        signingRepository.save(activeSigning);
    }
}
