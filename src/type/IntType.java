package type;

public class IntType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof IntType) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean compare(Type t) {
		if (t instanceof IntType)
			return true;
		
		return false;
	}
	
	public int value;
	
	@Override
	public String toString() {
		return "int";
	}
}
