package com.noelrdb.gestion_fichajes.javafx.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.noelrdb.gestion_fichajes.FxmlView;
import com.noelrdb.gestion_fichajes.javafx.StageManager;
import com.noelrdb.gestion_fichajes.signing.entity.Signing;
import com.noelrdb.gestion_fichajes.signing.service.SigningService;
import com.noelrdb.gestion_fichajes.worker.entity.Worker;
import com.noelrdb.gestion_fichajes.worker.service.WorkerService;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

@Controller
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label adminNameLabel;

    // Campos para CRUD de trabajadores
    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField dniField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField codeField;
    
    @FXML
    private javafx.scene.control.CheckBox isAdminCheckBox;

    @FXML
    private TableView<Worker> workersTable;

    @FXML
    private TableColumn<Worker, Integer> workerIdColumn;

    @FXML
    private TableColumn<Worker, String> workerNameColumn;

    @FXML
    private TableColumn<Worker, String> workerSurnameColumn;

    @FXML
    private TableColumn<Worker, String> workerDniColumn;

    @FXML
    private TableColumn<Worker, Integer> workerCodeColumn;

    @FXML
    private TableColumn<Worker, String> workerPhoneColumn;

    @FXML
    private TableColumn<Worker, String> workerEmailColumn;
    
    // Tabla de fichajes en tiempo real
    @FXML
    private TableView<Signing> signingsTable;
    
    @FXML
    private TableColumn<Signing, String> signingWorkerNameColumn;
    
    @FXML
    private TableColumn<Signing, String> signingStatusColumn;
    
    @FXML
    private TableColumn<Signing, String> signingSignInColumn;
    
    @FXML
    private TableColumn<Signing, String> signingWorkedTimeColumn;
    
    @FXML
    private TableColumn<Signing, String> signingBreakTimeColumn;
    
    @FXML
    private TableColumn<Signing, String> signingRemainingTimeColumn;
    
    @FXML
    private Label totalWorkersLabel;
    
    @FXML
    private Label activeWorkersLabel;
    
    @FXML
    private Label adminCountLabel;

    private final WorkerService workerService;
    private final SigningService signingService;
    private final StageManager stageManager;
    
    private Timeline timeline;
    private static final int WORK_DAY_MINUTES = 480; // 8 horas
    private static final int MAX_BREAK_MINUTES = 30; // 30 minutos

    public AdminDashboardController(WorkerService workerService, SigningService signingService, StageManager stageManager) {
        this.workerService = workerService;
        this.signingService = signingService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        if (currentWorker != null) {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Panel de Administración - " + currentWorker.getName() + " " + currentWorker.getSurname());
            }
            if (adminNameLabel != null) {
                adminNameLabel.setText(currentWorker.getName() + " " + currentWorker.getSurname());
            }
        }

        setupWorkersTable();
        setupSigningsTable();
        loadData();
        startAutoRefresh();
    }

    private void setupWorkersTable() {
        workerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        workerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        workerSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        workerDniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        workerCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        workerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        workerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Auto-cargar datos al hacer click en la tabla
        workersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadWorkerToForm(newSelection);
            }
        });
    }
    
    private void setupSigningsTable() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        // Columna nombre del trabajador
        signingWorkerNameColumn.setCellValueFactory(cellData -> {
            Worker worker = cellData.getValue().getMiWorker();
            String workerName = worker != null ? worker.getName() + " " + worker.getSurname() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(workerName);
        });
        
        // Columna estado
        signingStatusColumn.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getStatus();
            String statusText;
            
            if ("WORKING".equals(status)) {
                statusText = "TRABAJANDO";
            } else if ("ON_BREAK".equals(status)) {
                statusText = "EN DESCANSO";
            } else if ("FINISHED".equals(status)) {
                statusText = "FINALIZADO";
            } else {
                statusText = "DESCONOCIDO";
            }
            
            return new javafx.beans.property.SimpleStringProperty(statusText);
        });
        
        // Columna hora de entrada
        signingSignInColumn.setCellValueFactory(cellData -> {
            LocalDateTime signIn = cellData.getValue().getSignIn();
            String timeStr = signIn != null ? signIn.format(timeFormatter) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(timeStr);
        });
        
        // Columna tiempo trabajado
        signingWorkedTimeColumn.setCellValueFactory(cellData -> {
            long workedSeconds = calculateWorkedTimeInSeconds(cellData.getValue());
            return new javafx.beans.property.SimpleStringProperty(formatSeconds(workedSeconds));
        });
        
        // Columna tiempo de descanso
        signingBreakTimeColumn.setCellValueFactory(cellData -> {
            Signing signing = cellData.getValue();
            Integer totalBreakSecondsFromDB = signing.getTotalBreakSeconds() != null ? signing.getTotalBreakSeconds() : 0;
            long totalBreakSeconds = totalBreakSecondsFromDB;
            
            // Si está en descanso, sumar el tiempo actual de descanso en segundos
            if ("ON_BREAK".equals(signing.getStatus()) && signing.getBreakStart() != null) {
                totalBreakSeconds += ChronoUnit.SECONDS.between(signing.getBreakStart(), LocalDateTime.now());
            }
            
            return new javafx.beans.property.SimpleStringProperty(formatSeconds(totalBreakSeconds));
        });
        
        // Columna tiempo restante
        signingRemainingTimeColumn.setCellValueFactory(cellData -> {
            long remainingSeconds = calculateRemainingTimeInSeconds(cellData.getValue());
            return new javafx.beans.property.SimpleStringProperty(formatSeconds(remainingSeconds));
        });
    }
    
    private long calculateWorkedTimeInSeconds(Signing signing) {
        LocalDateTime signIn = signing.getSignIn();
        if (signIn == null) return 0;
        
        LocalDateTime endTime = signing.getSignOut() != null ? signing.getSignOut() : LocalDateTime.now();
        long totalSeconds = ChronoUnit.SECONDS.between(signIn, endTime);
        
        Integer totalBreakSecondsFromDB = signing.getTotalBreakSeconds() != null ? signing.getTotalBreakSeconds() : 0;
        long totalBreakSeconds = totalBreakSecondsFromDB;
        
        // Si está en descanso, sumar el tiempo actual de descanso
        if ("ON_BREAK".equals(signing.getStatus()) && signing.getBreakStart() != null) {
            totalBreakSeconds += ChronoUnit.SECONDS.between(signing.getBreakStart(), LocalDateTime.now());
        }
        
        return totalSeconds - totalBreakSeconds;
    }
    
    private long calculateRemainingTimeInSeconds(Signing signing) {
        if ("FINISHED".equals(signing.getStatus())) {
            return 0;
        }
        
        long workedSeconds = calculateWorkedTimeInSeconds(signing);
        long workDaySeconds = WORK_DAY_MINUTES * 60;
        long remainingSeconds = workDaySeconds - workedSeconds;
        return Math.max(0, remainingSeconds);
    }
    
    private String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private String formatMinutes(long minutes) {
        long hours = minutes / 60;
        long mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
    
    private void startAutoRefresh() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            loadSignings();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void loadData() {
        loadWorkers();
        loadSignings();
    }

    private void loadWorkers() {
        List<Worker> workers = workerService.findAllWorkers();
        ObservableList<Worker> data = FXCollections.observableArrayList(workers);
        workersTable.setItems(data);
        updateStatistics(workers);
    }
    
    private void updateStatistics(List<Worker> workers) {
        if (workers == null) workers = workerService.findAllWorkers();
        
        int total = workers.size();
        int active = (int) workers.stream().filter(w -> w.getActive() != null && w.getActive()).count();
        int admins = (int) workers.stream().filter(w -> w.getIsAdmin() != null && w.getIsAdmin()).count();
        
        if (totalWorkersLabel != null) totalWorkersLabel.setText(String.valueOf(total));
        if (activeWorkersLabel != null) activeWorkersLabel.setText(String.valueOf(active));
        if (adminCountLabel != null) adminCountLabel.setText(String.valueOf(admins));
    }
    
    private void loadSignings() {
        List<Signing> signings = signingService.getActiveSignings();
        ObservableList<Signing> data = FXCollections.observableArrayList(signings);
        signingsTable.setItems(data);
    }

    @FXML
    public void handleAddWorker() {
        StringBuilder errors = new StringBuilder();
        
        // Validar nombre
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            errors.append("• El nombre no puede estar vacío\n");
        } else if (name.matches(".*\\d.*")) {
            errors.append("• El nombre no puede contener números\n");
        }
        
        // Validar apellido
        String surname = surnameField.getText();
        if (surname == null || surname.trim().isEmpty()) {
            errors.append("• El apellido no puede estar vacío\n");
        } else if (surname.matches(".*\\d.*")) {
            errors.append("• El apellido no puede contener números\n");
        }
        
        // Validar DNI (excepción para administradores)
        String dni = dniField.getText();
        boolean isAdmin = isAdminCheckBox != null && isAdminCheckBox.isSelected();
        if (dni == null || dni.trim().isEmpty()) {
            errors.append("• El DNI no puede estar vacío\n");
        } else if (!isAdmin && !isValidDNI(dni)) {
            errors.append("• El DNI no tiene un formato válido (8 dígitos + letra)\n");
        }
        
        // Validar teléfono
        String phone = phoneField.getText();
        if (phone == null || phone.trim().isEmpty()) {
            errors.append("• El teléfono no puede estar vacío\n");
        } else if (!phone.matches("\\d{9}")) {
            errors.append("• El teléfono ha de ser un número de 9 dígitos\n");
        }
        
        // Validar email
        String email = emailField.getText();
        if (email == null || email.trim().isEmpty()) {
            errors.append("• El email no puede estar vacío\n");
        } else if (!email.contains("@")) {
            errors.append("• El email debe contener @\n");
        }
        
        // Validar código
        String codeStr = codeField.getText();
        if (codeStr == null || codeStr.trim().isEmpty()) {
            errors.append("• El código no puede estar vacío\n");
        }
        
        // Si hay errores, mostrarlos y no continuar
        if (errors.length() > 0) {
            showAlert("Errores de Validación", errors.toString(), Alert.AlertType.ERROR);
            return;
        }
        
        try {
            Integer code = Integer.parseInt(codeStr);
            
            Worker worker = new Worker();
            worker.setName(name);
            worker.setSurname(surname);
            worker.setDni(dni);
            worker.setPhone(phone);
            worker.setEmail(email);
            worker.setCode(code);
            worker.setActive(false);
            worker.setIsAdmin(isAdminCheckBox != null && isAdminCheckBox.isSelected());

            workerService.createWorker(worker);
            showAlert("Éxito", "Trabajador añadido correctamente.", Alert.AlertType.INFORMATION);
            clearFields();
            loadWorkers();
            workersTable.refresh();
            workersTable.getSelectionModel().clearSelection();
        } catch (NumberFormatException e) {
            showAlert("Error", "El código debe ser un número.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Error al añadir trabajador: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private boolean isValidDNI(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }
        
        String dniLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
        String numberPart = dni.substring(0, 8);
        char letter = dni.charAt(8);
        
        try {
            int dniNumber = Integer.parseInt(numberPart);
            char expectedLetter = dniLetters.charAt(dniNumber % 23);
            return Character.toUpperCase(letter) == expectedLetter;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    public void handleUpdateWorker() {
        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();
        if (selectedWorker == null) {
            showAlert("Advertencia", "Selecciona un trabajador de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        StringBuilder errors = new StringBuilder();
        
        // Validar nombre
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            errors.append("• El nombre no puede estar vacío\n");
        } else if (name.matches(".*\\d.*")) {
            errors.append("• El nombre no puede contener números\n");
        }
        
        // Validar apellido
        String surname = surnameField.getText();
        if (surname == null || surname.trim().isEmpty()) {
            errors.append("• El apellido no puede estar vacío\n");
        } else if (surname.matches(".*\\d.*")) {
            errors.append("• El apellido no puede contener números\n");
        }
        
        // Validar DNI (excepción para administradores)
        String dni = dniField.getText();
        boolean isAdmin = isAdminCheckBox != null && isAdminCheckBox.isSelected();
        if (dni == null || dni.trim().isEmpty()) {
            errors.append("• El DNI no puede estar vacío\n");
        } else if (!isAdmin && !isValidDNI(dni)) {
            errors.append("• El DNI no tiene un formato válido (8 dígitos + letra)\n");
        }
        
        // Validar teléfono
        String phone = phoneField.getText();
        if (phone == null || phone.trim().isEmpty()) {
            errors.append("• El teléfono no puede estar vacío\n");
        } else if (!phone.matches("\\d{9}")) {
            errors.append("• El teléfono ha de ser un número de 9 dígitos\n");
        }
        
        // Validar email
        String email = emailField.getText();
        if (email == null || email.trim().isEmpty()) {
            errors.append("• El email no puede estar vacío\n");
        } else if (!email.contains("@")) {
            errors.append("• El email debe contener @\n");
        }
        
        // Validar código
        String codeStr = codeField.getText();
        if (codeStr == null || codeStr.trim().isEmpty()) {
            errors.append("• El código no puede estar vacío\n");
        }
        
        // Si hay errores, mostrarlos y no continuar
        if (errors.length() > 0) {
            showAlert("Errores de Validación", errors.toString(), Alert.AlertType.ERROR);
            return;
        }

        try {
            Integer code = Integer.parseInt(codeStr);
            
            selectedWorker.setName(name);
            selectedWorker.setSurname(surname);
            selectedWorker.setDni(dni);
            selectedWorker.setPhone(phone);
            selectedWorker.setEmail(email);
            selectedWorker.setCode(code);
            selectedWorker.setIsAdmin(isAdmin);

            workerService.updateWorker(selectedWorker.getId(), selectedWorker);
            showAlert("Éxito", "Trabajador actualizado correctamente.", Alert.AlertType.INFORMATION);
            clearFields();
            loadWorkers();
            workersTable.refresh(); // Forzar actualización visual
            workersTable.getSelectionModel().clearSelection(); // Limpiar selección
        } catch (NumberFormatException e) {
            showAlert("Error", "El código debe ser un número.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar trabajador: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDeleteWorker() {
        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();
        if (selectedWorker == null) {
            showAlert("Advertencia", "Selecciona un trabajador de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("¿Estás seguro de que deseas eliminar a " + 
                                     selectedWorker.getName() + " " + selectedWorker.getSurname() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    workerService.deleteWorker(selectedWorker.getId());
                    showAlert("Éxito", "Trabajador eliminado correctamente.", Alert.AlertType.INFORMATION);
                    clearFields();
                    loadWorkers();
                    workersTable.refresh();
                    workersTable.getSelectionModel().clearSelection();
                } catch (Exception e) {
                    showAlert("Error", "Error al eliminar trabajador: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    public void handleLoadWorkerData() {
        Worker selectedWorker = workersTable.getSelectionModel().getSelectedItem();
        if (selectedWorker == null) {
            showAlert("Advertencia", "Selecciona un trabajador de la tabla.", Alert.AlertType.WARNING);
            return;
        }
        loadWorkerToForm(selectedWorker);
    }
    
    private void loadWorkerToForm(Worker worker) {
        nameField.setText(worker.getName());
        surnameField.setText(worker.getSurname());
        dniField.setText(worker.getDni());
        phoneField.setText(worker.getPhone());
        emailField.setText(worker.getEmail());
        codeField.setText(String.valueOf(worker.getCode()));
        if (isAdminCheckBox != null) {
            isAdminCheckBox.setSelected(worker.getIsAdmin() != null && worker.getIsAdmin());
        }
    }

    @FXML
    public void handleRefresh() {
        loadData();
        showAlert("Actualizado", "Datos actualizados correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleLogout() {
        if (timeline != null) {
            timeline.stop();
        }
        SessionManager.clearSession();
        stageManager.switchScene(FxmlView.LOGIN);
    }
    
    @FXML
    public void handleClearForm() {
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        surnameField.clear();
        dniField.clear();
        phoneField.clear();
        emailField.clear();
        codeField.clear();
        if (isAdminCheckBox != null) {
            isAdminCheckBox.setSelected(false);
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
