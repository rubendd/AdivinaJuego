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
    private int numeroPremios = 0;

    

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
            Server.notificarMensaje("Cliente conectado => " + id);
            salida.println(id);
            while (true) {
                int fila = Integer.parseInt(entrada.readLine());
                int columna = Integer.parseInt(entrada.readLine());
                fila--; // Ajustamos la entrada de la fila.
                columna--; // Ajustamos la entrada de la columna.
                boolean premioEncontrado = tablero[fila][columna];
                // Server.notificarMensaje(Arrays.deepToString(tablero)); // Para debugear
                // Server.notificarMensaje(String.valueOf(premioEncontrado)); // Para debugear
                if (premioEncontrado) {
                    numeroPremios++;
                    Server.notificarMensaje("Premio encontrado en fila " + "[" + fila + "]" + ", columna " + "[" + columna + "]" + " Cliente: " + id);
                    premioEncontrado(fila, columna);
                    Server.actualizarTablero(Server.getTablero());
                    salida.println("¡Felicidades! Has encontrado un premio.");
                    salida.println(numeroPremios);
                } else {
                    Server.notificarMensaje("No hay premio en fila " + fila + ", columna " + columna);
                    salida.println("["+fila+"]" + " " + "["+columna+"]");
                }
                // Juego termina
                if (tableroCompleto()) {
                    Server.notificarMensaje("Todos los premios han sido encontrados. El juego ha terminado.");
                    salida.println("Juego terminado. Todos los premios han sido encontrados.");
                    break;
                } else {
                    salida.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Server.notificarMensaje("Cliente cerrado => " + id);
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
    
     private void premioEncontrado(int fila, int columna) {
        Server.premioEncontrado(fila, columna);
    }

}
