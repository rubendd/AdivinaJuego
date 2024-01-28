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
 *
 * @author Dugo
 */
public final class Cliente {

    private static final List<ClienteListener> clienteListeners = new ArrayList<>();
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4444;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader userInput;
    private int clienteId;
    private int jugadas = 4;
    private int premios;

    public static void addListener(ClienteListener listener) {
        clienteListeners.add(listener);
    }

    private void mandarId() {
        try {
            clienteId = Integer.parseInt(reader.readLine());
            clienteListeners.forEach(l -> l.enviarId(clienteId));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mandarJugadas() {
        clienteListeners.forEach(l -> l.enviarNumeroJuagdas(jugadas));
    }

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

    private void mandarPremios() {
        try {
            premios = Integer.parseInt(reader.readLine());
            clienteListeners.forEach(l -> l.enviarNumeroPremios(premios));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mandarTerminarPartida() {
        clienteListeners.forEach(l -> l.finalizarPartida());
    }

    public void mandarIntentoServer(int fila, int columna) {
        writer.println(fila);
        writer.println(columna);
        jugadas--;
        mandarJugadas();
    }

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

    private boolean errorDeConexion() {
        if (socket == null) {
            clienteListeners.forEach(l -> l.enviarErrorConexion());
            return true;
        }
        return false;

    }

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
