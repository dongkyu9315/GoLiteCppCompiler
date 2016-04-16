package type;

public class IntType extends Type{

//	@Override
//	public boolean assign(Type t) {
//		if (t instanceof IntType)
//			return true;
//		else if(t instanceof RuneType)
//			return true;
//		else if (t instanceof AliasType) 
//			return assign(((AliasType) t).type);
//		
//		return false;
//	}
	
	public int value;
	
	@Override
	public String toString() {
		return "int";
	}

	@Override
	public String print() {
		return "int";
	}

}
