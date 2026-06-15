/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

/**
 *
 * @author alecn
 */
public class PersistenciaException extends Exception {
    public PersistenciaException(String mensaje) { 
        super(mensaje); 
    }
    public PersistenciaException(String mensaje, Throwable causa) { 
        super(mensaje, causa); 
    }
}
