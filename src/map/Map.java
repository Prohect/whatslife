package map;

import java.util.HashMap;

public class Map {

    private final Point[][] map;

    public Point[][] getMap() {
        return map;
    }

    public Map(Point[][] map) {
        this.map = map;
    }
}
