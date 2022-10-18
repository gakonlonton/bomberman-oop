package uet.oop.bomberman.graphics;

import javafx.scene.canvas.GraphicsContext;
import uet.oop.bomberman.controller.CollisionManager;
import uet.oop.bomberman.controller.GameMaster;
import uet.oop.bomberman.controller.Graph;
import uet.oop.bomberman.controller.Vertices;
import uet.oop.bomberman.entities.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static uet.oop.bomberman.BombermanGame.WINDOW_HEIGHT;
import static uet.oop.bomberman.BombermanGame.WINDOW_WIDTH;

public class Map {
    private int level;
    protected int[][] itemList;
    protected List<List<Entity>> map = new ArrayList<>();
    private Graph graph;

    public Map(int level) {
        this.level = level;
        mapReader();
        convertToGraph();
    }

    /*
        Read map from level files
     */

    public void mapReader() {
        Path mapDir = Paths.get("").toAbsolutePath();
        File file = new File(mapDir.normalize().toString() + "/res/levels/Level" + (level + 1) + ".txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        }
        catch (FileNotFoundException e) {
            System.out.println("Map file not found!");
        }
        sc.nextLine();
        itemList = new int[WINDOW_HEIGHT][WINDOW_WIDTH];
        for (int i = 0; i < WINDOW_HEIGHT; i++) {
            String tempLine = sc.nextLine();
            map.add(new ArrayList<>());
            for (int j = 0; j < WINDOW_WIDTH; j++) {
                switch (tempLine.charAt(j)) {
                    case 'p':
                        map.get(i).add(new Grass(j, i, Sprite.grass.getFxImage()));
                        Entity bomber = new Bomber(j, i, Sprite.player_right.getFxImage(),
                                                    new CollisionManager(this, Bomber.HEIGHT, Bomber.WIDTH));
                        GameMaster.entities.get(level).add(bomber);
                        break;
                    case '#':
                        map.get(i).add(new Wall(j, i, Sprite.wall.getFxImage()));
                        break;
                    case '*':
                        map.get(i).add(new Brick(j, i, Sprite.brick.getFxImage()));
                        break;
                    case '1':
                        map.get(i).add(new Grass(j, i, Sprite.grass.getFxImage()));
                        Enemy balloon = new EnemyBalloon(j, i, Sprite.balloom_left1.getFxImage(),
                                                        new CollisionManager(this, EnemyBalloon.HEIGHT, EnemyBalloon.WIDTH));
                        GameMaster.entities.get(level).add(balloon);
                        break;
                    case '2':
                        map.get(i).add(new Grass(j, i, Sprite.grass.getFxImage()));
                        Enemy oneal = new EnemyOneal(j, i, Sprite.oneal_left1.getFxImage(),
                                                    new CollisionManager(this, EnemyOneal.HEIGHT, EnemyOneal.WIDTH));
                        GameMaster.entities.get(level).add(oneal);
                        break;
                    case 'x':
                        map.get(i).add(new Brick(j, i, Sprite.brick.getFxImage()));
                        itemList[i][j] = ItemPortal.code;
                        break;
                    case 'b':
                        map.get(i).add(new Brick(j, i, Sprite.brick.getFxImage()));
                        itemList[i][j] = ItemBomb.code;
                        break;
                    case 'f':
                        map.get(i).add(new Brick(j, i, Sprite.brick.getFxImage()));
                        itemList[i][j] = ItemFlame.code;
                        break;
                    case 's':
                        map.get(i).add(new Brick(j, i, Sprite.brick.getFxImage()));
                        itemList[i][j] = ItemSpeed.code;
                        break;
                    default:
                        map.get(i).add(new Grass(j, i, Sprite.grass.getFxImage()));
                        break;
                }
            }
        }
        sc.close();
    }

    /*
        Render map on screen
     */

    public void mapRender(GraphicsContext gc) {
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                map.get(i).get(j).render(gc);
            }
        }
    }

    /*
        Convert map to Graph
     */

    public void convertToGraph() {
        List<Vertices> verticesList = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                verticesList.add(new Vertices(j, i));
            }
        }
        graph = new Graph(verticesList);
        for (int i = 0; i < verticesList.size() - 1; i++) {
            for (int j = i + 1; j < verticesList.size(); j++) {
                boolean isAdj = false;
                int x1 = verticesList.get(i).getXTilePos();
                int x2 = verticesList.get(j).getXTilePos();
                int y1 = verticesList.get(i).getYTilePos();
                int y2 = verticesList.get(j).getYTilePos();
                if (map.get(y1).get(x1) instanceof Grass
                        && map.get(y2).get(x2) instanceof Grass) {
                    if (x1 == x2) {
                        if (y1 == y2 + 1) isAdj = true;
                        else if (y1 == y2 - 1) isAdj = true;
                    } else if (y1 == y2) {
                        if (x1 == x2 + 1) isAdj = true;
                        else if (x1 == x2 - 1) isAdj = true;
                    }
                }
                if (isAdj) graph.addAdjVertices(i, j);
            }
        }
    }

    /*
        Getter functions
     */
    public List<List<Entity>> getMap() {
        return map;
    }

    public Graph getGraph() {
        return graph;
    }

    public int getItems(int x, int y) {
        return itemList[y][x];
    }

    public Entity getPosition(int x, int y) {
        int roundedX = Math.round(x / Sprite.SCALED_SIZE);
        int roundedY = Math.round(y / Sprite.SCALED_SIZE);
        return map.get(roundedY).get(roundedX);
    }

    public Bomber getBomber() {
        for (int i = 0; i < GameMaster.entities.get(level).size(); i++) {
            if(GameMaster.entities.get(level).get(i) instanceof Bomber) {
                return (Bomber) GameMaster.entities.get(level).get(i);
            }
        }
        return null;
    }

    public void replace(int x, int y, Entity entity) {
        map.get(y).set(x, entity);
    }

    public void reset() {
        map.clear();
        GameMaster.entities.get(level).clear();
        GameMaster.bombsList.clear();
        mapReader();
    }
}
