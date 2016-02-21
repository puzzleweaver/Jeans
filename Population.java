import java.util.ArrayList;
import java.util.Random;

public class Population {

	public static Random r = new Random();

	public double targetPop = 50, distThreshold = 3.0;
	public ArrayList<Genome> genome = new ArrayList<Genome>();
	public ArrayList<Genome> rep = new ArrayList<Genome>();
	public int nextRep;
	
	//distance constants
	public double c1 = 1.0, c2 = 1.0, c3 = 0.8;
	
	private FitnessEvaluator fitness;

	public Population(int ins, int outs) {
		// create simple initial network manually
		genome.add(new Genome(ins, outs));
		for(int i = 0; i < outs; i++) {
			int rid = r.nextInt(ins);
			genome.get(0).cons.add(new Connection(rid, ins+i, Math.random()*2-1));
			genome.get(0).nodes.get(rid).cons.add(genome.get(0).cons.size()-1);
		}
	}

	public void setFitnessEvaluator(FitnessEvaluator f) {
		fitness = f;
	}
	
	public void advance(int gens) {
		for(int n = 0; n < gens; n++) {
			
			double fits[] = new double[genome.size()];
			for(int i = 0; i < fits.length; i++) {
				fits[i] = fitness.getFitness(genome.get(i));
			}
			
			// reset species value for each genome or create new species when necessary
			double dist, min = Double.MAX_VALUE;
			for(int i = 0; i < genome.size(); i++) {
				for(int j = 0; j < rep.size(); j++) {
					dist = dist(genome.get(i), rep.get(j));
					if(dist < min) {
						min = dist;
						genome.get(i).species = rep.get(j).species;
					}
				}
				if(min >= distThreshold) {
					// add new species
					rep.add(genome.get(i));
					genome.get(i).species = nextRep;
					nextRep++;
				}
			}
			
			// calculate number of offspring for each species
			double offspring[] = new double[nextRep], total = 0.00001, delta;
			for(int i = 0; i < genome.size(); i++) {
				delta = fits[i]/share(genome.get(i));
				offspring[genome.get(i).species] += delta;
				total += delta;
			}
			for(int i = 0; i < offspring.length; i++) {
				offspring[i] *= targetPop/total;
			}
	
			// initialize arrays to store all genomes by species
			ArrayList<ArrayList<Integer>> species = new ArrayList<>();
			for(int i = 0; i < nextRep; i++)
				species.add(new ArrayList<Integer>());
			for(int i = 0; i < genome.size(); i++)
				species.get(genome.get(i).species).add(i);
	
			// kill off the bottom half of each species
			for(int i = 0; i < species.size(); i++) {
				double fit = 0;
				for(int j = 0; j < species.get(i).size(); j++) {
					fit += fits[species.get(i).get(j)];
				}
				fit /= species.get(i).size();
				for(int j = 0; j < species.get(i).size(); j++) {
					if(fits[species.get(i).get(j)] < fit) {
						species.get(i).remove(j);
						j--;
					}
				}
			}
	
			// create next generation
			ArrayList<Genome> nextGen = new ArrayList<>();
			int count;
			
			// copy champions
			for(int i = 0; i < species.size(); i++) {
				double best = 0, val;
				int beast = 0;
				for(int j = 0; j < species.get(i).size(); j++) {
					val = fits[species.get(i).get(j)];
					if(val > best) {
						best = val;
						beast = species.get(i).get(j);
					}
				}
				nextGen.add(genome.get(beast));
				//fill in the rest with offspring
				count = 0;
				loop : while(count < offspring[i] - 1) {
					if(Math.random() < 0.25) {
						// asexual
						int id = r.nextInt(species.get(i).size());
						nextGen.add(new Genome(genome.get(species.get(i).get(id))));
						count++;
//					}else {
//						// sexual
//						int id1 = r.nextInt(species.get(i).size()),
//								id2 = r.nextInt(species.get(i).size());
//						if(id1 == id2)
//							continue loop;
//						nextGen.add(new Genome(genome.get(species.get(i).get(id1)), genome.get(species.get(i).get(id2))));
//						count++;
					}
				}
			}
			
			// replace old generation with the new one
			genome = nextGen;
		}
	}

	public double dist(Genome a, Genome b) {

		// calculate the distance between two nodes for speciation purposes

		boolean[] aHas = new boolean[Connection.conGIN], bHas = new boolean[Connection.conGIN];
		for(int i = 0; i < a.cons.size(); i++)
			aHas[a.cons.get(i).gin] = true;
		for(int i = 0; i < b.cons.size(); i++)
			bHas[b.cons.get(i).gin] = true;
		int E = 0, D = 0;
		int lastA = 0, lastB = 0;
		for(int i = 0; i < a.cons.size(); i++) {
			if(aHas[i] ^ bHas[i])
				D++;
			if(aHas[i])
				lastA = i;
			if(bHas[i])
				lastB = i;
		}
		if(lastA > lastB) {
			for(int i = lastB+1; i < aHas.length; i++) {
				if(aHas[i]) {
					E++;
					D--;
				}
			}
		}else if(lastB > lastA) {
			for(int i = lastA+1; i < bHas.length; i++) {
				if(bHas[i]) {
					E++;
					D--;
				}
			}
		}
		double W = 0;
		int num = 0;
		for(int i = 0; i < a.cons.size(); i++) {
			if(bHas[a.cons.get(i).gin]) {
				for(int j = 0; j < b.cons.size(); j++) {
					if(a.cons.get(i).gin == b.cons.get(j).gin) {
						W += Math.abs(a.cons.get(i).weight-b.cons.get(j).weight);
						num++;
					}
				}
			}
		}
		W /= num;

		double N = Math.max(a.cons.size(), b.cons.size());
		return c1*D/N + c2*E/N + c3*W;

	}
	
	public double share(Genome g) {
		//won't be dividing by zero because a genome is always close enough to itself
		double sum = 0;
		for(int i = 0; i < genome.size(); i++) {
			double dist = dist(genome.get(i), g);
			if(dist < distThreshold) {
				sum++;
			}
		}
		return sum;
	}
	
}
