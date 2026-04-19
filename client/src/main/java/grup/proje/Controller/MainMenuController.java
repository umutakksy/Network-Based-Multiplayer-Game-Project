package grup.proje.Controller;

import grup.proje.Assets;
import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import grup.proje.UI.LobbyBrowserUI;

public class MainMenuController {
    private ScreenManager screenManager;
    private NetworkManager networkManager;
    private LobbyBrowserUI lobbyBrowserUI;

    public MainMenuController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setBrowserUI(LobbyBrowserUI lobbyBrowserUI) {
        this.lobbyBrowserUI = lobbyBrowserUI;
    }

    public void CreateLobby(){
        Assets.createLobbySound.play();
        String nickname = Main.nicknameAtTextBox;
        try {
            networkManager.createLobby(nickname + "'s Lobby", "", nickname);
            screenManager.showLobby();
        }
        catch (Exception ex) {
            System.err.println("Lobi olusturma hatasi: " + ex.getMessage());
        }
    }

    public void JoinLobby(){
        String nickname = Main.nicknameAtTextBox;
        screenManager.showLobbyBrowser();
        networkManager.refreshLobbies();
        if (lobbyBrowserUI != null) {
            lobbyBrowserUI.updateLobbyList(networkManager.getActiveLobbies());
        }
    }
}
