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

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4444;

    private static final List<ClienteListener> clienteListeners = new ArrayList<>();

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader userInput;
    private int clienteId;
    private int jugadas = 3;

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

    private void mandarMensajeServidor() {

        try {
            String mensaje = reader.readLine();
            clienteListeners.forEach(l -> l.enviarMensajeServidor(mensaje));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void mandarIntentoServer(int fila, int columna) {
        if (jugadas > 0) {
            writer.println(fila);
            writer.println(columna);
            jugadas--;
        }
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
        try {
            mandarId();
            while (true) {
                mandarMensajeServidor();
                if (juegoTerminado()) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }
    }

    private boolean juegoTerminado() throws IOException {
        String respuestaServidor = reader.readLine();

        return respuestaServidor.contains("premios ya han sido encontrados");
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
