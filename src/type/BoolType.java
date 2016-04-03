package type;

public class BoolType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof BoolType)
			return true;
		else if (t instanceof AliasType) 
			return assign(((AliasType) t).type);
		return false;
	}
	
	@Override
	public String toString() {
		return "bool";
	}

	@Override
	public String print() {
		return "bool";
	}
	
}
