/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

/**
 *
 * @author Dugo
 */
public interface ClienteListener {
    void enviarId(int id);
    void enviarNumeroJuagdas(int jugadas);
    void enviarMensajeServidor(String mensaje);
}
