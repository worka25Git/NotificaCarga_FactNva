package com.telcel.notifica.carga.database;

import com.telcel.notifica.carga.utils.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;

public class OracleConnection {

    private OracleConnection() {
    }

    public static Connection getConnection() throws Exception {

        Class.forName(ConfigManager.get("db.oracle.driver"));

        String url = ConfigManager.get("db.oracle.url");
        String user = ConfigManager.get("db.oracle.user");
        String password = ConfigManager.get("db.oracle.password");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("No se configuró 'db.oracle.url'");
        }
        if (user == null || user.isBlank()) {
            throw new IllegalStateException("No se configuró 'db.oracle.user'");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("No se configuró 'db.oracle.password'");
        }
        return DriverManager.getConnection(url, user, password);
    }

}