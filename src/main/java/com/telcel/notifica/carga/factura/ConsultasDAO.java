package com.telcel.notifica.carga.factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultasDAO {

    private static final Logger logger = Logger.getLogger(ConsultasDAO.class.getName());

    private static final String SQL_CONSULTA = "SELECT CL.NOMBRE, LF.FACTURA, LF.ROWID AS ID_FACTURA, LF.FECHA, "
            + "LF.MONTO_COMPRA, LF.RFC, LF.STATUS, LF.REGION, "
            + "SUBSTR(LF.OBSERVACIONES,0,58) AS OBSERVACIONES, LF.FECHA_TELCEL "
            + "FROM LOG_FAC_SAP LF, CLIENTE CL "
            + "WHERE MENSAJE='1' "
            + "AND LF.RFC = CL.RFC "
            + "AND TIPO_BOLSA = '1' "
            + "AND LF.TIPO <> 'A' "
            + "AND LF.STATUS <> 'C' "
            + "ORDER BY CL.NOMBRE";

    private static final String SQL_ACTUALIZA =
            "UPDATE LOG_FAC_SAP "
                    + "SET MENSAJE = ? "
                    + "WHERE FACTURA = ? "
                    + "AND ROWID = ?";

    public List<Factura> checaCarga() {

        List<Factura> facturas = new ArrayList<>();

        try (Connection con = ConnectionBCTR.getConnection();
             PreparedStatement pstm = con.prepareStatement(SQL_CONSULTA);
             ResultSet rs = pstm.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();

            System.out.println("===== COLUMNAS DEVUELTAS POR ORACLE =====");

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println(i + " -> " + meta.getColumnLabel(i));
            }
            System.out.println("=========================================");
            while (rs.next()) {

                facturas.add(new Factura(
                        rs.getString("NOMBRE"),
                        rs.getString("FACTURA"),
                        rs.getString("ID_FACTURA"),
                        rs.getString("FECHA"),
                        rs.getLong("MONTO_COMPRA"),
                        rs.getInt("REGION"),
                        rs.getString("OBSERVACIONES"),
                        rs.getString("FECHA_TELCEL")));

            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al obtener las facturas.", ex);
        }

        return facturas;
    }

    public void actualizaFactura(Set<String> notificadas) {

        try (Connection con = ConnectionBCTR.getConnection();
             PreparedStatement pstm = con.prepareStatement(SQL_ACTUALIZA)) {

            for (String datos : notificadas) {

                String[] tokens = datos.split(",");
                if (tokens.length < 3) {
                    logger.warning("Registro inválido: " + datos);
                    continue;
                }
                int estatus = Integer.parseInt(tokens[1]);

                if (estatus == 0) {
                    pstm.setString(1, "0");
                    pstm.setString(2, tokens[0]);
                    pstm.setString(3, tokens[2]);
                    pstm.executeUpdate();
                    logger.log(Level.INFO, "Se actualizó la factura: {0}", tokens[0]);

                } else {
                    logger.log(Level.INFO, "No se actualizó la factura: {0}", tokens[0]);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al actualizar las facturas.", ex);
        }
    }
}
