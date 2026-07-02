package com.telcel.notifica.carga.provider;

import com.telcel.notifica.carga.database.InformixConnection;
import com.telcel.notifica.carga.mapper.FacturaMapper;
import com.telcel.notifica.carga.mapper.InformixFacturaMapper;
import com.telcel.notifica.carga.sql.InformixSQL;

import java.sql.Connection;

public class InformixProvider implements DatabaseProvider {

    @Override
    public Connection getConnection() throws Exception {
        return InformixConnection.getConnection();
    }

    @Override
    public String getConsultarFacturas() {
        return InformixSQL.CONSULTA_FACTURAS;
    }

    @Override
    public String getActualizarFactura() {
        return InformixSQL.ACTUALIZA_FACTURA;
    }

    @Override
    public FacturaMapper getFacturaMapper() {
        return new InformixFacturaMapper();
    }

}