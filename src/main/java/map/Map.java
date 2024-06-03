package map;

public class Map {

    private double minX = -900;
    private double maxX = 900;
    private double minY = -500;
    private double maxY = 500;

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    private Point[][] map;

    public Point[][] getMap() {
        return map;
    }

    public Map(Point[][] map) {
        this.map = map;
    }

    public Map() {
    }
}
