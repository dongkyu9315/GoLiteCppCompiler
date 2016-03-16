package type;

public class RuneType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof RuneType)
			return true;
		
		return false;
	}

	@Override
	public boolean compare(Type t) {
		if (t instanceof RuneType)
			return true;
		
		return false;
	}

	@Override
	public String toString() {
		return "rune";
	}
}
