package type;

public class StringType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof StringType)
			return true;
		else if (t instanceof AliasType) 
			return assign(((AliasType) t).type);
		return false;
	}
	
	@Override
	public String toString() {
		return "string";
	}
}
