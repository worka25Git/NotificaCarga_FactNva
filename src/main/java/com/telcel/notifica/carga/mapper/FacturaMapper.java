package com.telcel.notifica.carga.mapper;

import com.telcel.notifica.carga.factura.Factura;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface FacturaMapper {

    Factura map(ResultSet rs) throws SQLException;

}