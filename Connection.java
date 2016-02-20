
public class Connection {

	public static int conGIN = 0;
	
	public int a, b;
	public double weight;
	public boolean enabled;
	public int gin;

	public Connection(int a, int b, double weight, int age) {
		this.a = a;
		this.b = b;
		this.weight = weight;
		enabled = true;
		gin = age;
	}
	
	public Connection(int a, int b, double weight) {
		this.a = a;
		this.b = b;
		this.weight = weight;
		enabled = true;
		gin = conGIN;
		conGIN++;
	}
	
	public Connection get() {
		Connection c = new Connection(a, b, weight, gin);
		c.enabled = enabled;
		return c;
	}

}