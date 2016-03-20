package goplusplus;

import java.util.HashMap;
import java.util.Map;

import goplusplus.analysis.ReversedDepthFirstAdapter;
import goplusplus.node.Node;
import goplusplus.node.Token;

public class Position extends ReversedDepthFirstAdapter
{
	  private Map<Node, Integer> lines = new HashMap<Node, Integer>();
	  private Map<Node, Integer> positions = new HashMap<Node, Integer>();

	  public int getLine(Node node) { return lines.get(node); }
	  public int getPos(Node node) { return positions.get(node); }

	  private int line, pos;

	  // called on each token
	  public void defaultCase(Node node) {
	    Token token = (Token) node;
	    line = token.getLine();
	    pos = token.getPos();
	    lines.put(node, line);
	    positions.put(node, pos);
	  }

	  // called for each alternative
	  public void defaultOut(Node node) {
	    // use the line/pos of the last seen token
	    lines.put(node, line);
	    positions.put(node, pos);
	  }
}
