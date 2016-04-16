package type;

public class FloatType extends Type{

//	@Override
//	public boolean assign(Type t) {
//		if (t instanceof FloatType)
//			return true;
//		else if (t instanceof IntType)
//			return true;
//		else if(t instanceof RuneType)
//			return true;
//		else if (t instanceof AliasType) 
//			return assign(((AliasType) t).type);
//		
//		return false;
//	}
	
	@Override
	public String toString() {
		return "float64";
	}

	@Override
	public String print() {
		return "double";
	}
}
