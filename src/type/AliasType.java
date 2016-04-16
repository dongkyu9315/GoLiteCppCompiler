package type;

public class AliasType extends Type {

//	public boolean assign(Type t) {
//		return type.assign(t);
//	}

	public Type type;
	public String alias;
	
	@Override
	public boolean isBasic(){
		if(type.isBasic())
			return true;
		return false;
	}
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
