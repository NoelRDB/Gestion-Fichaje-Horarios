package com.noelrdb.gestion_fichajes.signing.entity;

import java.time.LocalDateTime;

import com.noelrdb.gestion_fichajes.worker.entity.Worker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**
 * Clase que va a manejar el fichaje del trabajador almacenando un id del registro, la fecha del fichaje
 * el trabajador que realiza el fichaje, la hora de entrada a la empresa y la hora de salida de la empresa.
 * Anotación de la clase como entidad para JPA y mapeo a la tabla "signings"
 */
@Entity
@Table (name = "signings")
public class Signing {

    /**
     * Anotación del campo ID como clave primaria y generación automatica para nuevos fichajes
     */
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;


    /**
     * Anotación ManyToOne para la relación entre fichajes y currantes
     */
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker miWorker;


    private LocalDateTime signIn;
    private LocalDateTime signOut;


    // Constructores sin parametrizar y parametrizados
    public Signing() {}

    public Signing (int id, Worker miWorker, LocalDateTime signIn, LocalDateTime signOut) {
        this.id = id;
        this.miWorker = miWorker;
        this.signIn = signIn;
        this.signOut = signOut;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Worker getMiWorker() {
        return miWorker;
    }

    public void setMiWorker(Worker miWorker) {
        this.miWorker = miWorker;
    }

    public LocalDateTime getSignIn() {
        return signIn;
    }

    public void setSignIn(LocalDateTime signIn) {
        this.signIn = signIn;
    }

    public LocalDateTime getSignOut() {
        return signOut;
    }

    public void setSignOut(LocalDateTime signOut) {
        this.signOut = signOut;
    }

    // Metodo toString
    @Override
    public String toString() {
        return "Signing [id=" + id + ", miWorker=" + miWorker + ", signIn=" + signIn + ", signOut=" + signOut + "]";
    }
    
}
