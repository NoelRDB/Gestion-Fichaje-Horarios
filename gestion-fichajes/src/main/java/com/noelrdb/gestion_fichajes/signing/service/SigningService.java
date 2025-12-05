package com.noelrdb.gestion_fichajes.signing.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
            .findTopByWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId);
        
        if (activeSigning.isPresent()) { 
            throw new IllegalStateException("Ya existe un fichaje de entrada sin salida");
        }
        
        // 3. Activar al trabajador (está trabajando)
        worker.setActive(true);
        workerRepository.save(worker);
        
        // 4. Crear nuevo fichaje
        Signing signing = new Signing();
        signing.setMiWorker(worker);
        signing.setSignIn(LocalDateTime.now());
        signing.setStatus("WORKING");
        signing.setTotalBreakSeconds(0);
        
        signingRepository.save(signing);
    }

    public void signOut(int workerId) {
        // 1. Buscar fichaje activo (devuelve Signing, no Worker)
        Signing activeSigning = signingRepository
            .findTopByWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId)
            .orElseThrow(() -> new IllegalStateException("No hay fichaje de entrada activo"));
        
        // 2. Desactivar al trabajador (ya no está trabajando)
        Worker worker = workerRepository.findById(workerId)
            .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));
        worker.setActive(false);
        workerRepository.save(worker);
        
        // 3. Registrar salida directamente
        activeSigning.setSignOut(LocalDateTime.now());
        activeSigning.setStatus("FINISHED");
        
        signingRepository.save(activeSigning);
    }
    
    // Método para iniciar descanso
    public void startBreak(int workerId) {
        Signing activeSigning = signingRepository
            .findTopByWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId)
            .orElseThrow(() -> new IllegalStateException("No hay fichaje activo"));
        
        if ("ON_BREAK".equals(activeSigning.getStatus())) {
            throw new IllegalStateException("Ya estás en descanso");
        }
        
        activeSigning.setBreakStart(LocalDateTime.now());
        activeSigning.setStatus("ON_BREAK");
        signingRepository.save(activeSigning);
    }
    
    // Método para reanudar trabajo después del descanso
    public void resumeWork(int workerId) {
        Signing activeSigning = signingRepository
            .findTopByWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId)
            .orElseThrow(() -> new IllegalStateException("No hay fichaje activo"));
        
        if (!"ON_BREAK".equals(activeSigning.getStatus())) {
            throw new IllegalStateException("No estás en descanso");
        }
        
        // Calcular tiempo de descanso en SEGUNDOS
        LocalDateTime breakStart = activeSigning.getBreakStart();
        if (breakStart != null) {
            long breakSeconds = Duration.between(breakStart, LocalDateTime.now()).toSeconds();
            int currentTotal = activeSigning.getTotalBreakSeconds() != null ? activeSigning.getTotalBreakSeconds() : 0;
            int newTotal = currentTotal + (int) breakSeconds;
            
            System.out.println("=== DEBUG RESUME WORK ===");
            System.out.println("Total anterior (segundos): " + currentTotal);
            System.out.println("Descanso actual (segundos): " + breakSeconds);
            System.out.println("Nuevo total (segundos): " + newTotal);
            
            activeSigning.setTotalBreakSeconds(newTotal);
        }
        
        activeSigning.setBreakStart(null);
        activeSigning.setStatus("WORKING");
        
        Signing saved = signingRepository.save(activeSigning);
        System.out.println("Guardado en BD - Total break segundos: " + saved.getTotalBreakSeconds());
        System.out.println("========================");
    }
    
    // Método para obtener el fichaje activo de un trabajador
    public Optional<Signing> getActiveSigningByWorkerId(int workerId) {
        return signingRepository.findTopByWorkerIdAndSignOutIsNullOrderBySignInDesc(workerId);
    }
    
    // Método para obtener todos los fichajes
    public List<Signing> getAllSignings() {
        return signingRepository.findAll();
    }
    
    // Método para obtener todos los fichajes activos (sin signOut)
    public List<Signing> getActiveSignings() {
        return signingRepository.findBySignOutIsNull();
    }
    
    // Método para obtener fichajes de un trabajador
    public List<Signing> getSigningsByWorkerId(int workerId) {
        return signingRepository.findByWorkerId(workerId);
    }
}
