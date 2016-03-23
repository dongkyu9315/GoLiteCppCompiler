package type;

import java.util.HashMap;

public class StructType extends Type{

	@Override
	public boolean assign(Type t) {
		return false;
	}

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
}
