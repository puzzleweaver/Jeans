

public class Val {

	public double x;
	
	public Val(double x) {
		this.x = x;
	}
	public Val(Val v) {
		x = v.x;
	}
	public Val() {
		
	}
	
	public Val weight(Val v) {
		return new Val(x*v.x);
	}
	
}
