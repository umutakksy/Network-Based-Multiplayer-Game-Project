package grup.proje.Controller;

import grup.proje.Assets;
import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import grup.proje.UI.LobbyUI;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class LobbyController {

    private ScreenManager screenManager;
    private NetworkManager networkManager;
    private LobbyUI lobbyUI;

    public LobbyController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setUI(LobbyUI lobbyUI) {
        this.lobbyUI = lobbyUI;
    }

    public void StartGame(){
        screenManager.showGame();
    }

    public void LeaveLobby() {
        networkManager.leaveLobby(Main.nicknameAtTextBox);
        screenManager.showLobbyBrowser();
    }

    public void SendMessage(String msg){
        if (msg == null || msg.trim().isEmpty()) return;
        Assets.sendMessageSound.play(0.2);
        networkManager.sendChatMessage(Main.nicknameAtTextBox, msg);
        if (lobbyUI != null) {
            lobbyUI.clearChatInput();
        }
    }

    public void goBackToLobbyBrowser(){
        Platform.runLater(() -> {
            showError("Host lobiden çıktı!");
            screenManager.showLobbyBrowser();
        });
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

