package type;

public class AliasType extends Type {

	@Override
	public boolean assign(Type t) {
		return type.assign(t);
	}

	public Type type;
	
	@Override
	public String toString() {
		return "alias " + type.toString();
	}

	@Override
	public String print() {
		return type.print();
	}
}
