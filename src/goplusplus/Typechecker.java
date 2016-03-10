package goplusplus;

import java.util.HashMap;
import java.util.LinkedList;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class Typechecker extends DepthFirstAdapter{
	public Typechecker() {
		symbolTable = new LinkedList<HashMap<String, String> >();
	}
	
	public static void check(Node node) {
		node.apply(new Typechecker());
	}
	
	LinkedList<HashMap<String, String> > symbolTable;
	
	
}
