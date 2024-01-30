package server;

/**
 * La interfaz ServidorListener define los métodos que deben ser implementados
 * por las clases que deseen recibir notificaciones de eventos en el servidor.
 * Estos eventos incluyen la impresión de mensajes del servidor y la actualización
 * del tablero de juego.
 *
 * @author Dugo
 */
public interface ServidorListener {

    /**
     * Notifica a los oyentes sobre un mensaje impreso por el servidor.
     *
     * @param mensaje Mensaje impreso por el servidor.
     */
    void imprimirMensaje(String mensaje);

    /**
     * Notifica a los oyentes sobre la actualización del tablero de juego.
     *
     * @param tablero Matriz que representa el estado actual del tablero.
     */
    void actualizarTablero(String[][] tablero);
}
