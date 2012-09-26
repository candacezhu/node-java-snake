/* Snake game implementation using node.js and Java
   Copyright 2012 Candace Zhu */

import java.util.ArrayList;
import java.util.List;

public final class SnakeModel {
    private List<List<Point>> twoSnakesDots = new ArrayList<List<Point>>();
    private List<Point> appleDots = new ArrayList<Point>();
    private List<Point> wallDots = new ArrayList<Point>();
    
    public SnakeModel() {
        for(int i = 0; i < 2; i++) {
            List<Point> snake = new ArrayList<Point>();
            twoSnakesDots.add(snake);
        }
    }
    
    public void setSnakeOneDots(List<Point> snakeOneDots) {
        twoSnakesDots.set(0, snakeOneDots);
    }
    
    public void setSnakeTwoDots(List<Point> snakeTwoDots) {
        twoSnakesDots.set(1, snakeTwoDots);
    }
    
    public void setAppleDots(List<Point> appleDots) {
        this.appleDots = appleDots;
    }
    
    public void setWallDots(List<Point> wallDots) {
        this.wallDots = wallDots;
    }
    
    public List<List<Point>> getTwoSnakesDots() {
        return twoSnakesDots;
    }
    
    public List<Point> getAppleDots() {
        return appleDots;
    }
    
    public List<Point> getWallDots() {
        return wallDots;
    }
}
