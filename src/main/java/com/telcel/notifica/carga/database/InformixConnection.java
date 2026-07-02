package com.telcel.notifica.carga.database;

import com.telcel.notifica.carga.utils.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;

public class InformixConnection {

    private InformixConnection() {
    }

    public static Connection getConnection() throws Exception {

        Class.forName(ConfigManager.get("db.informix.driver"));

        return DriverManager.getConnection(
                ConfigManager.get("db.informix.url"),
                ConfigManager.get("db.informix.user"),
                ConfigManager.get("db.informix.password"));
    }

}