package com.example.chat_rober;


import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ManejadorClienteMultihilo implements Runnable {
    private final Socket socket;
    private final int numeroCliente;

    public ManejadorClienteMultihilo(Socket socket, int numeroCliente) {
        this.socket = socket;
        this.numeroCliente = numeroCliente;
    }

    public void run() {
        PrintWriter salida = null;

        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("¡Bienvenido! Eres el cliente #" + numeroCliente);
            // Agregar cliente a la lista global
            EchoServerMultihilo.lista_usuarios.add(salida);

            // ver cada mensaje del historial y mostrarlos
            for (String cada_mensaje : EchoServerMultihilo.cloud_chat) {
                salida.println(cada_mensaje);
            }

            String mensaje;
            // que tenga un conenido el mensaje
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("[Usuario #" + numeroCliente + "] " + mensaje);

                // Reenviar a todos los clientes conectados
                for (PrintWriter cliente : EchoServerMultihilo.lista_usuarios) {
                    cliente.println("[Usuario #" + numeroCliente + "] " + mensaje);
                }

                // Guardar mensaje en historial
                EchoServerMultihilo.cloud_chat.add("[Usuario #" + numeroCliente + "] " + mensaje);

                // Si el cliente se desconecta
                if (mensaje.equalsIgnoreCase("salir")) {
                    //recorrer los clientes conectados
                    for (PrintWriter cliente : EchoServerMultihilo.lista_usuarios) {
                        //mesaje de despedida
                        cliente.println("👋 Usuario #" + numeroCliente + " se ha desconectado");
                    }
                    break;
                }

                if(mensaje.equalsIgnoreCase("roll")){
                    double dado = (Math.random()/1000);
                    salida.println(dado);
                }

                if (mensaje.equalsIgnoreCase("limpiar")){
                    EchoServerMultihilo.cloud_chat.remove(salida);
                    EchoServerMultihilo.lista_usuarios.remove(salida);
                }
            }

        } catch (IOException e) {
            System.err.println("Error con cliente #" + numeroCliente + ": " + e.getMessage());
        } finally {
            // Quitar cliente de la lista y cerrar socket
            if (salida != null) {
                EchoServerMultihilo.lista_usuarios.remove(salida);
            }
            try {
                socket.close();
                System.out.println("❌ Cliente #" + numeroCliente + " desconectado");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}