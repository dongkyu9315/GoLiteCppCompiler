package type;

public class AliasType extends Type {

	@Override
	public boolean assign(Type t) {
		return type.assign(t);
	}

	public Type type;
}
