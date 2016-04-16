package type;

public class SliceType extends Type{

	@Override
	public boolean assign(Type t) {
		if (t instanceof SliceType) {
			if (((SliceType) t).elementType == elementType) {
				return true;
			}
		} else if (t instanceof AliasType) { 
			return is(((AliasType) t).type);
		} else if (t instanceof AppendType) {
			if (((AppendType) t).type == elementType) {
				return true;
			}
		}
		return false;
	}
	
	public Type elementType;
	
	@Override
	public String toString() {
		return "[] " + elementType;
	}

	@Override
	//pointer to vector
	public String print() {
		return "std::vector<" + elementType.print() +"> *";
	}
}
