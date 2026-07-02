package com.telcel.notifica.carga.provider;

import com.telcel.notifica.carga.database.OracleConnection;
import com.telcel.notifica.carga.mapper.FacturaMapper;
import com.telcel.notifica.carga.mapper.OracleFacturaMapper;
import com.telcel.notifica.carga.sql.OracleSQL;

import java.sql.Connection;

public class OracleProvider implements DatabaseProvider{

    @Override
    public Connection getConnection() throws Exception {
        return OracleConnection.getConnection();
    }

    @Override
    public String getConsultarFacturas() {
        return OracleSQL.CONSULTA_FACTURAS;
    }

    @Override
    public String getActualizarFactura() {
        return OracleSQL.ACTUALIZA_FACTURA;
    }

    @Override
    public FacturaMapper getFacturaMapper() {
        return new OracleFacturaMapper();
    }

}