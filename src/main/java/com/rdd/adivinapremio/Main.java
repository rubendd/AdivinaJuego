/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rdd.adivinapremio;

import pantalla.Init;
import pantalla.PantallaCliente;
import pantalla.PantallaServidor;

/**
 *
 * @author Dugo
 */
public class Main {

    public static void main(String[] args) {
        Init init = new Init();
        init.setLocationRelativeTo(null);
        init.setVisible(true);

        // Esperar hasta que la ventana se cierre para obtener la respuesta
        while (init.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Obtener la respuesta y realizar la acción correspondiente
        int clienteChoice = init.getCliente();
        switch (clienteChoice) {
            case 1:
                initServer();
                break;
            case 0:
                initCliente();
                break;
            default:
                System.exit(0);
        }
    }

    private static void initServer() {
        // Lógica de inicialización del servidor
        PantallaServidor pantallaServidor = new PantallaServidor();
        pantallaServidor.setLocationRelativeTo(null);
        pantallaServidor.setVisible(true);
    }

    private static void initCliente() {
        // Lógica de inicialización del cliente
        PantallaCliente pantallaCliente = new PantallaCliente();
        pantallaCliente.setLocationRelativeTo(null);
        pantallaCliente.setVisible(true);

    }

}
