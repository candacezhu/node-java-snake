/* Snake game implementation using node.js and Java
   Copyright 2012 Candace Zhu */

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public final class SnakePanel extends JPanel{
	private final static int SIZE = 600;
	private final static int ROWS = 50;
	private SnakeModel model;

	public SnakePanel(SnakeModel model) {
		this.model = model;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int width = SIZE / ROWS;
		// paint cells
		g.setColor(Color.lightGray);
		for(int i = 0; i < ROWS; i++) {
			g.drawLine(i * width, 0, i * width, 600);
			g.drawLine(0, i * width, 600, i * width);
		}
		
		// paint walls 
		g.setColor(Color.gray);
		List<Point> wallDots = model.getWallDots();
		for(Point wallDot : wallDots) {
			paintDots(wallDot, g);
		}
		
		// paint apples
		g.setColor(Color.red);
		List<Point> appleDots = model.getAppleDots();
		for(Point appleDot : appleDots) {
			paintDots(appleDot, g);
		}
		
		// paint snakes
		List<List<Point>> twoSnakeDots = model.getTwoSnakesDots();
		for(int i = 0; i < twoSnakeDots.size(); i++) {
			Color snakeColor;
			if(i == 0) {
				snakeColor = Color.blue;
			} else {
				snakeColor = Color.orange;
			}
			
			g.setColor(snakeColor);
			for(Point snakeDot : twoSnakeDots.get(i)) {
				paintDots(snakeDot, g);
			}
		}
 	}
	
	private void paintDots(Point p, Graphics g) {
		int cellSize = SIZE / ROWS; 
		g.fillRect(p.getX() * cellSize, p.getY() * cellSize, cellSize, cellSize);
	}
}
