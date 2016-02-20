public class Main {

	public static double distThreshhold = 3.0;
	public static double POP = 150;

	public static void main(String[] args) {

		Population pop = new Population();
		for(int i = 0; i < 100; i++) {
//			System.out.println("GEN: " + i);
			pop.advance(1);
			int[] spec = new int[pop.nextRep];
			for(int j = 0; j < pop.genome.size(); j++) {
				spec[pop.genome.get(j).species]++;
			}
//			for(int j = 0; j < pop.nextRep; j++) {
//				System.out.print(spec[j] + " ");
//			}
//			System.out.println();
			double best = 0;
			for(int j = 0; j < pop.genome.size(); j++) {
				best = Math.max(best, pop.fitness(pop.genome.get(j)));
			}
			System.out.println(best);
		}
		
		
	}

}
