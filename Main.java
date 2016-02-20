public class Main {

	public static double distThreshhold = 3.0;
	public static double POP = 150;

	public static void main(String[] args) {

		Population pop = new Population();
		for(int i = 0; i < 4; i++) {
			System.out.println("GEN: " + i);
			pop.advance(1);
		}
		
		for(int i = 0; i < pop.genome.size(); i++) {
			System.out.println(pop.fitness(pop.genome.get(i)));
		}
		
	}

}
