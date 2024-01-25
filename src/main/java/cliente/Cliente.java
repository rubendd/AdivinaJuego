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

    private static List<ClienteListener> clienteListeners = new ArrayList<>();

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader userInput;
    private int clienteId;
    private int jugadas = 3;

    public static void addListener(ClienteListener listener) {
        clienteListeners.add(listener);
    }

    public void mandarId() {
        try {
            clienteId = Integer.parseInt(reader.readLine());
            clienteListeners.forEach(l -> l.enviarId(clienteId));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mandarNumeroJugadas() {

    }

    public void mandarIntentoServer() {
        
        jugadas--;        
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
                if (juegoTerminado()) {
                    break;
                }

                int fila = obtenerEntradaUsuario("Ingrese la fila (0-2): ");
                int columna = obtenerEntradaUsuario("Ingrese la columna (0-3): ");

                enviarMovimientoAlServidor(fila, columna);

                // Receive and print the server's response
                System.out.println(reader.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }
    }

    private boolean juegoTerminado() throws IOException {
        String respuestaServidor = reader.readLine();
        System.out.println(respuestaServidor);

        return respuestaServidor.contains("premios ya han sido encontrados");
    }

    private int obtenerEntradaUsuario(String mensaje) throws IOException {
        System.out.print(mensaje);
        return Integer.parseInt(userInput.readLine());
    }

    private void enviarMovimientoAlServidor(int fila, int columna) {
        writer.println(fila + "," + columna);
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
