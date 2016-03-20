package type;

import java.util.ArrayList;
import java.util.Iterator;

public class StructType extends Type{

	@Override
	public boolean assign(Type t) {
		return false;
	}

	@Override
	public boolean compare(Type t) {
		return false;
	}

	public ArrayList<Type> elementTypes;
	
	@Override
	public String toString() {
		String s = "{";
		for (Iterator iterator = elementTypes.iterator(); iterator.hasNext();) {
			Type type = (Type) iterator.next();
			s += type;
			if (iterator.hasNext())
				s += ", ";
		}
		s += "}";
		return s;
	}
}
