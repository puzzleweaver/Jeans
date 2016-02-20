public class Main {

	public static void main(String[] args) {

		Population pop = new Population();
		for(int i = 0; i < 50; i++) {
			System.out.println("GEN: " + i);
			pop.advance(1);
		}
		
		for(int i = 0; i < pop.genome.size(); i++) {
			System.out.println(pop.fitness(pop.genome.get(i)) + " " + pop.fitness2(pop.genome.get(i)));
		}
		
	}

}
