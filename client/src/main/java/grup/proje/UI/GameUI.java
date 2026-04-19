package grup.proje.UI;

import grup.proje.*;
import grup.proje.Controller.GameController;
import grup.proje.Controller.LobbyController;
import grup.proje.Controller.MainMenuController;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GameUI {
    StackPane root;
    Pane remainsLayer;
    Pane enemyLayer;
    Pane bulletLayer;
    Pane playerLayer;
    Pane wallLayer;
    Pane uiLayer;

    private GameController controller;

    private Text timerText;

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public StackPane createGameScreen(double width, double height) {
        root = new StackPane();
        remainsLayer = new Pane();
        enemyLayer = new Pane();
        bulletLayer = new Pane();
        playerLayer = new Pane();
        wallLayer = new Pane();
        uiLayer = new Pane();
        // ilk eklenen en altta kalır
        root.getChildren().addAll(remainsLayer, enemyLayer, bulletLayer, playerLayer, wallLayer, uiLayer);

        BackgroundSize bgSize = new BackgroundSize(700, 700, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(
                Assets.bgImg,
                BackgroundRepeat.NO_REPEAT, // Tekrar etmesin
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,  // Ortala
                bgSize                      // Belirlediğimiz 700x700 boyutu uygula
        );
        root.setBackground(new Background(bImg));

        CreateWalls(wallLayer);

        timerText = new Text("0");
        timerText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        timerText.setFill(Color.WHITE);

        // Üst ortada durması için bir kapsayıcı (HBox veya StackPane)
        StackPane timerContainer = new StackPane(timerText);
        timerContainer.setPrefWidth(700); // Ekran genişliği
        timerContainer.setLayoutY(70);    // Üstten biraz boşluk
        timerContainer.setAlignment(Pos.CENTER); // İçindeki Text'i her zaman ortalar

        uiLayer.getChildren().add(timerContainer);

        controller.initGame();
        return root;
    }

    public void update(List<Player> players, List<Enemy> enemies, List<Bullet> bullets) {

        // layerları temizle
        playerLayer.getChildren().clear();
        enemyLayer.getChildren().clear();
        bulletLayer.getChildren().clear();

        // ekle
        for (Player p : players) playerLayer.getChildren().add(p.getSprite());
        for (Enemy e : enemies) enemyLayer.getChildren().add(e.getSprite());
        for (Bullet b : bullets) bulletLayer.getChildren().add(b.getSprite());
    }

    public void updateTimer(int seconds) {
        timerText.setText(seconds + "");
    }

    public void showGameOver(int secondsPassed, int score) {
        Rectangle overlay = new Rectangle(700, 700, Color.rgb(0, 0, 0, 0.7));

        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setLayoutX(200);
        gameOverBox.setLayoutY(250);
        gameOverBox.setPrefWidth(300);

        Text title = new Text("OYUN BİTTİ!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        title.setFill(Color.RED);

        Text finalStats = new Text(
                "Hayatta Kalma Süresi: " + secondsPassed + " sn\nToplam Skor: " + score
        );
        finalStats.setFont(Font.font("Arial", 20));
        finalStats.setFill(Color.WHITE);
        finalStats.setTextAlignment(TextAlignment.CENTER);

        Button restartBtn = new Button("Tekrar Dene");
        restartBtn.setOnAction(e -> controller.Restart());

        gameOverBox.getChildren().addAll(title, finalStats, restartBtn);
        uiLayer.getChildren().addAll(overlay, gameOverBox);
    }

    public void CreateRemains(double x, double y, Image remainsImg) {
        ImageView remains = new ImageView(remainsImg);
        remains.setFitWidth(35);
        remains.setFitHeight(35);
        remains.setX(x);
        remains.setY(y);

        remainsLayer.getChildren().add(remains);

        FadeTransition fade = new FadeTransition(Duration.millis(10000), remains);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> remainsLayer.getChildren().remove(remains));
        fade.play();
    }

    public void CreateWalls(Pane root){
        //ÜST
        for(int i = 0; i < 700; i += GameObject.TILE_SIZE){
            ImageView wall = new ImageView(Assets.wallImg);
            wall.setFitWidth(GameObject.TILE_SIZE);
            wall.setFitHeight(GameObject.TILE_SIZE);
            wall.setX(i);
            wall.setY(0);
            root.getChildren().add(wall);
        }

        //ALT
        for(int i = 0; i < 700; i += GameObject.TILE_SIZE){
            ImageView wall = new ImageView(Assets.wallImg);
            wall.setFitWidth(GameObject.TILE_SIZE);
            wall.setFitHeight(GameObject.TILE_SIZE);
            wall.setRotate(180);
            wall.setX(i);
            wall.setY(700-GameObject.TILE_SIZE);
            root.getChildren().add(wall);
        }

        //SOL
        for(int i = 0; i < 700; i += GameObject.TILE_SIZE){
            ImageView wall = new ImageView(Assets.wallImg);
            wall.setFitWidth(GameObject.TILE_SIZE);
            wall.setFitHeight(GameObject.TILE_SIZE);
            wall.setRotate(270);
            wall.setX(0);
            wall.setY(i);
            root.getChildren().add(wall);
        }

        //SAĞ
        for(int i = 0; i < 700; i += GameObject.TILE_SIZE){
            ImageView wall = new ImageView(Assets.wallImg);
            wall.setFitWidth(GameObject.TILE_SIZE);
            wall.setFitHeight(GameObject.TILE_SIZE);
            wall.setRotate(90);
            wall.setX(700-GameObject.TILE_SIZE);
            wall.setY(i);
            root.getChildren().add(wall);
        }
    }
}
