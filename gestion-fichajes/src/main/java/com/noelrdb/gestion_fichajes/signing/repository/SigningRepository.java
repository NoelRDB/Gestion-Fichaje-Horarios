package com.noelrdb.gestion_fichajes.signing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noelrdb.gestion_fichajes.signing.entity.Signing;

public interface SigningRepository extends JpaRepository<Signing, Integer> {
    
    // Metodo para buscar fichajes por el ID del trabajador
    List<Signing> findByMiWorkerId(int workerId);

    Optional<Signing> findTopByMiWorkerIdAndSignOutIsNullOrderBySignInDesc(int workerId);

}
