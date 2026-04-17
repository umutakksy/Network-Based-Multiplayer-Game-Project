package grup.proje;

import javafx.scene.image.Image;

public class Bullet extends GameObject {
    private double dirX, dirY;
    private double speed = 4.5;

    public Bullet(Image img, double startX, double startY, double targetX, double targetY) {
        super(img, startX, startY);
        this.sprite.setFitWidth(15); // Mermi küçük olsun
        this.sprite.setFitHeight(15);

        // Normalizasyon (createBullet içindeki mantık)
        double dx = targetX;
        double dy = targetY;
        double length = Math.sqrt(dx * dx + dy * dy);
        this.dirX = dx / length;
        this.dirY = dy / length;
    }

    public void move() {
        this.x += dirX * speed;
        this.y += dirY * speed;
        moveSprite();
    }

    public boolean isOffScreen() {
        return x < -50 || x > 750 || y < -50 || y > 750;
    }
}