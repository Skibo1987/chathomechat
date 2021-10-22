package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private HBox msgPanel;
    @FXML
    private HBox authPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    private Socket socket;
    private static final int PORT = 8189;
    private final String IP_ADDRESS = "localhost";
    private DataInputStream in;
    private DataOutputStream out;

    private boolean authenticated;
    private String nickname;
    private Stage stage;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);

        if (!authenticated) {
            nickname = "";

        }
        setTitle(nickname);
        textArea.clear();

    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //autentification
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {

                            if (in.equals("/end")) {
                                System.out.println("Client Disconnected");
                                break;
                            }
                            if (str.startsWith("/authok")) {
                                nickname = str.split("\\s")[1];
                                setAuthenticated(true);
                                break;
                            }

                        } else {
                            textArea.appendText(str + "\n");
                        }

                    }

                    //work
                    while (authenticated) {
                        String str = in.readUTF();

                        if (in.equals("/end")) {

                            break;
                        }
                        textArea.appendText(str + "\n");

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Client Disconnected");
                    setAuthenticated(false);
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

    @FXML
    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (socket != null && !socket.isClosed()){
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
        setAuthenticated(false);
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String msg = String.format("/auth %s %s", login, password);

        try {
            out.writeUTF(msg);
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nickname) {

        Platform.runLater(() -> {
            if (!nickname.equals("")) {
                stage.setTitle(String.format("Chat_Home[ %s ]", nickname));
            } else {
                stage.setTitle("Chat_Home");
            }
        });

    }
}
