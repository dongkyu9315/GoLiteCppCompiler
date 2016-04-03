package type;

import goplusplus.node.TId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FunctionType extends Type{

	@Override
	public boolean assign(Type t) {
		return false;
	}
	
	public ArrayList<Type> paramType;
	public Type returnType;	
	public String name;
	public List<TId> paramId;
	
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

	@Override
	//do nothing, leave it to CppGenerator to print function
	public String print() {
		return "";
	}
}
