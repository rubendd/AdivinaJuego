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
 * La clase ServerWorker maneja la conexión y la comunicación con un cliente individual.
 * Cada instancia de esta clase se crea para manejar la conexión con un cliente específico,
 * y se ejecuta en un hilo separado para permitir la comunicación simultánea con múltiples clientes.
 * Proporciona métodos para procesar los intentos de los clientes, notificar eventos al servidor,
 * y cerrar adecuadamente la conexión con el cliente al finalizar la comunicación.
 *
 * @author Dugo
 */
public class ServerWorker extends Thread {

    /**
     * Socket que representa la conexión con el cliente.
     */
    private final Socket clienteSocket;

    /**
     * Flujo de entrada para recibir datos del cliente.
     */
    private final BufferedReader entrada;

    /**
     * Flujo de salida para enviar datos al cliente.
     */
    private final PrintWriter salida;

    /**
     * Tablero del juego compartido con otros clientes.
     */
    private final String[][] tablero;

    /**
     * Identificador único asignado al cliente.
     */
    private final int id;

    /**
     * Número de premios encontrados por el cliente.
     */
    private int numeroPremios = 0;

    /**
     * Constructor de la clase ServerWorker.
     * Inicializa los flujos de entrada/salida y otros datos necesarios para la comunicación con el cliente.
     *
     * @param clienteSocket Socket que representa la conexión con el cliente.
     * @param tablero       Tablero del juego compartido con otros clientes.
     * @param id            Identificador único asignado al cliente.
     * @throws IOException Si hay un error al crear los flujos de entrada/salida.
     */
    public ServerWorker(Socket clienteSocket, String[][] tablero, int id) throws IOException {
        this.clienteSocket = clienteSocket;
        this.entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
        this.salida = new PrintWriter(clienteSocket.getOutputStream(), true);
        this.tablero = tablero;
        this.id = id;
    }

    /**
     * Método principal que se ejecuta cuando el hilo del servidor está activo.
     * Maneja la comunicación con el cliente, procesa sus intentos y notifica eventos al servidor.
     * Cierra adecuadamente la conexión con el cliente al finalizar la comunicación.
     */
    @Override
    public void run() {
        try {
            Server.notificarMensaje("Cliente conectado => " + id);
            salida.println(id);

            while (!tableroCompleto()) {
                int fila = Integer.parseInt(entrada.readLine()) - 1;
                int columna = Integer.parseInt(entrada.readLine()) - 1;

                String premioEncontrado = tablero[fila][columna];

                if (!premioEncontrado.equals("SIN PREMIO")) {
                    realizarPremioEncontrado(fila, columna, premioEncontrado);
                } else {
                    realizarSinPremio(fila, columna);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarClienteSocket();
        }
    }

    /**
     * Procesa cuando un premio es encontrado por el cliente.
     * Notifica al servidor, actualiza el tablero, envía mensajes al cliente y actualiza el número de premios.
     *
     * @param fila            Fila en la que se encontró el premio.
     * @param columna         Columna en la que se encontró el premio.
     * @param premioEncontrado Nombre del premio encontrado.
     */
    private void realizarPremioEncontrado(int fila, int columna, String premioEncontrado) {
        numeroPremios++;
        Server.notificarMensaje("Cliente " + id + " =>" + " Premio encontrado en fila " + "[" + fila + "]" +
                ", columna " + "[" + columna + "]");
        premioEncontrado(fila, columna);
        Server.actualizarTablero(Server.getTablero());
        salida.println("[" + fila + "]" + " " + "[" + columna + "]" + "Has encontrado " + premioEncontrado);
        salida.println(numeroPremios);
    }

    /**
     * Procesa la situación cuando no se encuentra un premio en la posición especificada por el cliente.
     * Notifica al servidor, envía mensajes al cliente y actualiza el número de premios.
     *
     * @param fila    Fila en la que se realizó el intento sin premio.
     * @param columna Columna en la que se realizó el intento sin premio.
     */
    private void realizarSinPremio(int fila, int columna) {
        Server.notificarMensaje("Cliente " + id + " =>" + " No hay premio en fila " + fila + ", columna " + columna);
        fila++;
        columna++;
        salida.println("[" + fila + "]" + " " + "[" + columna + "]" + " Sin premio");
        salida.println(numeroPremios);
    }

    /**
     * Verifica si el tablero del juego está completo (todos los premios han sido encontrados).
     *
     * @return true si todos los premios han sido encontrados, false de lo contrario.
     */
    private boolean tableroCompleto() {
        return Server.tableroCompleto();
    }

    /**
     * Notifica al servidor que un premio ha sido encontrado en la posición especificada.
     *
     * @param fila    Fila en la que se encontró el premio.
     * @param columna Columna en la que se encontró el premio.
     */
    private void premioEncontrado(int fila, int columna) {
        Server.premioEncontrado(fila, columna);
    }

    /**
     * Cierra adecuadamente el socket de conexión con el cliente y notifica al servidor.
     */
    private void cerrarClienteSocket() {
        try {
            try (clienteSocket) {
                Server.notificarMensaje("Cliente cerrado => " + id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
