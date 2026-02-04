package com.example.chat_rober;
import java.io.*;
import java.net.Socket;

public class EchoClient {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    public void conectar(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        salida = new PrintWriter(socket.getOutputStream(), true);
    }

    public void enviar(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    public String recibir() throws IOException {
        if (entrada != null) {
            return entrada.readLine();
        }

        return null;
    }
}