package com.telcel.notifica.carga.factura;

import com.telcel.notifica.carga.utils.ConfigManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBCTR {

  public static Connection getConnection() {

    Connection con = null;

    String url = ConfigManager.get("db.bctr.url");
    String user = ConfigManager.get("db.bctr.user");
    String password = ConfigManager.get("db.bctr.password");

    try {
      Class.forName(ConfigManager.get("db.driver"));
    } catch (Exception e) {
      System.out.println("No se pudo cargar el driver Oracle");
      e.printStackTrace();
    }

    try {
      con = DriverManager.getConnection(url, user, password);
      System.out.println("Conexión establecida... a la IP- " + url.substring(17));
    } catch (SQLException sqle) {
      System.out.println("Error con la conexión a la base de datos: BCTR");
      sqle.printStackTrace();
    }

    return con;
  }
}