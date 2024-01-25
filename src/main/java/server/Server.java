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
    private static List<ServidorListener> listeners = new ArrayList<>();

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

    public static void initServer() {
        inicializarTablero();
        actualizarTablero(tablero);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            notificarMensaje("Servidor esperando conexiones en el puerto " + PORT);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                idCliente++;
                notificarMensaje("Servidor: Cliente conectado desde " + clienteSocket.getInetAddress().getHostAddress() + 
                        " con id: "+idCliente);

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

        // Coloca aleatoriamente los premios en el tablero
        for (int i = 0; i < NUMERO_PREMIOS; i++) {
            int fila = random.nextInt(FILAS);
            int columna = random.nextInt(COLUMNAS);
            tablero[fila][columna] = true;
        }
    }

    private static boolean tableroCompleto() {
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

    private static void manejarCliente(Socket clienteSocket) {
        try {
            ServerWorker clienteHandler = new ServerWorker(clienteSocket, tablero,idCliente);
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
