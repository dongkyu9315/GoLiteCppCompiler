package type;

public class AliasType extends Type {

	@Override
	public boolean assign(Type t) {
		return type.assign(t);
	}

	Type type;
	
	@Override
	public String toString() {
		return "alias " + type.toString();
	}
}
