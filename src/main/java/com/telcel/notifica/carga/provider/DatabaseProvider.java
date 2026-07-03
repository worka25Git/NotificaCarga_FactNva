package com.telcel.notifica.carga.provider;

import com.telcel.notifica.carga.mapper.FacturaMapper;

import java.sql.Connection;

public interface DatabaseProvider {

    Connection getConnection() throws Exception;

    String getConsultarFacturas();

    String getActualizarFactura();

    FacturaMapper getFacturaMapper();

}
