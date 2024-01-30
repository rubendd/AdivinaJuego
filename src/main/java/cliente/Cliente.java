/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase gestiona el cliente para un juego multijugador. Proporciona
 * métodos para establecer conexión con el servidor, enviar y recibir
 * información, y gestionar recursos de red.
 *
 * @author Dugo
 */
public final class Cliente {

    /**
     * Lista de objetos que implementan la interfaz ClienteListener. Estos
     * objetos recibirán notificaciones sobre eventos en el cliente.
     */
    private static final List<ClienteListener> clienteListeners = new ArrayList<>();

    private static final String SERVER_IP = "192.168.1.139";

    private static final int SERVER_PORT = 4444;

    private Socket socket;

    private BufferedReader reader;

    private PrintWriter writer;

    private BufferedReader userInput;

    private int clienteId;

    private int jugadas = 4;

    private int premios;

    /**
     * Agrega un objeto que implementa ClienteListener a la lista de oyentes.
     *
     * @param listener Objeto que implementa la interfaz ClienteListener.
     */
    public static void addListener(ClienteListener listener) {
        clienteListeners.add(listener);
    }

    /**
     * Recibe el identificador del cliente por parte del servidor.
     */
    private void mandarId() {
        try {
            clienteId = Integer.parseInt(reader.readLine());
            clienteListeners.forEach(l -> l.enviarId(clienteId));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Envía el número de jugadas restantes a todos los oyentes registrados.
     */
    private void mandarJugadas() {
        clienteListeners.forEach(l -> l.enviarNumeroJuagdas(jugadas));
    }

    /**
     * Envía un mensaje recibido del servidor a todos los oyentes registrados.
     *
     * @throws IOException Si hay un error al leer el mensaje del servidor.
     */
    private void mandarMensajeServidor() throws IOException {
        try {
            String mensaje = reader.readLine();
            if (!mensaje.equals("null")) {
                clienteListeners.forEach(l -> l.enviarMensajeServidor(mensaje));
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Envía el número de premios acumulados a todos los oyentes registrados.
     */
    private void mandarPremios() {
        try {
            premios = Integer.parseInt(reader.readLine());
            clienteListeners.forEach(l -> l.enviarNumeroPremios(premios));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Notifica a todos los oyentes registrados que la partida ha terminado.
     */
    private void mandarTerminarPartida() {
        clienteListeners.forEach(l -> l.finalizarPartida());
    }

    /**
     * Envía un intento (fila y columna) al servidor y actualiza el número de
     * jugadas.
     *
     * @param fila Fila del intento.
     * @param columna Columna del intento.
     */
    public void mandarIntentoServer(int fila, int columna) {
        writer.println(fila);
        writer.println(columna);
        jugadas--;
        mandarJugadas();
    }

    /**
     * Constructor de la clase Cliente. Establece la conexión con el servidor al
     * instanciar un objeto de esta clase.
     */
    public Cliente() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicia el juego del cliente. Envía la ID y el número de jugadas al
     * servidor, y luego entra en un bucle para recibir mensajes y premios del
     * servidor. Finalmente, notifica a los oyentes que la partida ha terminado
     * y cierra recursos.
     */
    public void jugar() {
        if (!errorDeConexion()) { // Si no hay error de conexión.
            try {
                mandarId(); // Al iniciar la pantalla mandamos la id proporcionada por el server.
                mandarJugadas(); // Al iniciar la pantalla cliente mandamos el numero de jugadas.
                while (true) {
                    mandarMensajeServidor();
                    mandarPremios();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mandarTerminarPartida();
                cerrarRecursos();
            }
        }
    }

    /**
     * Verifica si hay un error de conexión y notifica a los oyentes
     * registrados.
     *
     * @return true si hay un error de conexión, false de lo contrario.
     */
    private boolean errorDeConexion() {
        if (socket == null) {
            clienteListeners.forEach(l -> l.enviarErrorConexion());
            return true;
        }
        return false;
    }

    /**
     * Cierra los recursos de red (socket, reader, writer, userInput).
     */
    private void cerrarRecursos() {
        try {
            socket.close();
            reader.close();
            writer.close();
            userInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
