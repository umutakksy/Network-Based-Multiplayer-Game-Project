package grup.proje.Controller;

import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.Map;

public class LoginController {

    private ScreenManager screenManager;
    private NetworkManager networkManager;

    public LoginController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void onLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Lütfen tüm alanları doldurun.");
            return;
        }

        new Thread(() -> {
            try {
                Map<String, Object> response = networkManager.login(username, password);
                Platform.runLater(() -> {
                    Main.nicknameAtTextBox = (String) response.get("username");
                    screenManager.showMainMenu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Giriş başarısız: " + e.getMessage()));
            }
        }).start();
    }

    public void onRegister(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Lütfen tüm alanları doldurun.");
            return;
        }

        new Thread(() -> {
            try {
                networkManager.register(username, password);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Başarılı");
                    alert.setContentText("Kayıt başarılı! Şimdi giriş yapabilirsiniz.");
                    alert.showAndWait();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Kayıt başarısız: " + e.getMessage()));
            }
        }).start();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
