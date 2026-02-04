package com.example.chat_rober;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EchoServerMultihilo {

    private static final int PUERTO = 8080;
    private static final int MAX_CLIENTES = 10;

    // AtomicInteger: Variable thread-safe para contar clientes sin sincronización explícita
    private static final AtomicInteger clientesConectados = new AtomicInteger(0);

    // Lista de mensajes enviados por usuarios
    public static List<PrintWriter> lista_usuarios = new CopyOnWriteArrayList<>();
    //Lista de mensajes guardados
    public static List<String> cloud_chat = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        System.out.println("Servidor multihilo iniciado en puerto " + PUERTO);
        System.out.println("📊 Pool de hilos: " + MAX_CLIENTES);

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                int numCliente = clientesConectados.incrementAndGet();
                System.out.println("✅ Cliente #" + numCliente + " conectado: " +
                        clienteSocket.getInetAddress());
                pool.execute(new ManejadorClienteMultihilo(clienteSocket, numCliente));
            }
        } catch (IOException e) {
            // Captura errores de red o del socket
            System.err.println("Error en servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}

