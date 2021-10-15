package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8189;
    private List<ClientHandler> clients;


    public Server() {
        clients = new CopyOnWriteArrayList<>();
        try {

            server = new ServerSocket(PORT);
            System.out.println("Server started!");


            while (true) {
                socket = server.accept();
                System.out.println("Client Connected");
                subscribe(new ClientHandler(socket, this));

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public void broadcastMsg(String msg) {
        for (ClientHandler c : clients){
           c.sendMsg(msg);
        }
    }
    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);

    }
    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }


}
