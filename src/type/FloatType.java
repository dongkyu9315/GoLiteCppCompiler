package type;

public class FloatType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof FloatType)
			return true;
		else if (t instanceof IntType)
			return true;
		
		return false;
	}
	
	@Override
	public String toString() {
		return "float64";
	}
}
