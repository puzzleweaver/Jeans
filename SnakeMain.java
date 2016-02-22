import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class SnakeMain extends BasicGame implements FitnessEvaluator {
	
	private static int width = 15, height = 15, scale = 40;
	
	private Genome genome;
	private Snake mainGame;
	
//	private Direction dir = Direction.LEFT;
	
	private int counter = 0;
	
	public SnakeMain(String title) {
		super(title);
	}
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new SnakeMain("Snake AI"));
		app.setDisplayMode(width*scale, height*scale, false);
		app.setVSync(true);
		app.setShowFPS(false);
		app.setAlwaysRender(true);
		app.setMinimumLogicUpdateInterval(15);
		app.start();
	}
	
	public void init(GameContainer gc) throws SlickException {
		//evolve all the things!!!
		Population pop = new Population(5, 2);
		pop.setFitnessEvaluator(this);
		pop.targetPop = 150;
		pop.distThreshold = 3.0;
		for(int i = 0; i < 1000; i++) {
			System.out.println("GENERASHIN " + i);
			pop.advance(1);
		}
		genome = pop.genome.get((int) (Math.random()*pop.genome.size()));
		genome.clear();
		mainGame = new Snake(width, height);
		System.out.println(genome);
	}
	public void render(GameContainer gc, Graphics g) throws SlickException {
		mainGame.render(g, scale);
	}
	public void update(GameContainer gc, int delta) throws SlickException {
//		counter += delta;
//		Input input = gc.getInput();
//		
//		if(counter > 100) {
//			if(input.isKeyPressed(Input.KEY_A) && dir != Direction.RIGHT) dir = Direction.LEFT;
//			else if(input.isKeyPressed(Input.KEY_S) && dir != Direction.UP) dir = Direction.DOWN;
//			else if(input.isKeyPressed(Input.KEY_D) && dir != Direction.LEFT) dir = Direction.RIGHT;
//			else if(input.isKeyPressed(Input.KEY_W) && dir != Direction.DOWN) dir = Direction.UP;
//			counter = 0;
//			if(!snakeGame.update(dir.dx, dir.dy))
//				snakeGame = new Snake(width, height);
//		}
		
		counter += delta;
		if(counter > 100) {
			counter = 0;
			genome.iterate(mainGame.getInput(), 1);
			double[] out = genome.getOut();
			if(!mainGame.update(out[0] > 0.5, out[1] > 0.5)) {
				genome.clear();
				mainGame = new Snake(width, height);
			}
		}
		
		if(gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			genome.clear();
			mainGame = new Snake(width, height);
		}
		
//		counter += delta;
//		Input input = gc.getInput();
//		if(counter > 100) {
//			counter = 0;
//			boolean left = input.isKeyPressed(Input.KEY_LEFT);
//			boolean right = input.isKeyPressed(Input.KEY_RIGHT);
//			if(!mainGame.update(left, right)) {
//				mainGame = new Snake(width, height);
//			}
//		}
	}
	
	public double getFitness(Genome g) {
		//averages N cases to get more accurate results
		int N = 10;
		int avg = 0;
		for(int i = 0; i < N; i++)
			avg += getOneFitness(g)/N;
		return avg;
	}
	
	public double getOneFitness(Genome g) {
		Snake snakeGame = new Snake(width, height);
		double time = 0;
		g.clear();
		double[] out;
		do {
			time++;
			g.iterate(snakeGame.getInput(), 1);
			out = g.getOut();
		}while(snakeGame.update(out[0] > 0.5, out[1] > 0.5) && time < 500);
		//food * time = N optimally
		//clamp the percent error
		double N = 1;
		double food = snakeGame.getFood();
//		double error = (food * time - N) / N;
////		double points = time/1000;
//		double clampedError = Math.exp(-error * error);
		return 1 - Math.exp(-food);
	}
	
}
