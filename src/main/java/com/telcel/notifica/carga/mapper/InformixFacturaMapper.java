package com.telcel.notifica.carga.mapper;

import com.telcel.notifica.carga.factura.Factura;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InformixFacturaMapper implements FacturaMapper {

    @Override
    public Factura map(ResultSet rs) throws SQLException {

        return new Factura(
                rs.getString("NOMBRE"),
                rs.getString("FACTURA"),
                rs.getString("ID_FACTURA"),
                rs.getString("FECHA"),
                rs.getLong("MONTO_COMPRA"),
                rs.getInt("REGION"),
                rs.getString("OBSERVACIONES"),
                rs.getString("FECHA_TELCEL"));

    }

}