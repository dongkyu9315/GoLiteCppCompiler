package type;

public class ArrayType extends Type {

//	@Override
//	public boolean assign(Type t) {
//		if (t.is(Type.ARRAY)) {
//			ArrayType temp = (ArrayType) t;
//			if (temp.size == size && temp.elementType == elementType) {
//				return true;
//			}
//		} else if (t instanceof AliasType) { 
//			return assign(((AliasType) t).type);
//		}
//		
//		return false;
//	}

	public Type elementType;
	public int size;
	
	@Override
	public String toString() {
		return "[" + size + "] " + elementType;
	}

	@Override
	public String print() {
		return "std::array<" + elementType.print() + ", " + size + ">";
	}
}
