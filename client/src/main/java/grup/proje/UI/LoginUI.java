package grup.proje.UI;

import grup.proje.Assets;
import grup.proje.Controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginUI {

    private LoginController controller;
    private StackPane root;

    public void setController(LoginController controller) {
        this.controller = controller;
    }

    public StackPane createLoginScreen(double width, double height) {
        root = new StackPane();
        root.setPrefSize(width, height);

        // Arka plan (LobbyBg kullanabiliriz veya siyah gradyan)
        root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, #1a2a3a, #050a0f);");

        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(400);
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 20; -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-radius: 20;");

        // Başlık
        Label title = new Label("NEXUS PROTOCOL");
        title.setFont(Font.font("Orbitron", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#00d2ff"));
        title.setStyle("-fx-effect: dropshadow(gaussian, #00d2ff, 15, 0.5, 0, 0);");

        VBox fields = new VBox(15);
        fields.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı Adı");
        styleTextField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifre");
        styleTextField(passwordField);

        fields.getChildren().addAll(usernameField, passwordField);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button loginBtn = new Button("Giriş Yap");
        styleButton(loginBtn, "#3498db");
        loginBtn.setOnAction(e -> controller.onLogin(usernameField.getText(), passwordField.getText()));

        Button registerBtn = new Button("Kayıt Ol");
        styleButton(registerBtn, "#2ecc71");
        registerBtn.setOnAction(e -> controller.onRegister(usernameField.getText(), passwordField.getText()));

        buttons.getChildren().addAll(loginBtn, registerBtn);

        container.getChildren().addAll(title, fields, buttons);
        root.getChildren().add(container);

        return root;
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: gray; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10; " +
                "-fx-border-color: rgba(255,255,255,0.2); " +
                "-fx-border-radius: 10;");
    }

    private void styleButton(Button btn, String color) {
        btn.setPrefWidth(120);
        btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 10;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10;"));
    }
}
