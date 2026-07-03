package com.telcel.notifica.carga.factura;

import com.telcel.notifica.carga.mapper.FacturaMapper;
import com.telcel.notifica.carga.provider.DatabaseProvider;
import com.telcel.notifica.carga.provider.ProviderFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultasDAO {

    private static final Logger logger = Logger.getLogger(ConsultasDAO.class.getName());

    public List<Factura> checaCarga() {

        List<Factura> facturas = new ArrayList<>();
        DatabaseProvider provider = ProviderFactory.getProvider();

        try (Connection con = provider.getConnection();
             PreparedStatement ps = con.prepareStatement(provider.getConsultarFacturas());
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            /*
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.println(i + " -> " + meta.getColumnLabel(i));
            }
            */

            FacturaMapper mapper = provider.getFacturaMapper();
            while (rs.next()) {
                Factura factura = mapper.map(rs);
                logger.info(String.format(
                        "[BD] Factura=%s | Cadena=%s | Región=%d | Fecha=%s",
                        factura.getFactura(),
                        factura.getNombre(),
                        factura.getRegion(),
                        factura.getFecha()));

                facturas.add(factura);
            }
            logger.info("[BD] Total de facturas obtenidas: " + facturas.size());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al obtener las facturas.", ex);
        }

        return facturas;
    }

    public void actualizaFactura(Set<String> notificadas) {

        DatabaseProvider provider = ProviderFactory.getProvider();

        try (Connection con = provider.getConnection();
             PreparedStatement pstm = con.prepareStatement(provider.getActualizarFactura())) {

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

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al actualizar las facturas.", ex);
        }
    }
}
