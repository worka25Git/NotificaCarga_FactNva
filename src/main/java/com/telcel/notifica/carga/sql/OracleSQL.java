package com.telcel.notifica.carga.sql;

public final class OracleSQL {

    private OracleSQL() {
    }

    public static final String CONSULTA_FACTURAS =
            "SELECT CL.NOMBRE AS NOMBRE, " +
                    "LF.FACTURA AS FACTURA, " +
                    "LF.ROWID AS ID_FACTURA, " +
                    "LF.FECHA AS FECHA, " +
                    "LF.MONTO_COMPRA AS MONTO_COMPRA, " +
                    "LF.RFC AS RFC, " +
                    "LF.STATUS AS STATUS, " +
                    "LF.REGION AS REGION, " +
                    "SUBSTR(LF.OBSERVACIONES,0,58) AS OBSERVACIONES, " +
                    "LF.FECHA_TELCEL AS FECHA_TELCEL " +
                    "FROM LOG_FAC_SAP LF, CLIENTE CL " +
                    "WHERE MENSAJE='1' " +
                    "AND LF.RFC = CL.RFC " +
                    "AND TIPO_BOLSA='1' " +
                    "AND LF.TIPO <> 'A' " +
                    "AND LF.STATUS <> 'C' " +
                    "ORDER BY CL.NOMBRE";

    public static final String ACTUALIZA_FACTURA =
            "UPDATE LOG_FAC_SAP " +
                    "SET MENSAJE=? " +
                    "WHERE FACTURA=? " +
                    "AND ROWID=?";
}