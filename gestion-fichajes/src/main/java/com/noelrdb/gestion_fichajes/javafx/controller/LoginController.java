package com.noelrdb.gestion_fichajes.javafx.controller;

import org.springframework.stereotype.Controller;

import com.noelrdb.gestion_fichajes.FxmlView;
import com.noelrdb.gestion_fichajes.javafx.StageManager;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.service.WorkerService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@Controller
public class LoginController {

    @FXML
    private TextField dniField;

    @FXML
    private PasswordField codeField;

    private final WorkerService workerService;
    private final StageManager stageManager;

    public LoginController(WorkerService workerService, StageManager stageManager) {
        this.workerService = workerService;
        this.stageManager = stageManager;
    }

    @FXML
    public void handleLogin() {
        String dni = dniField.getText().trim();
        String codeText = codeField.getText().trim();

        if (dni.isEmpty() || codeText.isEmpty()) {
            showAlert("Error", "Por favor, completa todos los campos.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int code = Integer.parseInt(codeText);
            Worker worker = workerService.authenticate(dni, code);

            if (worker != null) {
                // Guardar el trabajador autenticado en la sesión
                SessionManager.setCurrentWorker(worker);
                
                // Verificar si es administrador
                if (worker.getIsAdmin() != null && worker.getIsAdmin()) {
                    stageManager.switchScene(FxmlView.ADMIN_DASHBOARD);
                } else {
                    stageManager.switchScene(FxmlView.DASHBOARD);
                }
            } else {
                showAlert("Error de autenticación", "DNI o código incorrectos.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "El código debe ser numérico.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
