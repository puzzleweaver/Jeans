public class Main {

	public static void main(String[] args) {

		Population pop = new Population();
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
				best = Math.max(best, pop.fitness(g));
			}
			System.out.println("GEN" + i + ": " + best);
		}

		double best = 0;
		Genome champ = null;
		for (int j = 0; j < pop.genome.size(); j++) {
			if (best < pop.fitness(pop.genome.get(j))) {
				best = pop.fitness(pop.genome.get(j));
				champ = pop.genome.get(j);
			}
		}
		
		System.out.println("CHAMP: " + best);
		champ.clear();
		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 2; b++) {
				champ.iterate(new double[] { a, b, 1.0 }, 5);
				System.out.println(a + "^" + b + " = " + champ.getOut()[0]);
			}
		}

	}

}