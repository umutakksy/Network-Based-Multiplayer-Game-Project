package grup.proje.Lobby;

import grup.proje.Assets;
import grup.proje.Main;
import grup.proje.UI.LobbyBrowserUI;
import grup.proje.UI.LobbyUI;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LobbyClient {
    Socket socket;
    BufferedReader input;
    public PrintWriter output;
    LobbyUI lobbyUI;
    String nickname;

    public LobbyClient(LobbyUI lobbyUI) {
        this.lobbyUI = lobbyUI;
    }

    public void connect(String ip) throws Exception{
        nickname = Main.nicknameAtTextBox;
        socket = new Socket(ip, 5000);
        input =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        output =
                new PrintWriter(socket.getOutputStream(), true);

        output.println("NAME;" + nickname);

        // SERVER DİNLEYEN THREAD
        Thread listener = new Thread(() -> {
            try {
                String msg;
                while ((msg = input.readLine()) != null) {
                    if (msg.startsWith("PLAYERS;")) {
                        Assets.joinLobbySound.play();
                        String data = msg.substring(8); // PLAYERS;
                        String[] names = data.split(",");

                        List<String> playerList = new ArrayList<>();
                        for (String name : names) {
                            if (!name.isEmpty()) playerList.add(name);
                        }

                        javafx.application.Platform.runLater(() -> {
                            // UI güncelle
                            lobbyUI.setPlayers(playerList);
                        });

                    }
                    else if (msg.startsWith("CHAT;")) {
                        Assets.getMessageSound.play(0.2);
                        String[] parts = msg.split(";", 3); // CHAT, Nickname, Mesaj
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String content = parts[2];
                            System.out.println(sender+" "+content);

                            // UI güncelleneceği için Platform.runLater şart!
                            Platform.runLater(() -> {
                                lobbyUI.appendChatMessage(sender, content);
                            });
                        }
                    }

                    else if(msg.equals("SERVER_CLOSED") && !Main.isHost) {
                        System.out.println("Host oyunu kapattı");

                        // 🔥 lobby ekranına dön
                        lobbyUI.goBackToLobbyBrowser();
                        disconnect();
                    }
                }
            } catch (Exception e) {
                System.out.println("Server bağlantısı kesildi (lobby client)");
            }
        });
        listener.start();
    }

    public void disconnect(){
        try {
            if (output != null) {
                output.println("DISCONNECT");
                output.close();
            }

            if (input != null) input.close();

            if (socket != null && !socket.isClosed()) {
                socket.close(); // 🔥 en önemli satır
            }

            System.out.println("Server bağlantısı kapatıldı (lobby client)");

        } catch (IOException e) {
            System.out.println("Disconnect hatası: " + e.getMessage());
        }
    }
}