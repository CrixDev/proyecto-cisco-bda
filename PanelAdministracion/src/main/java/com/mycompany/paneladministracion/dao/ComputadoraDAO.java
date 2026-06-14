/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.dtos.ComputadoraDTO;
import com.mycompany.paneladministracion.entidades.Computadora;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de computadoras. Maneja datos prueba en memoria (sin conexión a base de datos).
 *
 * @author Cristian Devora
 */
public class ComputadoraDAO {

    private final List<Computadora> computadoras;

    public ComputadoraDAO() {
        this.computadoras = new ArrayList<>();
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        computadoras.add(new Computadora(1, "Datos prueba - PC-01", true));
        computadoras.add(new Computadora(2, "Datos prueba - PC-02", false));
        computadoras.add(new Computadora(3, "Datos prueba - PC-03", true));
        computadoras.add(new Computadora(4, "Datos prueba - PC-04", true));
    }

    // 4. Mostrar la lista de computadoras.
    public List<ComputadoraDTO> listarComputadoras() {
        List<ComputadoraDTO> lista = new ArrayList<>();
        for (Computadora computadora : computadoras) {
            lista.add(new ComputadoraDTO(
                    computadora.getId(), computadora.getEtiqueta(), computadora.isHabilitada()));
        }
        return lista;
    }

    // 4. Habilitar y deshabilitar una PC para el uso de apartado.
    public boolean habilitarComputadora(int idComputadora) {
        Computadora computadora = buscarComputadora(idComputadora);
        if (computadora != null) {
            computadora.setHabilitada(true);
            return true;
        }
        return false;
    }

    public boolean deshabilitarComputadora(int idComputadora) {
        Computadora computadora = buscarComputadora(idComputadora);
        if (computadora != null) {
            computadora.setHabilitada(false);
            return true;
        }
        return false;
    }

    private Computadora buscarComputadora(int idComputadora) {
        for (Computadora computadora : computadoras) {
            if (computadora.getId() == idComputadora) {
                return computadora;
            }
        }
        return null;
    }
}
