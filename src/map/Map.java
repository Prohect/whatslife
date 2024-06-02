package map;

public class Map {

    private double minX = -960;
    private double maxX = 960;
    private double minY = -540;
    private double maxY = 540;

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
