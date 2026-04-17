package grup.proje;

import javafx.scene.image.Image;

import java.util.*;

public class Enemy extends GameObject {
    private double speed = 1.1; // Kare içindeki ilerleme hızı
    private double targetX, targetY; // Gitmek istediği bir sonraki karenin koordinatı
    private boolean isMoving = false; // Şu an bir kareye doğru ilerliyor mu?

    private int health;
    private Image remainsImg; // Bu düşman ölünce çıkacak görsel
    private boolean isFlashing = false; // Beyazlama efekti aktif mi?

    public Enemy(Image img, Image deadImg, double x, double y, int health) {
        super(img, x, y);
        // Başlangıçta bulunduğu yeri hedef olarak belirle (sabit durması için)
        this.targetX = x;
        this.targetY = y;
        this.remainsImg = deadImg;
        this.health = health;
    }

    public void takeDamage() {
        if (isFlashing) return; // Zaten beyazlamışsa hasar alma (opsiyonel)

        health--;
        if (health > 0) {
            flashWhite();
        }
    }

    private void flashWhite() {
        isFlashing = true;

        // JavaFX ColorAdjust kullanarak sprite'ı bembeyaz yapalım
        javafx.scene.effect.ColorAdjust whiteEffect = new javafx.scene.effect.ColorAdjust();
        whiteEffect.setBrightness(1.0); // Parlaklığı sona çekerek bembeyaz yapar
        sprite.setEffect(whiteEffect);

        // 0.5 saniye (500ms) sonra eski haline döndür
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
        pause.setOnFinished(e -> {
            sprite.setEffect(null); // Efekti kaldır
            isFlashing = false;
        });
        pause.play();
    }

    public int getHealth() { return health; }

    public Image getRemainsImg() {
        return remainsImg;
    }

    public void update(double playerX, double playerY, List<Enemy> allEnemies) {
        if (!isMoving) {
            // 1. Yeni bir kare seçme zamanı
            calculateNextStep(playerX, playerY, allEnemies);
        } else {
            // 2. Hedef kareye doğru ilerle
            moveToTarget();
        }
        moveSprite();
    }

    private void calculateNextStep(double playerX, double playerY, List<Enemy> allEnemies) {
        double nextX = this.x;
        double nextY = this.y;

        // Mesafeleri ölç
        double diffX = playerX - this.x;
        double diffY = playerY - this.y;

        // Hangi eksende daha uzaktaysa o yöndeki bir sonraki kareyi hedefle
        if (Math.abs(diffX) > Math.abs(diffY)) {
            nextX += (diffX > 0) ? TILE_SIZE : -TILE_SIZE;
        } else {
            nextY += (diffY > 0) ? TILE_SIZE : -TILE_SIZE;
        }

        // ÇAKIŞMA KONTROLÜ: Gitmek istediğim karede başka bir düşman var mı?
        if (!isTileOccupied(nextX, nextY, allEnemies)) {
            this.targetX = nextX;
            this.targetY = nextY;
            isMoving = true;
        }
        // Eğer kare doluysa, düşman o kare boşalana kadar "bekler" (Retro hissi)
    }

    private void moveToTarget() {
        if (this.x < targetX) this.x += speed;
        else if (this.x > targetX) this.x -= speed;

        if (this.y < targetY) this.y += speed;
        else if (this.y > targetY) this.y -= speed;

        // Hedefe vardık mı? (Küçük bir tolerans payı ile)
        if (Math.abs(this.x - targetX) < speed && Math.abs(this.y - targetY) < speed) {
            this.x = targetX;
            this.y = targetY;
            isMoving = false;
        }
    }

    private boolean isTileOccupied(double tx, double ty, List<Enemy> allEnemies) {
        for (Enemy other : allEnemies) {
            if (other == this) continue;
            // Diğer düşmanın mevcut konumu VEYA hedef konumu burası mı?
            if ((other.x == tx && other.y == ty) || (other.targetX == tx && other.targetY == ty)) {
                return true;
            }
        }
        return false;
    }
}