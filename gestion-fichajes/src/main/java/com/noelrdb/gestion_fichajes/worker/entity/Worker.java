package com.noelrdb.gestion_fichajes.worker.entity;

import java.util.List;

import com.noelrdb.gestion_fichajes.signing.entity.Signing;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Clase que va a manejar las entidades de los trabajadores con todos los atributos necesarios
 * para su correcta gestion dentro de la aplicacion.
 */

/**
 * Anotar la clase como entidad de JPA y mapearla a la tabla "workers"
 */
@Entity
@Table(name = "workers")
public class Worker {

    /**
     * Anotación del campo id para la clave primaria y generación automatica para cuando se creen mas trabajadores
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int code;
    private String surname;
    private String dni;
    private String email;
    private String phone;
    private Boolean active = false;
    private Boolean isAdmin = false;

    @OneToMany(mappedBy = "worker")
    private List<Signing> signings;

    // Contructores sin parametrizar y parametrizados
    public Worker() {
    }

    public Worker(int id, String name, int code , String surname, String dni, String email, String phone, Boolean active) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.surname = surname;
        this.dni = dni;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }

    // Getters y setters 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setSignings(List<Signing> signings) {
        this.signings = signings;
    }

    public List<Signing> getSignings() {
        return signings;
    }

    // Metodo to String paramostrar los datos
    @Override
    public String toString() {
        return "Worker [id=" + id + ", name=" + name + ", code=" + code + ", surname=" + surname + ", dni=" + dni + ", email=" + email + ", phone=" + phone + ", active=" + active + "]";
    }

}
