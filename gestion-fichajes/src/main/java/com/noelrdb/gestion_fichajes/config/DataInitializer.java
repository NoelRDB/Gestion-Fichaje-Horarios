package com.noelrdb.gestion_fichajes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.repository.WorkerRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WorkerRepository workerRepository;

    public DataInitializer(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar si la base de datos está vacía
        if (workerRepository.count() == 0) {
            // Crear usuario administrador
            Worker admin = new Worker();
            admin.setName("Administrador");
            admin.setCode(0);
            admin.setSurname("Sistema");
            admin.setDni("admin");
            admin.setEmail("admin@sistema.com");
            admin.setPhone("000000000");
            admin.setActive(false);
            admin.setIsAdmin(true);
            workerRepository.save(admin);

            // Crear trabajadores de prueba
            Worker juan = new Worker();
            juan.setName("Juan");
            juan.setCode(1234);
            juan.setSurname("García");
            juan.setDni("12345678A");
            juan.setEmail("juan@email.com");
            juan.setPhone("600111222");
            juan.setActive(false);
            juan.setIsAdmin(false);
            workerRepository.save(juan);

            Worker maria = new Worker();
            maria.setName("María");
            maria.setCode(5678);
            maria.setSurname("López");
            maria.setDni("87654321B");
            maria.setEmail("maria@email.com");
            maria.setPhone("600333444");
            maria.setActive(false);
            maria.setIsAdmin(false);
            workerRepository.save(maria);

            Worker pedro = new Worker();
            pedro.setName("Pedro");
            pedro.setCode(9999);
            pedro.setSurname("Martínez");
            pedro.setDni("11223344C");
            pedro.setEmail("pedro@email.com");
            pedro.setPhone("600555666");
            pedro.setActive(false);
            pedro.setIsAdmin(false);
            workerRepository.save(pedro);

            System.out.println("✅ Datos iniciales cargados correctamente:");
            System.out.println("   - Administrador (DNI: admin, Código: 0)");
            System.out.println("   - Juan (DNI: 12345678A, Código: 1234)");
            System.out.println("   - María (DNI: 87654321B, Código: 5678)");
            System.out.println("   - Pedro (DNI: 11223344C, Código: 9999)");
        }
    }
}
