package com.telcel.notifica.carga.factura;

import com.telcel.notifica.carga.envio.EnvioNotificaFacturaNva;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultasDAO {
  private static Logger logger = Logger.getLogger(EnvioNotificaFacturaNva.class.getName());

  
  public ArrayList<Factura> checaCargaBCTR() throws SQLException {
    ArrayList<Factura> Factura = new ArrayList<Factura>();
    try {
      con = ConnectionBCTR.getConnection();
      String sql = "SELECT CL.NOMBRE, LF.FACTURA, LF.rowid, LF.FECHA, LF.MONTO_COMPRA, LF.RFC, LF.STATUS, LF.REGION, SUBSTR(LF.OBSERVACIONES,0,58), LF.FECHA_TELCEL FROM LOG_FAC_SAP LF, CLIENTE CL WHERE MENSAJE='1'and LF.RFC = CL.RFC and TIPO_BOLSA = '1' and LF.TIPO <>'A' and LF.STATUS <> 'C' order by CL.NOMBRE";
      System.out.println(sql);
      PreparedStatement pstm = con.prepareStatement(sql);
      ResultSet rstm = pstm.executeQuery();
      while (rstm.next())
        Factura.add(new Factura(rstm.getString(1), rstm.getString(2), rstm.getString(3), rstm.getString(4), rstm.getLong(5), rstm.getInt(8), rstm.getString(9), rstm.getString(10))); 
      for (Factura Factura1 : Factura)
        System.out.println(Factura1.getNombre() + "," + Factura1.getFactura() + "," + Factura1.getId_factura() + "," + Factura1.getFecha() + "," + Factura1.getMonto_compra() + "," + Factura1.getRegion() + "," + Factura1.getObservaciones() + "," + Factura1.getFechaTelcel()); 
      this.con.close();
      boolean cerrar = this.con.isClosed();
      if (cerrar) {
        System.out.println("Se cerro correctamente la conexion a BD...BCTR");
      } else {
        System.out.println("NO se cerro correctamente la conexion a BD, favor de validar...BCTR");
      } 
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Hubo un error en la BD al obtener la info(BCTR)..." + e.getMessage());
    } 
    return Factura;
  }
  
  public void actualizaFacturaBCTR(Set<String> notificadas) throws SQLException {
    try {
      this.con = ConnectionBCTR.getConnection();
      for (String datos : notificadas) {
        String[] tokens = datos.split(",");
        if (Integer.parseInt(tokens[1]) == 0) {
          String sql = "UPDATE LOG_FAC_SAP  SET MENSAJE='0' WHERE FACTURA='" + tokens[0] + "' and rowid='" + tokens[2] + "'";
          PreparedStatement pstm = this.con.prepareStatement(sql);
          pstm.executeQuery();
          logger.log(Level.INFO, "se actualizo la factura:  {0}", (Object[])new String[] { tokens[0] });
          System.out.println("Se actualizo la factura: " + tokens[0]);
          continue;
        } 
        logger.log(Level.INFO, "No se actualizo registro");
      } 
      this.con.close();
      boolean cerrar = this.con.isClosed();
      if (cerrar) {
        System.out.println("Se cerro correctamente la conexion a BD...BCTR");
      } else {
        System.out.println("NO se cerro correctamente la conexion a BD, favor de validar...BCTR");
      } 
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Hubo un error en la BD al actualizar la info(BCTR)..." + ex.getMessage());
    } 
  }
}
