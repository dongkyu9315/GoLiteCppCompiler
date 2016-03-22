package type;

public class ArrayType extends Type {

	@Override
	public boolean assign(Type t) {
		return false;
	}

	public Type elementType;
	public int size;
	
	@Override
	public String toString() {
		return "[" + size + "] " + elementType;
	}
}
