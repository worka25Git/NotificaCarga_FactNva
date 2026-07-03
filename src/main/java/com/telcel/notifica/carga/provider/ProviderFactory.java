package com.telcel.notifica.carga.provider;

import com.telcel.notifica.carga.utils.ConfigManager;

public final class ProviderFactory {

    private ProviderFactory() {
    }

    public static DatabaseProvider getProvider() {

        String motor = ConfigManager.get("db.tipo");

        switch (motor.toLowerCase()) {

            case "oracle":
                return new OracleProvider();

            case "informix":
                return new InformixProvider();

            default:
                throw new IllegalArgumentException(
                        "Motor de base de datos no soportado: " + motor);
        }
    }

}