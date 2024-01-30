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
 * La clase Main es la clase principal del programa que inicia la aplicación.
 * Proporciona métodos para inicializar y mostrar la interfaz gráfica de usuario,
 * así como para tomar decisiones basadas en la elección del usuario al cerrar la ventana inicial.
 *
 * @author Dugo
 */
public class Main {

    /**
     * Método principal que inicia la aplicación. Crea y muestra la ventana de inicio,
     * espera hasta que se cierre la ventana y realiza acciones basadas en la elección del usuario.
     *
     * @param args Argumentos de la línea de comandos (no utilizados en este caso).
     */
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

    /**
     * Inicializa y muestra la interfaz gráfica del servidor.
     */
    private static void initServer() {
        // Lógica de inicialización del servidor
        PantallaServidor pantallaServidor = new PantallaServidor();
        pantallaServidor.setLocationRelativeTo(null);
        pantallaServidor.setVisible(true);
    }

    /**
     * Inicializa y muestra la interfaz gráfica del cliente.
     */
    private static void initCliente() {
        // Lógica de inicialización del cliente
        PantallaCliente pantallaCliente = new PantallaCliente();
        pantallaCliente.setLocationRelativeTo(null);
        pantallaCliente.setVisible(true);
    }
}

