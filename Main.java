public class Main {

	public static double distThreshhold = 0.5;
	public static double POP = 150;

	public static void main(String[] args) {

		Population pop = new Population();
		pop.advance(10);
		
		for(int i = 0; i < pop.genome.size(); i++) {
			System.out.println(pop.fitness(pop.genome.get(i)));
		}
		
	}

}
