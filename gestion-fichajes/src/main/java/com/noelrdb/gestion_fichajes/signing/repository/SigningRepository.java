package com.noelrdb.gestion_fichajes.signing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noelrdb.gestion_fichajes.signing.entity.Signing;

public interface SigningRepository extends JpaRepository<Signing, Integer> {
    
    // Metodo para buscar fichajes por el ID del trabajador
    List<Signing> findByWorkerId(int workerId);

    Optional<Signing> findTopByWorkerIdAndSignOutIsNullOrderBySignInDesc(int workerId);
    
    // Método para buscar todos los fichajes activos (sin signOut)
    List<Signing> findBySignOutIsNull();

}
