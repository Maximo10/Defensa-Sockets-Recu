module com.example.chat_rober {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chat_rober to javafx.fxml;
    exports com.example.chat_rober;
}