package type;

public abstract class Type {
	public abstract boolean assign(Type t);
	public abstract boolean compare(Type t);
	public boolean is(Type t) {
		return this.getClass().isInstance(t);
	}
	
	public static IntType INT = new IntType();
	public static FloatType FLOAT64 = new FloatType();
	public static RuneType RUNE = new RuneType();
	public static StringType STRING = new StringType();
	public static BoolType BOOL = new BoolType();
	public static VoidType VOID = new VoidType();
	public static FunctionType FUNC = new FunctionType();
}
