package type;

public class RuneType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t.is(Type.RUNE) || t.is(Type.INT)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "rune";
	}
}
