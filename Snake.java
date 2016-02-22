import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Snake {
	
	private ArrayList<Cell> cells;
	private Food food;
	
	private int width, height;
	
	private int dx = -1, dy = 0;
	
	private int counter = 0;
	private int death;
	
	public Snake(int w, int h) {
		width = w;
		height = h;
		cells = new ArrayList<>();
		cells.add(new Cell(w/2, h/2));
		food = new Food();
		death = (width + height);
	}
	
	public void addCell() {
		Cell lastCell = cells.get(cells.size()-1);
		cells.add(new Cell(lastCell.x, lastCell.y));
	}
	
	public void render(Graphics g, int scale) {
		//background
		g.setColor(Color.black);
		g.fillRect(0, 0, width*scale, height*scale);
		//snake
		g.setColor(Color.green);
		for(int i = 0; i < cells.size(); i++)
			g.fillRect(cells.get(i).x*scale, cells.get(i).y*scale, scale, scale);
		//food
		g.setColor(Color.blue);
		g.fillRect(food.x*scale, food.y*scale, scale, scale);
	}
	
	public boolean update(boolean left, boolean right) {
		counter++;
		if(counter > death)
			return false;
		//change direction
		if(left) {
			int temp = dx;
			dx = dy;
			dy = -temp;
		}
		if(right) {
			int temp = dx;
			dx = -dy;
			dy = temp;
		}
		//move snake
		for(int i = cells.size()-1; i > 0; i--) {
			cells.get(i).x = cells.get(i-1).x;
			cells.get(i).y = cells.get(i-1).y;
		}
		cells.get(0).x += dx;
		cells.get(0).y += dy;
		//check collisions
		for(int i = 1; i < cells.size()-1; i++)
			if(cells.get(0).x == cells.get(i).x && cells.get(0).y == cells.get(i).y)
				return false;
		//border collisions
		if(cells.get(0).x < 0 || cells.get(0).x >= width ||
				cells.get(0).y < 0 || cells.get(0).y >= height)
			return false;
		//food collisions
		if(cells.get(0).x == food.x && cells.get(0).y == food.y) {
			addCell();
			food.spawn();
			counter = 0;
		}
		return true;
	}
	
	public double[] getInput() {
		double[] input = new double[5];
		//head
		input[0] = 1.0*cells.get(0).x / width;
		input[1] = 1.0*cells.get(0).y / height;
		//food
		input[2] = 1.0*food.x / width;
		input[3] = 1.0*food.y / height;
		//bias
		input[4] = 1.0;
		return input;
	}
	
	public double getFood() {
		int difX = cells.get(0).x - food.x;
		int difY = cells.get(0).y - food.y;
		int dist2 = difX*difX + difY*difY;
		double diagonal = width*width + height*height;
		return cells.size() + dist2/diagonal;
	}
	
	private class Cell {
		
		public int x, y;
		
		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	private class Food {
		
		public int x, y;
		
		public Food() {
			spawn();
		}
		
		public void spawn() {
			x = (int) (Math.random()*width);
			y = (int) (Math.random()*height);
		}
		
	}
	
}
