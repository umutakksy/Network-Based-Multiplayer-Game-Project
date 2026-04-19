package grup.proje.Controller;

import grup.proje.Assets;
import grup.proje.Main;
import grup.proje.Lobby.LobbyInfo;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import grup.proje.UI.LobbyBrowserUI;
import javafx.scene.control.Alert;

public class LobbyBrowserController {

    private ScreenManager screenManager;
    private NetworkManager networkManager;

    private LobbyBrowserUI lobbyBrowserUI;

    public LobbyBrowserController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setUI(LobbyBrowserUI lobbyBrowserUI) {
        this.lobbyBrowserUI = lobbyBrowserUI;
    }

    public void Join(LobbyInfo lobby){
        if(lobby == null) return;
        try{
            // Şifre varsa sorulabilir, şimdilik boş gönderiyoruz
            networkManager.joinLobby(lobby.id, Main.nicknameAtTextBox, "");
            screenManager.showLobby();
            System.out.println("Lobiye bağlama isteği gönderildi!");
        }catch(Exception e){
            System.err.println("Lobiye katilma hatasi: " + e.getMessage());
            showError("Lobiye katilamadi: " + e.getMessage());
        }
    }

    public void Back(){
        screenManager.showMainMenu();
    }

    public void Refresh(){
        networkManager.refreshLobbies();
    }

    public void stopDiscovery(){
        // Artık discovery yok
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

