package com.example.chat_rober;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class HelloController {

    @FXML private TextArea Area_texto;      // TextArea de mensajes
    @FXML private TextField bara_texto;     // Campo para escribir mensajes
    @FXML private Button btn_enviar;        // Botón enviar
    @FXML private Button nuevaVentana;      // Botón abrir nueva ventana

    private EchoClient cliente;       // Cliente socket
    private String nombreCliente;     // Nombre ingresado por el usuario

    @FXML
    public void initialize() {

        // Pedir nombre al usuario
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nombre de usuario");
        dialogo.setHeaderText("Ingresa tu nombre para el chat:");
        dialogo.setContentText("Nombre:");

        Optional<String> resultado = dialogo.showAndWait();
        // Asignar nombreCliente de forma simple
        if (resultado.isPresent() && !resultado.get().trim().isEmpty()) {
            nombreCliente = resultado.get().trim();
        } else {
            nombreCliente = "Usuario" + (System.currentTimeMillis() % 1000);
        }

        // Crear un nuevo cliente
        cliente = new EchoClient();

        // Hilo para conectarse y recibir mensajes
        new Thread(() -> {
            try {
                // Conectar al servidor
                cliente.conectar("localhost", 8080);

                // Enviar nombre al servidor
                cliente.enviar(nombreCliente);

                // Mensaje inicial en la UI
                Platform.runLater(() ->
                        Area_texto.appendText("✅ Conectado como " + nombreCliente + "\n")
                );

                // Recibir mensajes del servidor continuamente
                String mensaje;
                while ((mensaje = cliente.recibir()) != null) {
                    String finalMensaje = mensaje;
                    Platform.runLater(() ->
                            Area_texto.appendText(finalMensaje + "\n")
                    );
                    if (mensaje.equalsIgnoreCase("limpiar")){
                        Platform.runLater(() -> Area_texto.clear());
                    }
                }

            } catch (IOException e) {
                Platform.runLater(() ->
                        Area_texto.appendText("❌ Error: " + e.getMessage() + "\n")
                );
            }
        }).start();

        // Acción del botón enviar
        btn_enviar.setOnAction(e -> {
            String texto = bara_texto.getText().trim();
            if (!texto.isEmpty()) {
                cliente.enviar(texto);
                bara_texto.clear();
            }
        });

        // Acción del botón nueva ventana
        nuevaVentana.setOnAction(e -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                stage.setScene(new Scene(loader.load(), 390, 615));
                stage.setTitle("Grupo Rober");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
