package com.noelrdb.gestion_fichajes.javafx.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

@Controller
public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;
    
    @FXML
    private Label workTimeLabel;
    
    @FXML
    private Label breakTimeLabel;
    
    @FXML
    private Label remainingWorkTimeLabel;
    
    @FXML
    private Label availableBreakTimeLabel;

    @FXML
    private TableView<Worker> activeWorkersTable;

    @FXML
    private TableColumn<Worker, Integer> workerIdColumn;

    @FXML
    private TableColumn<Worker, String> workerNameColumn;

    @FXML
    private TableView<Signing> signingsTable;

    @FXML
    private TableColumn<Signing, Integer> signingIdColumn;

    @FXML
    private TableColumn<Signing, String> signingWorkerColumn;

    @FXML
    private TableColumn<Signing, String> signInColumn;

    @FXML
    private TableColumn<Signing, String> signOutColumn;

    private final SigningService signingService;
    private final WorkerService workerService;
    private final StageManager stageManager;
    
    private Timeline timeline;
    private static final int WORK_DAY_MINUTES = 480; // 8 horas
    private static final int MAX_BREAK_MINUTES = 30; // 30 minutos

    public DashboardController(SigningService signingService, WorkerService workerService, StageManager stageManager) {
        this.signingService = signingService;
        this.workerService = workerService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        if (currentWorker != null) {
            welcomeLabel.setText("Bienvenido/a, " + currentWorker.getName() + " " + currentWorker.getSurname());
            updateStatus(currentWorker);
        }

        setupTables();
        loadData();
        startTimer();
    }

    private void setupTables() {
        // Configurar tabla de trabajadores activos
        workerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        workerNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getName() + " " + cellData.getValue().getSurname()
            )
        );

        // Configurar tabla de fichajes
        signingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        signingWorkerColumn.setCellValueFactory(cellData -> {
            Worker worker = cellData.getValue().getMiWorker();
            return new javafx.beans.property.SimpleStringProperty(
                worker != null ? worker.getName() + " " + worker.getSurname() : "N/A"
            );
        });
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        signInColumn.setCellValueFactory(cellData -> {
            LocalDateTime signIn = cellData.getValue().getSignIn();
            return new javafx.beans.property.SimpleStringProperty(
                signIn != null ? signIn.format(formatter) : "N/A"
            );
        });
        signOutColumn.setCellValueFactory(cellData -> {
            LocalDateTime signOut = cellData.getValue().getSignOut();
            return new javafx.beans.property.SimpleStringProperty(
                signOut != null ? signOut.format(formatter) : "Trabajando"
            );
        });
    }

    private void loadData() {
        loadActiveWorkers();
        loadSignings();
    }

    private void loadActiveWorkers() {
        List<Worker> activeWorkers = workerService.findActiveWorkers();
        ObservableList<Worker> data = FXCollections.observableArrayList(activeWorkers);
        activeWorkersTable.setItems(data);
    }

    private void loadSignings() {
        List<Signing> signings = signingService.getAllSignings();
        ObservableList<Signing> data = FXCollections.observableArrayList(signings);
        signingsTable.setItems(data);
    }

    private void updateStatus(Worker worker) {
        Optional<Signing> activeSigningOpt = signingService.getActiveSigningByWorkerId(worker.getId());
        
        if (activeSigningOpt.isPresent()) {
            Signing activeSigning = activeSigningOpt.get();
            String status = activeSigning.getStatus();
            
            if ("ON_BREAK".equals(status)) {
                statusLabel.setText("Estado: EN DESCANSO");
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            } else if ("WORKING".equals(status)) {
                statusLabel.setText("Estado: TRABAJANDO");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }
        } else if (worker.getActive()) {
            statusLabel.setText("Estado: TRABAJANDO");
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            statusLabel.setText("Estado: FUERA DE SERVICIO");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }
    
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimers()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    private void updateTimers() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        if (currentWorker == null) return;
        
        Optional<Signing> activeSigningOpt = signingService.getActiveSigningByWorkerId(currentWorker.getId());
        
        if (activeSigningOpt.isPresent()) {
            Signing activeSigning = activeSigningOpt.get();
            LocalDateTime signIn = activeSigning.getSignIn();
            
            // Calcular tiempo total en segundos desde la entrada
            long totalSeconds = ChronoUnit.SECONDS.between(signIn, LocalDateTime.now());
            Integer totalBreakSecondsFromDB = activeSigning.getTotalBreakSeconds() != null ? activeSigning.getTotalBreakSeconds() : 0;
            long totalBreakSeconds = totalBreakSecondsFromDB;
            
            System.out.println("DEBUG updateTimers - totalBreakSeconds desde BD: " + totalBreakSecondsFromDB);
            
            // Si está en descanso, añadir el tiempo del descanso actual en segundos
            if ("ON_BREAK".equals(activeSigning.getStatus()) && activeSigning.getBreakStart() != null) {
                long currentBreakSeconds = ChronoUnit.SECONDS.between(activeSigning.getBreakStart(), LocalDateTime.now());
                totalBreakSeconds += currentBreakSeconds;
            }
            
            long workedSeconds = totalSeconds - totalBreakSeconds;
            
            // Actualizar etiquetas
            workTimeLabel.setText(formatSeconds(workedSeconds));
            breakTimeLabel.setText(formatSeconds(totalBreakSeconds));
            
            // Tiempo restante de jornada (8 horas = 28800 segundos)
            long remainingSeconds = (WORK_DAY_MINUTES * 60) - workedSeconds;
            remainingWorkTimeLabel.setText(formatSeconds(Math.max(0, remainingSeconds)));
            
            // Descanso disponible (30 minutos - tiempo de descanso usado)
            long maxBreakSeconds = MAX_BREAK_MINUTES * 60;
            long availableBreakSeconds = maxBreakSeconds - totalBreakSeconds;
            availableBreakTimeLabel.setText(formatSeconds(Math.max(0, availableBreakSeconds)));
            
            System.out.println("DEBUG - Descanso disponible (segundos): " + availableBreakSeconds);
            
            // Colorear según exceso de descanso
            if (totalBreakSeconds > maxBreakSeconds) {
                breakTimeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                availableBreakTimeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                breakTimeLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                availableBreakTimeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            }
        } else {
            workTimeLabel.setText("00:00:00");
            breakTimeLabel.setText("00:00:00");
            remainingWorkTimeLabel.setText("08:00:00");
            availableBreakTimeLabel.setText("00:30:00");
        }
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
        return String.format("%02d:%02d:00", hours, mins);
    }

    @FXML
    public void handleSignIn() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        
        try {
            signingService.signIn(currentWorker.getId());
            showAlert("Éxito", "Fichaje de entrada registrado correctamente.", Alert.AlertType.INFORMATION);
            
            // Actualizar trabajador en sesión
            Worker updatedWorker = workerService.findWorkerById(currentWorker.getId());
            SessionManager.setCurrentWorker(updatedWorker);
            updateStatus(updatedWorker);
            loadData();
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleSignOut() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        
        try {
            signingService.signOut(currentWorker.getId());
            showAlert("Éxito", "Fichaje de salida registrado correctamente.", Alert.AlertType.INFORMATION);
            
            // Actualizar trabajador en sesión
            Worker updatedWorker = workerService.findWorkerById(currentWorker.getId());
            SessionManager.setCurrentWorker(updatedWorker);
            updateStatus(updatedWorker);
            loadData();
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleRefresh() {
        loadData();
        showAlert("Actualizado", "Datos actualizados correctamente.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void handleBreak() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        
        try {
            signingService.startBreak(currentWorker.getId());
            showAlert("Descanso", "Descanso iniciado. Recuerda: máximo 30 minutos.", Alert.AlertType.INFORMATION);
            updateStatus(currentWorker);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void handleResumeWork() {
        Worker currentWorker = SessionManager.getCurrentWorker();
        
        try {
            signingService.resumeWork(currentWorker.getId());
            
            // Verificar si se pasó del tiempo de descanso
            Optional<Signing> activeSigningOpt = signingService.getActiveSigningByWorkerId(currentWorker.getId());
            if (activeSigningOpt.isPresent()) {
                int totalBreakSecs = activeSigningOpt.get().getTotalBreakSeconds() != null ? activeSigningOpt.get().getTotalBreakSeconds() : 0;
                int maxBreakSecs = MAX_BREAK_MINUTES * 60;
                if (totalBreakSecs > maxBreakSecs) {
                    int excesoMinutos = (totalBreakSecs - maxBreakSecs) / 60;
                    showAlert("Advertencia", 
                        "Has excedido el tiempo de descanso por " + excesoMinutos + " minutos.\n" +
                        "Deberás recuperar este tiempo.", 
                        Alert.AlertType.WARNING);
                } else {
                    showAlert("Éxito", "Trabajo reanudado correctamente.", Alert.AlertType.INFORMATION);
                }
            }
            
            updateStatus(currentWorker);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleLogout() {
        if (timeline != null) {
            timeline.stop();
        }
        SessionManager.clearSession();
        stageManager.switchScene(FxmlView.LOGIN);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
