package goplusplus;

import java.util.LinkedList;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class Weeder extends DepthFirstAdapter {
	
	/*Checklist
	 * DONE:
	 * 	break & continue
	 * 	ensure one default in switch
	 * 	in assignment check #LHS == #RHS
	 * 	in assignment check LHS is lvalue
	 * 	short variable decl, check LHS is id list
	 * TODO:
	 * 	check all paths in function(that returns a value) body have return statement
	 * 	check operands of op-assign & increment/decrement are lvalue
	 * 	add position/line # info in the exception
	 *  check string or alias of string CANNOT be used for type casting
	 */
	
	
	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
		LinkedList<PAstExp> lvals = node.getIds();
		if(lvals.size() != node.getAstExp().size())
			error("number of operands not match on different sides of the short variable decl");
		for(PAstExp lval : lvals){
			if(!(lval instanceof AIdAstExp)){
				error("LHS of short decl is not id list");
			}
		}
	}
	
	public void caseAAssignAstStm(AAssignAstStm node){
		if(node.getLval().size() != node.getRval().size()){
			error("number of expressions not match on different sides of the assignment");
		}
		
		for(PAstExp l :node.getLval()){
			checkIsLvalue(l);
		}
	}
	
	private void checkIsLvalue(PAstExp l){
		if(l instanceof AParenAstExp){
			checkIsLvalue(((AParenAstExp) l).getAstExp());
		}
		else if(!(l instanceof AIdAstExp || l instanceof AArrayAccessAstExp || l instanceof AFieldAccessAstExp)){
			error("LHS of assignment not a valid lvalue");
		}
	}
	
	public void caseABreakAstStm(ABreakAstStm node){
		Node parent = node.parent();
		while(parent != null){
			if(parent instanceof AForAstStm || parent instanceof AAstSwitchStm){
				return;
			}
		}
		error("break keyword not used inside enclosing for loop or switch");
	}
	
	public void caseAContinueAstStm(AContinueAstStm node){
		Node parent = node.parent();
		while(parent != null){
			if(parent instanceof AForAstStm ){
				return;
			}
			parent = parent.parent();
		}
		error("continue keyword not used inside enclosing for loop");
	}
	
	public void caseASwitchAstStm(ASwitchAstStm node){
		LinkedList<PAstSwitchStm> astSwitchStm = node.getAstSwitchStm();
		boolean hasDefault = false;
		
		for(PAstSwitchStm switchStm : astSwitchStm){
			PAstSwitchCase switchCase = ((AAstSwitchStm) switchStm).getAstSwitchCase();
			if(switchCase instanceof ADefaultAstSwitchCase){
				if(hasDefault)
					error("more than one default in switch statement");
				hasDefault = true;
			}
		}
	}
	
	
	private void error(String msg) {
		throw new WeederException(msg);
	}
}
