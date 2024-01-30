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
 * La clase Server gestiona el servidor del juego. Proporciona métodos para inicializar
 * el servidor, manejar conexiones con clientes, y notificar a los oyentes sobre eventos
 * y cambios en el juego, como la conexión de un nuevo cliente, el estado del tablero,
 * mensajes del servidor, y la actualización de premios encontrados por los clientes.
 * Utiliza una interfaz de escucha (ServidorListener) para enviar notificaciones a los oyentes.
 * También contiene lógica para inicializar el tablero con premios y verificar si todos los premios
 * han sido encontrados por los clientes.
 *
 * @author Dugo
 */
public class Server {

    /**
     * Puerto en el cual el servidor espera conexiones de clientes.
     */
    private static final int PORT = 4444;

    /**
     * Número de filas en el tablero del juego.
     */
    private static final int FILAS = 3;

    /**
     * Número de columnas en el tablero del juego.
     */
    private static final int COLUMNAS = 4;

    /**
     * Número total de premios en el juego.
     */
    private static final int NUMERO_PREMIOS = 4;

    /**
     * Identificador único asignado a cada cliente que se conecta al servidor.
     */
    private static int idCliente = 0;

    /**
     * Tablero del juego representado como una matriz de Strings.
     */
    private static String[][] tablero;

    /**
     * Lista de objetos que implementan la interfaz ServidorListener.
     * Estos objetos recibirán notificaciones sobre eventos en el servidor.
     */
    private static final List<ServidorListener> listeners = new ArrayList<>();

    /**
     * Array de Strings que representa los distintos premios disponibles en el juego.
     */
    private static final String[] PREMIOS = {"CRUCERO", "ENTRADAS", "CONCIERTO", "CONSOLA"};

    /**
     * Agrega un objeto que implementa ServidorListener a la lista de oyentes.
     *
     * @param listener Objeto que implementa la interfaz ServidorListener.
     */
    public static void addListener(ServidorListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifica a todos los oyentes un mensaje específico.
     *
     * @param mensaje Mensaje a ser notificado a los oyentes.
     */
    public static void notificarMensaje(String mensaje) {
        listeners.forEach(listener -> {
            listener.imprimirMensaje(mensaje);
        });
    }

    /**
     * Notifica a todos los oyentes sobre la actualización del tablero.
     *
     * @param tablero Matriz que representa el estado actual del tablero.
     */
    public static void actualizarTablero(String[][] tablero) {
        listeners.forEach(z -> z.actualizarTablero(tablero));
    }

    /**
     * Notifica a todos los oyentes sobre el premio encontrado en una posición específica del tablero.
     *
     * @param fila    Fila en la que se encuentra el premio.
     * @param columna Columna en la que se encuentra el premio.
     */
    public static void premioEncontrado(int fila, int columna) {
        tablero[fila][columna] = "SIN PREMIO"; // Eliminar el premio encontrado
        actualizarTablero(tablero);
    }

    /**
     * Método de inicialización del servidor. Inicializa el tablero, notifica a los oyentes,
     * y espera conexiones de clientes. Cada conexión de cliente se maneja en un hilo separado.
     */
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

    /**
     * Método de inicialización del tablero. Llena el tablero con premios y
     * lo notifica a los oyentes.
     */
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

    /**
     * Método que verifica si todos los premios han sido encontrados por los clientes.
     *
     * @return true si todos los premios han sido encontrados, false de lo contrario.
     */
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

    /**
     * Método que maneja la conexión con un cliente específico.
     *
     * @param clienteSocket Socket de la conexión con el cliente.
     */
    private void manejarCliente(Socket clienteSocket) {
        try {
            ServerWorker clienteHandler = new ServerWorker(clienteSocket, tablero, idCliente);
            clienteHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el número de filas en el tablero del juego.
     *
     * @return Número de filas.
     */
    public static int getFILAS() {
        return FILAS;
    }

    /**
     * Obtiene el número de columnas en el tablero del juego.
     *
     * @return Número de columnas.
     */
    public static int getCOLUMNAS() {
        return COLUMNAS;
    }

    /**
     * Obtiene la representación actual del tablero del juego.
     *
     * @return Tablero del juego.
     */
    public static String[][] getTablero() {
        return tablero;
    }
}
