/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author Dugo
 */
public interface ServidorListener {

    void imprimirMensaje(String mensaje);
    
    void actualizarTablero(boolean[][] tablero);
}
