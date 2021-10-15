package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textFeald;

    private Socket socket;
    private static final int PORT = 8189;
    private final String IP_ADDRESS = "localhost";
    private DataInputStream in;
    private DataOutputStream out;


    @FXML
    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(textFeald.getText());
            textFeald.clear();
            textFeald.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();

                        if (in.equals("/end")) {
                            System.out.println("Client Disconnected");
                            break;
                        }
                        textArea.appendText(str + "\n");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
