package type;

public class StringType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof StringType)
			return true;
		
		return false;
	}

	@Override
	public boolean compare(Type t) {
		if (t instanceof StringType)
			return true;
		
		return false;
	}
	
	@Override
	public String toString() {
		return "string";
	}
}
