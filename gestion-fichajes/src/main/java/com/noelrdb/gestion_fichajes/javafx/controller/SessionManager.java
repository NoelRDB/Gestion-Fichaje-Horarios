package com.noelrdb.gestion_fichajes.javafx.controller;

import com.noelrdb.gestion_fichajes.worker.entity.Worker;

public class SessionManager {
    private static Worker currentWorker;

    public static void setCurrentWorker(Worker worker) {
        currentWorker = worker;
    }

    public static Worker getCurrentWorker() {
        return currentWorker;
    }

    public static void clearSession() {
        currentWorker = null;
    }

    public static boolean isLoggedIn() {
        return currentWorker != null;
    }
}
