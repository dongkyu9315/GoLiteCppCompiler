package type;

public class RuneType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t.is(Type.RUNE) || t.is(Type.INT))
			return true;
		else if (t instanceof AliasType) 
			return assign(((AliasType) t).type);
		return false;
	}

	@Override
	public String toString() {
		return "rune";
	}

	@Override
	public String print() {
		return "char";
	}
}
