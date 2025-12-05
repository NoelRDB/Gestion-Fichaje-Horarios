package com.noelrdb.gestion_fichajes;

public enum FxmlView {
    LOGIN {
        @Override
        public String getTitle() {
            return "Iniciar Sesión - Sistema de Fichajes";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/login.fxml";
        }
    },
    DASHBOARD {
        @Override
        public String getTitle() {
            return "Dashboard - Sistema de Fichajes";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/dashboard.fxml";
        }
    },
    ADMIN_DASHBOARD {
        @Override
        public String getTitle() {
            return "Panel de Administrador - Sistema de Fichajes";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/admin-dashboard.fxml";
        }
    };

    public abstract String getTitle();
    public abstract String getFxmlFile();
}
