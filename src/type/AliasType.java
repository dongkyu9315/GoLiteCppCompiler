package type;

public class AliasType extends Type {

	@Override
	public boolean assign(Type t) {
		return type.assign(t);
	}

	@Override
	public boolean compare(Type t) {
		return type.compare(t);
	}

	Type type;
}
