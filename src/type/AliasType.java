package type;

public class AliasType extends Type {

	@Override
	public boolean assign(Type t) {
		return type.assign(t);
	}

	public Type type;
	public String alias;
	
	@Override
	public String toString() {
		return "alias " + type.toString();
	}

	@Override
	public String print() {
		if(type.is(Type.STRUCT)){
			return alias;
		}
		else{
			return type.print();
		}
	}
}
