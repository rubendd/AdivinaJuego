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
    private final String[][] tablero;
    private final int id;
    private int numeroPremios = 0;

    public ServerWorker(Socket clienteSocket, String[][] tablero, int id) throws IOException {
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

    private void realizarPremioEncontrado(int fila, int columna, String premioEncontrado) {
        numeroPremios++;
        Server.notificarMensaje("Cliente "+id + " =>"+" Premio encontrado en fila " + "[" + fila + "]" + ", columna " + "[" + columna + "]");
        premioEncontrado(fila, columna);
        Server.actualizarTablero(Server.getTablero());
        salida.println("[" + fila + "]" + " " + "[" + columna + "]" + "Has encontrado " + premioEncontrado);
        salida.println(numeroPremios);
    }

    private void realizarSinPremio(int fila, int columna) {
        Server.notificarMensaje("Cliente "+id + " =>"+ " No hay premio en fila " + fila + ", columna " + columna);
        fila++;
        columna++;
        salida.println("[" + fila + "]" + " " + "[" + columna + "]" + " Sin premio");
        salida.println(numeroPremios);
    }

    private boolean tableroCompleto() {
        return Server.tableroCompleto();
    }

    private void premioEncontrado(int fila, int columna) {
        Server.premioEncontrado(fila, columna);
    }

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
