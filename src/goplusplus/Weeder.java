package goplusplus;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class Weeder extends DepthFirstAdapter {
	private Position pos;
	private HashSet<String> aliasOfStringType = new HashSet<>();
	
	public Weeder(Position p){
		pos = p;
	}
	/*Checklist
	 * DONE:
	 * 	break & continue
	 * 	ensure one default in switch
	 * 	in assignment check #LHS == #RHS
	 * 	in assignment check LHS is lvalue
	 * 	short variable decl, check LHS is id list
	 * 	add position/line # info in the exception
	 * 	check operands of increment/decrement are lvalue
	 *  check string CANNOT be used for basic type casting
	 *  check all paths in function(that returns a value) body have return statement
	 *  check post statement of for loop must not be short var decl
	 *  check alias of string CANNOT be used for type casting
	 *  
	 */
	
	public void inAFuncCallAstExp(AFuncCallAstExp node){
		PAstExp funcName = node.getName();
		if(funcName instanceof AIdAstExp){
			String name = ((AIdAstExp) funcName).getId().getText();
			if(this.aliasOfStringType.contains(name))
				error("alias of string cannot be used for type casting", node);
		}
	}
	
	public void inAAstTypeDecl(AAstTypeDecl node){
		PAstTypeExp typeExp = node.getAstTypeExp();
		// store all alias of string for later lookup
		if(typeExp instanceof ABasicAstTypeExp){
			if(((ABasicAstTypeExp) typeExp).getBasicTypes().getText().toLowerCase().equals("string"))
				for(TId id : node.getId())
					this.aliasOfStringType.add(id.getText());
		}
		else if(typeExp instanceof AAliasAstTypeExp){
			String alias = ((AAliasAstTypeExp) typeExp).getId().getText();
			if(this.aliasOfStringType.contains(alias)){
				for(TId id : node.getId())
					this.aliasOfStringType.add(id.getText());
			}
		}
	}
	
	
	
	public void inAForAstStm(AForAstStm node) {
		PAstStm post = node.getPost();
		if(post instanceof AShortDeclAstStm){
			error("post statement of for loop must not be short var decl", node);
		}
	}
	
	
	public void inAAstFuncDecl(AAstFuncDecl node){
		if(node.getAstTypeExp()!=null){
			LinkedList<PAstStm> functionBody = node.getAstStm();
			if(!checkReturn(functionBody))
				error("missing return statement in function body",node);
		}
		
	}
	
	private boolean checkReturn(List<PAstStm> stms){
		for(PAstStm stm : stms){
			if(stm instanceof AReturnAstStm)
				return true;
			if(stm instanceof ALongifAstStm){
				LinkedList<PAstStm> ifStms = ((ALongifAstStm) stm).getIfStms();
				LinkedList<PAstStm> elseStms = ((ALongifAstStm) stm).getElseStms();
				if(checkReturn(ifStms) && checkReturn(elseStms))
					return true;
			}
		}
		return false;
	}
	
	
	public void caseABasicCastAstExp(ABasicCastAstExp node){
		if(node.getBasicTypes().getText().toLowerCase().equals("string"))
			error("cannot use string type for casting", node);
	}
	
	public void caseAIncDecAstStm(AIncDecAstStm node){
		PAstExp exp = node.getAstExp();
		checkIsLvalue(exp);
	}
	
	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
		LinkedList<PAstExp> lvals = node.getIds();
		if(lvals.size() != node.getAstExp().size())
			error("number of operands not match on different sides of the short variable decl",node);
		for(PAstExp lval : lvals){
			if(!(lval instanceof AIdAstExp)){
				error("LHS of short decl is not id list",lval);
			}
		}
	}
	
	public void caseAAssignAstStm(AAssignAstStm node){
		if(node.getLval().size() != node.getRval().size()){
			error("number of expressions not match on different sides of the assignment", node);
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
			error("LHS of assignment not a valid lvalue", l);
		}
	}
	
	public void caseABreakAstStm(ABreakAstStm node){
		Node parent = node.parent();
		while(parent != null){
			if(parent instanceof AForAstStm || parent instanceof AAstSwitchStm){
				return;
			}
			parent = parent.parent();
		}
		error("break keyword not used inside enclosing for loop or switch", node);
	}
	
	public void caseAContinueAstStm(AContinueAstStm node){
		
		Node parent = node.parent();
		while(parent != null){
			if(parent instanceof AForAstStm ){
				return;
			}
			parent = parent.parent();
		}
		error("continue keyword not used inside enclosing for loop", node);
	}
	
	public void inASwitchAstStm(ASwitchAstStm node){
		LinkedList<PAstSwitchStm> astSwitchStm = node.getAstSwitchStm();
		boolean hasDefault = false;
		
		for(PAstSwitchStm switchStm : astSwitchStm){
			PAstSwitchCase switchCase = ((AAstSwitchStm) switchStm).getAstSwitchCase();
			if(switchCase instanceof ADefaultAstSwitchCase){
				if(hasDefault)
					error("more than one default in switch statement", switchCase);
				hasDefault = true;
			}
		}
	}
	
	
	private void error(String msg, Node n) {
		throw new WeederException(String.format("%s around position [%d:%d]", msg, pos.getLine(n), pos.getPos(n)));
	}
}
