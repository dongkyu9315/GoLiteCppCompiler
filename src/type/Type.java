package type;

public abstract class Type {
	public boolean assign(Type t) {
		return is(t);
	}
	public abstract String print();
	public boolean is(Type t) {
		return t.getClass().equals(this.getClass());
	}
	
	public boolean isBasic(){
		return this.is(FLOAT64) || this.is(INT) || this.is(STRING) || this.is(BOOL)
				|| this.is(RUNE);
	}
	
	public static AliasType ALIAS = new AliasType();
	public static ArrayType ARRAY = new ArrayType();
	public static BoolType BOOL = new BoolType();
	public static FloatType FLOAT64 = new FloatType();
	public static FunctionType FUNC = new FunctionType();
	public static IntType INT = new IntType();
	public static RuneType RUNE = new RuneType();
	public static SliceType SLICE = new SliceType();
	public static StringType STRING = new StringType();
	public static VoidType VOID = new VoidType();
	public static StructType STRUCT = new StructType();
}
