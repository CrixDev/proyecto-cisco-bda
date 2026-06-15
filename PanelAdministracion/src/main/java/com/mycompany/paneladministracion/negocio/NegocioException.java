package com.mycompany.paneladministracion.negocio;

/**
 * Error de reglas de negocio (validaciones) en el panel de administración.
 */
public class NegocioException extends Exception {

    public NegocioException(String mensaje) {
        super(mensaje);
    }

    public NegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
