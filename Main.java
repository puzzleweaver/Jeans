
public class Main implements FitnessEvaluator {
	
	public static void main(String[] args) {
		Main fitness = new Main();
		Population pop = new Population();
		pop.setFitnessEvaluator(fitness);
		for (int i = 0; i < 40; i++) {
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
			for(int j = 0; j < pop.genome.size(); j++) {
				Genome g = pop.genome.get(j);
//				g = new Genome(""+g);
				best = Math.max(best, fitness.getFitness(g));
			}
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
		
		System.out.println("CHAMP: " + best);
		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 2; b++) {
				champ.clear();
				champ.iterate(new double[] { a, b, 1.0 }, 5);
				System.out.println(a + "^" + b + " = " + champ.getOut()[0]);
			}
		}

	}

	
	public double getFitness(Genome g) {
		double fit = 4.0;
		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 2; b++) {
				g.clear();
				g.iterate(new double[] { a, b, 1.0 }, 5);
				double delta = Math.abs((((a == 1) ^ (b == 1)) ? 1.0 : 0.0) - (g.getOut()[0]));
				fit -= delta;
			}
		}
		return fit / 4.0;
	}
	
}