import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Main implements FitnessEvaluator {
	
	public static void main(String[] args) {
		Main fitness = new Main();
		Population pop = new Population(11, 10);
		pop.setFitnessEvaluator(fitness);
		int gens = 500;
		double bests[][] = new double[gens][10];//(int) pop.targetPop];
		for (int i = 0; i < gens; i++) {
			// System.out.println("GEN: " + i);
			pop.advance(1);
			int[] spec = new int[pop.nextRep];
			for (int j = 0; j < pop.genome.size(); j++) {
				spec[pop.genome.get(j).species]++;
			}
			// for(int j = 0; j < pop.nextRep; j++) {
			// System.out.print(spec[j] + " ");
			// }
			// System.out.println();
			double best = 0;
			for(int j = 0; j < bests[0].length; j++) {
				Genome g = pop.genome.get(j);
//				g = new Genome(""+g);
				bests[i][j] = fitness.getFitness(g);
//				best = Math.max(best, fitness.getFitness(g));
			}
//			bests[i] = best;
			System.out.println("GEN" + i + ": " + best);
		}

		double best = 0;
		Genome champ = null;
		for (int j = 0; j < pop.genome.size(); j++) {
			if (best < fitness.getFitness(pop.genome.get(j))) {
				best = fitness.getFitness(pop.genome.get(j));
				champ = pop.genome.get(j);
			}
		}
		
		saveGraph(bests);
		
		System.out.println("CHAMP: " + best);
		
		print(champ);

//		Visualizer.create(champ);
		
	}

	public double getFitness(Genome g) {
		double fit = 10.0;
		g.clear();
		double[] input = new double[11], sorted = new double[10];
		for(int i = 0; i < 10; i++) {
			input[i] = Math.random();
			sorted[i] = input[i];
		}
		input[10] = 1.0;
		Arrays.sort(sorted);
		g.iterate(input, 200);
		double delta = 0.0;
		for(int i = 0; i < 10; i++) {
			delta += Math.abs(g.getOut()[i]-sorted[i]);
		}
		fit -= delta;
		return fit / 10.0;
	}
	
	public static void print(Genome g) {
		g.clear();
		double[] input = new double[11], sorted = new double[10];
		for(int i = 0; i < 10; i++) {
			input[i] = Math.random();
			sorted[i] = input[i];
		}
		input[10] = 1.0;
		Arrays.sort(sorted);
		g.iterate(input, 20);
		for(int i = 0; i < 10; i++) {
			System.out.println(g.getOut()[i] + " : " + sorted[i]);
		}
	}
	
	public static void saveGraph(double[][] best) {
		
		double[] avg = new double[best.length];
		for(int i = 0; i < best.length; i++) {
			for(int j = 0; j < best[0].length; j++) {
				avg[i] += best[i][j];
			}
			avg[i] /= best[0].length;
		}
		
		BufferedImage img = new BufferedImage(best.length, best.length, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, best.length, best.length);
		g.setColor(new Color(255, 255, 255, 128));
		for(int h = 0; h < best[0].length; h++) {
			for(int i = 0; i < best.length-1; i++) {
				g.fillRect(i, (int) ((1.0-best[i][h])*best.length), 1, 1);
			}
		}
		g.setColor(Color.green);
		for(int i = 0; i < avg.length-1; i++) {
			g.drawLine(i, (int) ((1.0-avg[i])*best.length), i+1, (int) ((1.0-avg[i+1])*best.length));
		}
		try {
		    // retrieve image
		    File outputfile = new File("saved.png");
		    ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {}
	}
	
}