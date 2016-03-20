package type;

public class BoolType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof BoolType)
			return true;
		
		return false;
	}

	@Override
	public boolean compare(Type t) {
		if (t instanceof BoolType)
			return true;
		
		return false;
	}
	
	@Override
	public String toString() {
		return "bool";
	}
	
	@Override
	public boolean is(Type t) {
		 return t.getClass().isInstance(this.getClass());
	}
}