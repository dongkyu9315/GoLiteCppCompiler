package type;

public class SliceType extends Type{

	@Override
	public boolean assign(Type t) {
		return false;
	}

	@Override
	public boolean compare(Type t) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Type elementType;
	
	@Override
	public String toString() {
		return "[] " + elementType;
	}
}
