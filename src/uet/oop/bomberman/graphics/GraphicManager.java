package uet.oop.bomberman.graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import uet.oop.bomberman.entities.Entity;
import java.util.List;

public class GraphicManager {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 18;
    private GraphicsContext gc;

    public GraphicManager(Canvas canvas) {
        gc = canvas.getGraphicsContext2D();
    }

    public void renderMap(Map map) {
        for (int i = 0; i < map.getMap().size(); i++) {
            List<Entity> maps = map.getMap().get(i);
            maps.forEach(g -> g.render(gc));
        }
    }

    public void renderBomber(Map map) {
        map.getEntities().forEach(g -> g.render(gc));
    }

    public void clearScreen(Canvas canvas) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}