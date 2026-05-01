package grup.proje;

import grup.proje.Lobby.LobbyInfo;
import grup.proje.UI.LobbyUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkManager {
    private final BackendClient backendClient = new BackendClient();
    private final ObservableList<LobbyInfo> activeLobbies = FXCollections.observableArrayList();
    public LobbyUI lobbyUI;
    private String currentLobbyId;

    public void setUI(LobbyUI lobbyUI){
        this.lobbyUI = lobbyUI;
    }

    public void refreshLobbies() {
        try {
            List<Map<String, Object>> lobbies = backendClient.listLobbies();
            List<LobbyInfo> lobbyInfos = lobbies.stream()
                    .map(LobbyInfo::fromMap)
                    .collect(Collectors.toList());
            Platform.runLater(() -> activeLobbies.setAll(lobbyInfos));
        } catch (Exception e) {
            System.err.println("Lobi listesi guncellenemedi: " + e.getMessage());
        }
    }

    public ObservableList<LobbyInfo> getActiveLobbies() {
        return activeLobbies;
    }

    public void createLobby(String name, String password, String creator) throws Exception {
        Map<String, Object> lobby = backendClient.createLobby(name, password, creator, 4);
        this.currentLobbyId = (String) lobby.get("id");
        startPolling();
    }

    public void joinLobby(String lobbyId, String username, String password) throws Exception {
        backendClient.joinLobby(lobbyId, password);
        this.currentLobbyId = lobbyId;
        startPolling();
    }

    public void leaveLobby(String username) {
        if (currentLobbyId != null) {
            try {
                stopPolling();
                backendClient.leaveLobby(currentLobbyId);
                currentLobbyId = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private java.util.concurrent.ScheduledExecutorService scheduler;
    private int lastMessageCount = 0;

    private void startPolling() {
        stopPolling();
        scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::pollUpdates, 0, 1500, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void stopPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        lastMessageCount = 0;
    }

    private void pollUpdates() {
        if (currentLobbyId == null) return;
        try {
            Map<String, Object> details = backendClient.getLobbyDetails(currentLobbyId);
            List<String> players = (List<String>) details.get("memberUsernames");
            List<String> chat = (List<String>) details.get("chatHistory");

            Platform.runLater(() -> {
                if (lobbyUI != null) {
                    lobbyUI.setPlayers(players);
                    if (chat.size() > lastMessageCount) {
                        for (int i = lastMessageCount; i < chat.size(); i++) {
                            String[] parts = chat.get(i).split(";", 3);
                            if (parts.length == 3) {
                                lobbyUI.appendChatMessage(parts[1], parts[2]);
                            }
                        }
                        lastMessageCount = chat.size();
                    }
                }
            });
        } catch (Exception e) {
            // handle error
        }
    }

    public void sendChatMessage(String nickname, String message) {
        if (currentLobbyId != null) {
            try {
                backendClient.sendChat(currentLobbyId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Auth methods
    public Map<String, Object> login(String username, String password) throws Exception {
        return backendClient.login(username, password);
    }

    public void register(String username, String password) throws Exception {
        backendClient.register(username, password);
    }

    // Compatibility methods (Legacy)
    public void startBroadcast(String h, int m) {}
    public void stopBroadcast() {}
    public void startDiscovery() {}
    public void stopDiscovery() {}
    public void startServer() {}
    public void stopServer() {}
    public void disconnectClient() {
        stopPolling();
    }
    public void setLobbyStatus(String s) {}
    public void setCurrentPlayers(int c) {}
}