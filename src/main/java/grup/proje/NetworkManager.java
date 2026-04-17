package grup.proje;

import grup.proje.Lobby.*;

import grup.proje.UI.LobbyBrowserUI;
import grup.proje.UI.LobbyUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.JavaFXBuilderFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * NetworkManager:
 * - UDP broadcast / discovery
 * - TCP server / client
 * - Chat ve Ready sistemi altyapısı
 */
public class NetworkManager {

    // Modüller
    private LobbyBroadcaster broadcaster;
    private LobbyDiscovery discovery;
    private LobbyServer server;
    private LobbyClient client;

    private ObservableList<LobbyInfo> activeLobbies = FXCollections.observableArrayList();

    private String hostName;
    private int maxPlayers = 3;
    private int currentPlayers = 1;

    public LobbyUI lobbyUI;

    public void setUI(LobbyUI lobbyUI){
        this.lobbyUI = lobbyUI;
    }

    // ------------------- BROADCAST -------------------
    public void startBroadcast(String hostName, int maxPlayers) {
        if (broadcaster != null) broadcaster.stop();
        this.hostName = hostName;
        this.maxPlayers = maxPlayers;
        broadcaster = new LobbyBroadcaster(hostName, maxPlayers);
        broadcaster.setCurrentPlayers(currentPlayers);
        broadcaster.start();
    }

    public void stopBroadcast() {
        if (broadcaster != null) broadcaster.stop();
    }

    public void setCurrentPlayers(int count) {
        this.currentPlayers = count;
        if (broadcaster != null) broadcaster.setCurrentPlayers(count);
    }

    public void setLobbyStatus(String status) {
        if (broadcaster != null) broadcaster.setStatus(status);
    }

    // ------------------- DISCOVERY -------------------
    public void startDiscovery() {
        if (discovery != null) stopDiscovery();
        activeLobbies.clear();
        discovery = new LobbyDiscovery(activeLobbies);
        discovery.start();
    }

    public void stopDiscovery() {
        if (discovery != null) discovery.stop();
    }

    public ObservableList<LobbyInfo> getActiveLobbies() {
        return activeLobbies;
    }

    // ------------------- TCP SERVER -------------------
    public void startServer() {
        server = new LobbyServer(this, lobbyUI);
        server.start();
    }

    public void stopServer() {
        if (server != null) server.stop();
    }

    // ------------------- TCP CLIENT -------------------
    public void connectToServer(String ip) throws Exception{
        client = new LobbyClient(lobbyUI);
        client.connect(ip);
    }

    public void disconnectClient() {
        if (client != null) client.disconnect();
    }

    // ------------------- CHAT -------------------
    public void sendChatMessage(String nickname, String message) {
        String formattedMessage = "CHAT;" + nickname + ";" + message;
        if (client != null) {
            client.output.println(formattedMessage); // Server'a gönder
        } else if (server != null) {
            // Eğer mesajı atan Host ise, direkt herkese yay
            LobbyServer.broadcast(formattedMessage);
            //server.getCHATmsg(formattedMessage);
        }
    }

    // Gelen mesajları handle et
    private void receiveMessage(String msg) {
        if (msg.startsWith("CHAT;")) {
            String chatMsg = msg.substring(5); // "CHAT;" kısmını çıkar
        }
    }

    // ------------------- PLAYER LIST -------------------
    private List<String> players = new ArrayList<>();

    public void addPlayer(String name) {
        if (!players.contains(name)) players.add(name);
    }

    public void removePlayer(String name) {
        players.remove(name);
    }
}