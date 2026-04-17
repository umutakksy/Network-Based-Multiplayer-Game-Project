package grup.proje.Lobby;

import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.UI.LobbyUI;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LobbyServer {
    static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    ServerSocket server;
    NetworkManager networkManager;
    LobbyUI lobbyUI;
    private Thread thread;

    public LobbyServer(NetworkManager networkManager, LobbyUI lobbyUI){
        this.networkManager = networkManager;
        this.lobbyUI = lobbyUI;
    }

    public void start(){
        thread = new Thread(() -> {
            try {
                server = new ServerSocket(5000);
                System.out.println("Server başladı");

                while (true) {
                    Socket socket = server.accept();
                    System.out.println("Bir client bağlandı");
                    ClientHandler handler = new ClientHandler(socket, this);
                    clients.add(handler);
                    networkManager.setCurrentPlayers(clients.size());
                    handler.start();
                }
            } catch (IOException e) {
                System.err.println("Server hatası: " + e.getMessage());
            }

        });

        thread.start();
    }

    public void stop(){
        try {
            if (server != null && !server.isClosed()) {
                broadcast("SERVER_CLOSED");
                server.close();
                System.out.println("Sunucu başarıyla kapatıldı. (lobby server)");
            }
        } catch (IOException e) {
            System.err.println("Kapatılırken hata oluştu: " + e.getMessage());
        }
    }

    public static void broadcast(String msg){
        for(ClientHandler client : clients){
            client.sendMessage(msg);
        }
    }

    public void sendPlayerList() {
        StringBuilder sb = new StringBuilder("PLAYERS;");

        for (ClientHandler client : clients) {
            if (client.getPlayerName() != null) {
                sb.append(client.getPlayerName()).append(",");
            }
        }
        broadcast(sb.toString());
    }
}
