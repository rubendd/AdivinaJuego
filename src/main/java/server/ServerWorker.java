/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Dugo
 */
public class ServerWorker extends Thread {

    private final Socket clienteSocket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    private final boolean[][] tablero;
    private final int id;
    private static int jugadas = 3;
    
      
    public ServerWorker(Socket clienteSocket, boolean[][] tablero, int id) throws IOException {
        this.clienteSocket = clienteSocket;
        this.entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
        this.salida = new PrintWriter(clienteSocket.getOutputStream(), true);
        this.tablero = tablero;
        this.id = id;
    }

    @Override
    public void run() {
        try {            
            while (true) {
                // Espera la entrada del cliente (fila y columna)
                salida.println(id);
                salida.println(jugadas);
                Server.notificarMensaje("soy el cliente "+id);
                int fila = Integer.parseInt(entrada.readLine());
                int columna = Integer.parseInt(entrada.readLine());

                // Verifica si hay un premio en la posición indicada
                boolean premioEncontrado = tablero[fila][columna];
                if (premioEncontrado) {
                    Server.notificarMensaje("Premio encontrado en fila " + fila + ", columna " + columna);
                    salida.println("¡Felicidades! Has encontrado un premio.");
                } else {
                   Server.notificarMensaje("No hay premio en fila " + fila + ", columna " + columna);
                    salida.println("Lo siento, no hay premio en esa posición.");
                }

                // Informa al cliente si el juego ha terminado
                if (tableroCompleto()) {
                    Server.notificarMensaje("Todos los premios han sido encontrados. El juego ha terminado.");
                    salida.println("Fin del juego. Todos los premios han sido encontrados.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean tableroCompleto() {
        // Verifica si todos los premios han sido encontrados
        for (boolean[] fila : tablero) {
            for (boolean premio : fila) {
                if (premio) {
                    return false; // Todavía hay premios sin encontrar
                }
            }
        }
        return true; // Todos los premios han sido encontrados
    }

}
