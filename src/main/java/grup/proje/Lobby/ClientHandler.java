package grup.proje.Lobby;

import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {

    Socket socket;
    PrintWriter output;
    public String playerName;
    public LobbyServer lobbyServer;

    public ClientHandler(Socket socket, LobbyServer lobbyServer){
        this.socket = socket;
        this.lobbyServer = lobbyServer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void run(){
        try{
            BufferedReader input =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            output =
                    new PrintWriter(socket.getOutputStream(), true);
            String message;
            while((message = input.readLine()) != null){
                if(message.equals("DISCONNECT")) {
                    LobbyServer.clients.remove(this);
                    socket.close();

                    // 🔥 herkese yeni listeyi gönder
                    lobbyServer.sendPlayerList();
                }
                else if(message.startsWith("NAME;")) {
                    playerName = message.substring(5);

                    // isim gelince herkese listeyi tekrar gönder
                    lobbyServer.sendPlayerList();
                }
                else if (message.startsWith("CHAT;")){
                    LobbyServer.broadcast(message);
                }
                System.out.println("Client'tan server'a gelen mesaj: " + message);
                LobbyServer.broadcast(socket.getPort() + ": " + message);
            }

        }catch(Exception e){
            System.out.println("Client bağlantıyı kapattı! (clientHandler)");
        }finally {
            LobbyServer.clients.remove(this);
        }
    }

    public void sendMessage(String msg){
        output.println(msg);
    }
}