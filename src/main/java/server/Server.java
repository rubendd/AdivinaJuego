/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dugo
 */
public class Server {

    private static final int PORT = 4444;
    private static final int FILAS = 3;
    private static final int COLUMNAS = 4;
    private static final int NUMERO_PREMIOS = 4;
    private static int idCliente = 0;
    private static boolean[][] tablero;
    private static final List<ServidorListener> listeners = new ArrayList<>();

    public static void addListener(ServidorListener listener) {
        listeners.add(listener);
    }

    public static void notificarMensaje(String mensaje) {
        listeners.forEach(listener -> {
            listener.imprimirMensaje(mensaje);
        });
    }

    public static void actualizarTablero(boolean[][] tablero) {
        listeners.forEach(z -> z.actualizarTablero(tablero));
    }

    public static void premioEncontrado(int fila, int columna) {
        tablero[fila][columna] = false;
        notificarMensaje("Premio encontrado en fila " + "[" + fila + "]" + ", columna " + "[" + columna + "]");
        actualizarTablero(tablero);
    }

    public void initServer() {
        inicializarTablero();
        actualizarTablero(tablero);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            notificarMensaje("Servidor esperando conexiones en el puerto " + PORT);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                idCliente++;
                notificarMensaje("Servidor: Cliente conectado desde " + clienteSocket.getInetAddress().getHostAddress()
                        + " con id: " + idCliente);

                if (tableroCompleto()) {
                    notificarMensaje("Servidor: Todos los premios ya han sido encontrados. Cliente desconectado.");
                    clienteSocket.close();
                    continue;
                }

                // Inicia un nuevo hilo para manejar la conexión con el cliente
                Thread clienteThread = new Thread(() -> manejarCliente(clienteSocket));
                clienteThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void inicializarTablero() {
        tablero = new boolean[FILAS][COLUMNAS];
        Random random = new Random();
        int premiosColocados = 0;

        while (premiosColocados < NUMERO_PREMIOS) {
            int fila = random.nextInt(FILAS);
            int columna = random.nextInt(COLUMNAS);

            if (!tablero[fila][columna]) {
                tablero[fila][columna] = true;
                premiosColocados++;
            }
        }
    }
    
    /**
     * Método estático que retorna si el tablero todavía tiene premios por
     * econtrar.
     * @return false si hay premios sin encontrar.
     */
    private static boolean tableroCompleto() {
        for (boolean[] fila : tablero) {
            for (boolean premio : fila) {
                if (premio) {
                    return false; // Todavía hay premios sin encontrar
                }
            }
        }
        return true; // Todos los premios han sido encontrados
    }

    private void manejarCliente(Socket clienteSocket) {
        try {
            ServerWorker clienteHandler = new ServerWorker(clienteSocket, tablero, idCliente);
            clienteHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getFILAS() {
        return FILAS;
    }

    public static int getCOLUMNAS() {
        return COLUMNAS;
    }

    public static boolean[][] getTablero() {
        return tablero;
    }

}
