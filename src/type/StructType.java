package type;

import java.util.HashMap;

public class StructType extends Type{

//	@Override
//	public boolean assign(Type t) {
//		if (t.is(Type.STRUCT)) {
//			StructType temp = (StructType)t;
//			return temp.attributes.equals(attributes);
//		} else if (t instanceof AliasType) {
//			return assign(((AliasType) t).type);
//		}
//		return false;
//	}

	public HashMap<String, Type> attributes;
	
	@Override
	public String toString() {
		String s = "{";
		for (HashMap.Entry<String, Type> entry : attributes.entrySet()) {
			s += entry.getKey() + " ";
			s += entry.getValue();
			s += ", ";
		}
		s = s.substring(0, s.length() - 2);
		s += "}";
		return s;
	}

	@Override
	//do nothing, leave it to CppGenerator to print function
	public String print() {
		return "";
	}
}
