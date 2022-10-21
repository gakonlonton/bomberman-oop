package uet.oop.bomberman.entities.enemies;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.controller.collision.CollisionManager;
import uet.oop.bomberman.entities.bomber.Bomb;
import uet.oop.bomberman.entities.bomber.Bomber;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.EntityDestroyable;
import uet.oop.bomberman.graphics.sprite.Sprite;

import static uet.oop.bomberman.controller.GameMaster.bombsList;

public abstract class Enemy extends EntityDestroyable {
    public int spriteIndex = 0;
    public enum EnemyStatus {
        ALIVE,
        DEAD
    }
    EnemyStatus enemyStatus;
    private String dir = "";
    protected CollisionManager collisionManager;
    protected Sprite[] leftSprites = new Sprite[3];
    protected Sprite[] rightSprites = new Sprite[3];

    public Enemy(int x, int y, Image img, CollisionManager collisionManager) {
        super(x, y, img);
        this.collisionManager = collisionManager;
        enemyStatus = EnemyStatus.ALIVE;
        this.speed = 1;
    }

    private boolean goNext = false;

    public void goRandom() {
        if (this instanceof Balloon) {
            leftSprites[0] = Sprite.balloom_left1;
            leftSprites[1] = Sprite.balloom_left2;
            leftSprites[2] = Sprite.balloom_left3;
            rightSprites[0] = Sprite.balloom_right1;
            rightSprites[1] = Sprite.balloom_right2;
            rightSprites[2] = Sprite.balloom_right3;
        }
        if (this instanceof Oneal) {
            leftSprites[0] = Sprite.oneal_left1;
            leftSprites[1] = Sprite.oneal_left2;
            leftSprites[2] = Sprite.oneal_left3;
            rightSprites[0] = Sprite.oneal_right1;
            rightSprites[1] = Sprite.oneal_right2;
            rightSprites[2] = Sprite.oneal_right3;
        }
        spriteIndex++;
        if (!goNext) {
            int rand = (int) (Math.random() * 16);
            switch (rand % 4) {
                case 0:
                    dir = "LEFT";
                    break;
                case 1:
                    dir = "RIGHT";
                    break;
                case 2:
                    dir = "UP";
                    break;
                case 3:
                    dir = "DOWN";
                    break;
                default:
                    break;
            }
        }
        if (collisionManager.touchObstacle(x, y, dir, speed) || touchBomb(x, y, dir)) {
            goNext = false;
            spriteIndex = 0;
        } else {
            if (dir == "LEFT") {
                x -= speed;
                pickSprite(Sprite.movingSprite(
                        leftSprites[0],
                        leftSprites[1],
                        leftSprites[2], spriteIndex, 20).getFxImage());
            }
            if (dir == "RIGHT") {
                x += speed;
                pickSprite(Sprite.movingSprite(
                        rightSprites[0],
                        rightSprites[1],
                        rightSprites[2], spriteIndex, 20).getFxImage());
            }
            if (dir == "UP") {
                y -= speed;
                pickSprite(Sprite.movingSprite(
                        rightSprites[0],
                        rightSprites[1],
                        rightSprites[2], spriteIndex, 20).getFxImage());
            }
            if (dir == "DOWN") {
                y += speed;
                pickSprite(Sprite.movingSprite(
                        leftSprites[0],
                        leftSprites[1],
                        leftSprites[2], spriteIndex, 20).getFxImage());
            }
            goNext = true;
        }
    }

    public abstract void move();

    public EnemyStatus getEnemyStatus() {
        return enemyStatus;
    }

    public boolean touchBomb(int x, int y, String dir) {
        int curX = x;
        int curY = y;
        switch (dir) {
            case "UP":
                curY -= speed;
                break;
            case "DOWN":
                curY += speed;
                break;
            case "LEFT":
                curX -= speed;
                break;
            case "RIGHT":
                curX += speed;
                break;
            default:
                break;
        }
        int xTile = curX / Sprite.SCALED_SIZE;
        int yTile = curY / Sprite.SCALED_SIZE;
        int xWidth = (curX + Bomber.WIDTH) / Sprite.SCALED_SIZE;
        int yHeight = (curY + Bomber.HEIGHT) / Sprite.SCALED_SIZE;
        for (Bomb bomb: collisionManager.getBombList()) {
            int xBomb = (bomb.x + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE;
            int yBomb = (bomb.y + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE;
            if ((xTile == xBomb && yTile == yBomb)
                    || (xWidth == xBomb && yTile == yBomb)
                    || (xTile == xBomb && yHeight == yBomb)
                    || (xWidth == xBomb && yHeight == yBomb)) {
                return true;
            }
        }
        return false;
    }

    public boolean touchBomber(int xTile, int yTile) {
        boolean check = true;
        if (xTile + Bomber.WIDTH < x || xTile > x + Sprite.SCALED_SIZE) check = false;
        if (yTile + Bomber.HEIGHT < y || yTile > y + Sprite.SCALED_SIZE) check = false;
        return check;
    }

    @Override
    public void update() {
        for (Entity i : bombsList) {
            if (((Bomb) i).inRange(x + Bomber.WIDTH / 2, y + Bomber.HEIGHT / 2)
                    && ((Bomb) i).getBombStatus() == Bomb.status.EXPLODED) {
                enemyStatus = EnemyStatus.DEAD;
            }
        }
        if (enemyStatus == EnemyStatus.ALIVE) {
            move();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (enemyStatus == EnemyStatus.ALIVE) {
            super.render(gc);
        }
    }
}