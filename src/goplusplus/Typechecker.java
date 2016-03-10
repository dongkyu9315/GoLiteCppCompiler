package goplusplus;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class Typechecker extends DepthFirstAdapter{
	public Typechecker() {
		
	}
	
	public static void check(Node node) {
		node.apply(new Typechecker());
	}
}
