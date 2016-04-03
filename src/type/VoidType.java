package type;

public class VoidType extends Type{
	
	@Override
	public boolean assign(Type t) {
		return false;
	}
	
	@Override
	public String toString() {
		return "void";
	}

	@Override
	public String print() {
		return "void";
	}
}
