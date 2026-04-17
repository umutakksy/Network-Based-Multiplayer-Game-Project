package grup.proje.Controller;

import grup.proje.*;
import grup.proje.UI.GameUI;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.*;

public class GameController {
    private ScreenManager screenManager;
    private NetworkManager networkManager;
    private GameUI gameUI;

    private Set<KeyCode> activeKeys = new HashSet<>(); // Basılı tutulan tuşları saklamak için

    private boolean isGameStarted = false;
    private AnimationTimer gameLoop;

    private Player localPlayer;
    private List<Player> players = new ArrayList<>();

    private List<Bullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private final long shotCooldown = 500_000_000;

    private List<Enemy> enemies = new ArrayList<>();
    private long lastEnemySpawnTime = 0;
    private long enemySpawnInterval = 2_000_000_000L;

    private int score = 0;
    private int secondsPassed = -1;
    private long lastTimerUpdate = 0; // Zamanlayıcıyı her saniye güncellemek için

    public GameController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setUI(GameUI gameUI){
        this.gameUI = gameUI;
    }

    public void initGame() {
        players.clear();
        enemies.clear();
        bullets.clear();

        // 🎮 Player oluştur
        localPlayer = new Player(Assets.playerImg, 332.5, 332.5, Main.nicknameAtTextBox);
        players.add(localPlayer);

        // reset değerler
        score = 0;
        secondsPassed = -1;
        lastShotTime = 0;
        lastEnemySpawnTime = 0;

        isGameStarted = true;

        startGameLoop();
    }

    public void setupControls(Scene scene) {
        scene.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
    }

    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isGameStarted) return;

                // 🔥 TIMER ARTIRMA
                if (now - lastTimerUpdate >= 1_000_000_000L) {
                    secondsPassed++;
                    lastTimerUpdate = now;
                }

                // 1. Mantık
                localPlayer.update(activeKeys);
                handleShooting(now);
                updateBullets();
                spawnEnemiesIfNeeded(now);
                updateEnemies();
                checkCollisions();

                // 2. UI’ya sadece “render et” de
                gameUI.update(players, enemies, bullets);
                gameUI.updateTimer(secondsPassed);
            }
        };
        gameLoop.start();
    }

    public void Restart(){
        screenManager.showGame();
    }

    private void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.move();

            if (b.isOffScreen()) {
                bullets.remove(i);
                i--; // 🔥 çok önemli
            }
        }
    }

    private void updateEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.update(localPlayer.getX(), localPlayer.getY(), enemies);
        }
    }

    private void spawnEnemiesIfNeeded(long now) {
        if (now - lastEnemySpawnTime >= enemySpawnInterval) {
            for (int i = 0; i<=secondsPassed/10; i++){
                spawnEnemy();
                lastEnemySpawnTime = now;
            }
        }
    }

    public void spawnEnemy() {
        Random random = new Random();
        int randomHealth = (random.nextBoolean()) ? 2 : 1;
        Image img = (randomHealth == 2) ? Assets.enemySprites.get(0) : Assets.enemySprites.get(1);
        Image remainsImg = (randomHealth == 2) ? Assets.remainsSprites.get(0) : Assets.remainsSprites.get(1);

        Enemy e;
        int kenar = random.nextInt(4);
        if (kenar == 0) {
            e = new Enemy(img, remainsImg, random.nextInt(20)*35, -35, randomHealth);
        } else if (kenar == 1) {
            e = new Enemy(img, remainsImg, random.nextInt(20)*35, 700, randomHealth);
        } else if (kenar == 2) {
            e = new Enemy(img, remainsImg, -35, random.nextInt(20)*35, randomHealth);
        } else {
            e = new Enemy(img, remainsImg, 700, random.nextInt(20)*35, randomHealth);
        }
        enemies.add(e);
    }

    private void checkCollisions() {

        //  Bullet → Enemy çarpışma
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);

            for (int j = 0; j < bullets.size(); j++) {
                Bullet b = bullets.get(j);

                if (b.getSprite().getBoundsInParent().intersects(e.getSprite().getBoundsInParent())) {

                    // mermiyi sil
                    bullets.remove(j);
                    j--;

                    // düşmana hasar ver
                    e.takeDamage();

                    // öldü mü?
                    if (e.getHealth() <= 0) {
                        score += (e.getHealth() == 2) ? 20 : 10; // Tank düşman 20, normal 10 puan

                        // remains oluştur (UI üzerinden)
                        gameUI.CreateRemains(e.getX(), e.getY(), e.getRemainsImg());

                        enemies.remove(i);
                        i--;

                        break; // if in hemen dışına atılabilir
                    }
                }
            }
        }

        // Enemy → Player çarpışma (Game Over)
        for (Enemy e : enemies) {
            if (e.getSprite().getBoundsInParent().intersects(localPlayer.getSprite().getBoundsInParent())) {
                stopGame();     // loop durdur
                gameUI.showGameOver(secondsPassed, score); // ekran değiştir
                break;
            }
        }
    }

    public void stopGame() {
        isGameStarted = false;

        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void handleShooting(long now) {
        double fx = 0, fy = 0;
        if (activeKeys.contains(KeyCode.UP)) fy -= 1;
        if (activeKeys.contains(KeyCode.DOWN)) fy += 1;
        if (activeKeys.contains(KeyCode.LEFT)) fx -= 1;
        if (activeKeys.contains(KeyCode.RIGHT)) fx += 1;


        if ((fx != 0 || fy != 0) && (now - lastShotTime >= shotCooldown)) {
            // Yeni mermi nesnesi oluştur
            Bullet b = new Bullet(
                    Assets.bulletImg,
                    localPlayer.getX() + localPlayer.sprite.getFitWidth()/3,
                    localPlayer.getY() + localPlayer.sprite.getFitHeight()/3,
                    fx, fy
            );
            bullets.add(b);
            //bulletLayer.getChildren().add(b.getSprite()); GameUI da update içinde yapılacak diğer hepsiyle beraber
            lastShotTime = now;
        }
    }
}
