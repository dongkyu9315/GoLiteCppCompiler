package type;

public class AppendType extends Type{

	@Override
	public boolean assign(Type t) {
		return false;
	}

	public Type type;

	@Override
	//do nothing, leave it to CppGenerator to print function
	public String print() {
		return "";
	}
}
