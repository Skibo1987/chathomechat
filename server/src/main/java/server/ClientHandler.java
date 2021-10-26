package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    Socket socket;
    Server server;
    DataInputStream in;
    DataOutputStream out;
    private boolean autenticated;
    private String nickname;

    public ClientHandler(Socket socket, Server server) {

        try {
            this.socket = socket;
            this.server = server;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //authentification
                    while (true) {
                        String str = in.readUTF();


                        if (in.equals("/end")) {
                            sendMsg("/end");
                            System.out.println("Client Disconnected");
                            break;
                        }
                        if (str.startsWith("/auth ")) {
                            String[] token = str.split("\\s+");
                            nickname = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);
                            if (nickname != null) {
                                server.subscribe(this);
                                autenticated = true;
                                sendMsg("/authok " + nickname);
                                break;
                            } else {
                                sendMsg("Wrong login/password");
                            }

                        }


                    }
                    //work
                    while (autenticated) {
                        String str = in.readUTF();

                        if (in.equals("/end")) {
                            sendMsg("/end");
                            System.out.println("Client Disconnected");
                            break;

                        }
                        //SQL//
                        if (str.startsWith("/chnick ")) {
                            String[] token = str.split("\\s+", 2);
                            if (token.length < 2){
                                continue;
                            }
                            if (token[1].contains(" ") ) {
                                sendMsg("No beckspases in nickname");
                                continue;

                            }
                            if (server.getAuthService().changeNick(this.nickname, token[1])){
                                sendMsg("/yournickis " + token[1]);
                                sendMsg("Your nickname is changed by " + token[1]);
                                this.nickname = token[1];
                                //server.broadcastClientList();


                            }else {
                                sendMsg("Can't chang nick. Nick " + token[1] + "already exist");
                            }

                        }


                        //SQL//

                        server.broadcastMsg(this, str);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
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

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
