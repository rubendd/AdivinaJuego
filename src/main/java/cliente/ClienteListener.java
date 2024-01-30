/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

/**
 * La interfaz ClienteListener incluye eventos para la recepción de la ID del 
 * cliente, la actualización del número de jugadas, la recepción de mensajes 
 * del servidor, la actualización del número de premios, la notificación de 
 * finalización de la partida y la notificación de errores de conexión.
 *
 * @author Dugo
 */
public interface ClienteListener {

    /**
     * Notifica a los oyentes sobre la ID asignada al cliente por el servidor.
     *
     * @param id Identificador único del cliente.
     */
    void enviarId(int id);

    /**
     * Notifica a los oyentes sobre el número actualizado de jugadas restantes.
     *
     * @param jugadas Número de jugadas restantes.
     */
    void enviarNumeroJuagdas(int jugadas);

    /**
     * Notifica a los oyentes sobre un mensaje recibido del servidor.
     *
     * @param mensaje Mensaje recibido del servidor.
     */
    void enviarMensajeServidor(String mensaje);

    /**
     * Notifica a los oyentes sobre el número actualizado de premios acumulados.
     *
     * @param premios Número de premios acumulados por el cliente.
     */
    void enviarNumeroPremios(int premios);

    /**
     * Notifica a los oyentes sobre la finalización de la partida.
     */
    void finalizarPartida();

    /**
     * Notifica a los oyentes sobre un error de conexión con el servidor.
     */
    void enviarErrorConexion();
}
