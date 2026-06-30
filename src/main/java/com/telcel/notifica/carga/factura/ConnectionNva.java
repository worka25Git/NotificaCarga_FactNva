package com.telcel.notifica.carga.factura;

import com.telcel.notifica.carga.utils.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionNva {

  public static Connection getConnection() {

    try {

      Class.forName(ConfigManager.get("db.driver"));

      String url = ConfigManager.get("db.nva.url");
      String user = ConfigManager.get("db.nva.user");
      String password = ConfigManager.get("db.nva.password");

      Connection con = DriverManager.getConnection(url, user, password);

      System.out.println("Conexión establecida: " + url);

      return con;

    } catch (ClassNotFoundException e) {

      throw new RuntimeException("No se pudo cargar el driver Oracle.", e);

    } catch (SQLException e) {

      throw new RuntimeException("Error al conectar con la base NVA.", e);

    }

  }
}
