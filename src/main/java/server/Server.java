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
    private static final int NUMERO_PREMIOS = 4; // Número total de premios
    private static int idCliente = 0;
    private static String[][] tablero; // Cambiado a String
    private static final List<ServidorListener> listeners = new ArrayList<>();

    private static final String[] PREMIOS = {"CRUCERO", "ENTRADAS", "CONCIERTO", "CONSOLA"};

    public static void addListener(ServidorListener listener) {
        listeners.add(listener);
    }

    public static void notificarMensaje(String mensaje) {
        listeners.forEach(listener -> {
            listener.imprimirMensaje(mensaje);
        });
    }

    public static void actualizarTablero(String[][] tablero) {
        listeners.forEach(z -> z.actualizarTablero(tablero));
    }

    public static void premioEncontrado(int fila, int columna) {
        tablero[fila][columna] = "SIN PREMIO"; // Eliminar el premio encontrado
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
                    notificarMensaje("Servidor: Todos los premios ya han sido encontrados.");
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
        tablero = new String[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                tablero[i][j] = "SIN PREMIO"; // Inicializar todas las celdas sin premios
            }
        }

        Random random = new Random();
        int premiosColocados = 0;

        while (premiosColocados < NUMERO_PREMIOS) {
            int fila = random.nextInt(FILAS);
            int columna = random.nextInt(COLUMNAS);

            if (tablero[fila][columna].equals("SIN PREMIO")) {
                tablero[fila][columna] = PREMIOS[premiosColocados];
                premiosColocados++;
            }
        }
    }

    public synchronized static boolean tableroCompleto() {
        for (String[] fila : tablero) {
            for (String premio : fila) {
                if (!premio.equals("SIN PREMIO")) {
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

    public static String[][] getTablero() {
        return tablero;
    }

}
