package type;

import java.util.ArrayList;
import java.util.Iterator;

public class FunctionType extends Type{

	@Override
	public boolean assign(Type t) {
		return false;
	}
	
	public ArrayList<Type> paramType;
	public Type returnType;	
	
	@Override
	public String toString() {
		String s = "(";
		for (Iterator<Type> iterator = paramType.iterator(); iterator.hasNext();) {
			Type type = (Type) iterator.next();
			s += type;
			if (iterator.hasNext())
				s += ", ";
		}
		s += ") : " + returnType;
		return s;
	}
}
