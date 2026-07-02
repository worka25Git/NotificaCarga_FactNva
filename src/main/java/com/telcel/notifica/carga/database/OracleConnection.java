package com.telcel.notifica.carga.database;

import com.telcel.notifica.carga.utils.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;

public class OracleConnection {

    private OracleConnection() {
    }

    public static Connection getConnection() throws Exception {

        Class.forName(ConfigManager.get("db.oracle.driver"));

        return DriverManager.getConnection(
                ConfigManager.get("db.oracle.url"),
                ConfigManager.get("db.oracle.user"),
                ConfigManager.get("db.oracle.password"));
    }

}