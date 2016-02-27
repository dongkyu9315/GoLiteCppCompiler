package goplusplus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class PrettyPrinter extends DepthFirstAdapter{
	public static void print(Node node, String filename) {
		node.apply(new PrettyPrinter(filename));
	}
	
	Stack<Integer> mIndentStack;
	FileWriter mFileWriter;
	
	public PrettyPrinter(String filename) {
		try {
			mFileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mIndentStack = new Stack<Integer>();
	}
	
	private void print(String s) {
		try {
			mFileWriter.append(s+" ");
			mFileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void print_idlist(LinkedList<TId> idlist) {
		for (Iterator iterator = idlist.iterator(); iterator.hasNext();) {
			TId id = (TId) iterator.next();
			print(id.getText().trim());
			if (iterator.hasNext())
				print(",");
		}
	}
	
	@Override
	public void caseAAstProgram(AAstProgram node) {
		print("package");
		print(node.getPackage().getText().trim());
		
		LinkedList decl = node.getDecl();
		if (!decl.isEmpty()) {
			for (Iterator iterator = decl.iterator(); iterator.hasNext();) {
				PAstDecl d = (PAstDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseAVarDecAstDecl(AVarDecAstDecl node) {
		LinkedList decl = node.getAstVarDecl();
		if (!decl.isEmpty()) {
			for (Iterator iterator = decl.iterator(); iterator.hasNext();) {
				PAstVarDecl d = (PAstVarDecl) iterator.next();
				d.apply(this);
				print("\n");
			}
		}
	}
	
	@Override
	public void caseATypeDecAstDecl(ATypeDecAstDecl node) {
		LinkedList decl = node.getAstTypeDecl();
		if (!decl.isEmpty()) {
			for (Iterator iterator = decl.iterator(); iterator.hasNext();) {
				PAstTypeDecl d = (PAstTypeDecl) iterator.next();
				d.apply(this);
				print("\n");
			}
		}
	}
	
	@Override
	public void caseAFuncDecAstDecl(AFuncDecAstDecl node) {
		node.apply(this);
	}
	
	@Override
	public void caseATypeAstVarDecl(ATypeAstVarDecl node) {
		print("var");
		LinkedList<TId> idlist = node.getId();
		print_idlist(idlist);		
		node.getAstTypeExp().apply(this);
	}
	
	@Override
	public void caseAExpAstVarDecl(AExpAstVarDecl node) {
		print("var");
		LinkedList<TId> idlist = node.getId();
		print_idlist(idlist);	
		
		print("=");
		
		LinkedList exps = node.getAstExp();
		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
	}
	
	@Override
	public void caseATypeExpAstVarDecl(ATypeExpAstVarDecl node) {
		print("var");
		
		LinkedList<TId> idlist = node.getId();
		print_idlist(idlist);	
		
		node.getAstTypeExp().apply(this);
		
		print("=");
		
		LinkedList exps = node.getAstExp();
		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
	}
	
	@Override
	public void caseAAstTypeDecl(AAstTypeDecl node) {
		print("type");
		LinkedList<TId> idlist = node.getId();
		print_idlist(idlist);
		
		node.getAstTypeExp().apply(this);
	}
	
	@Override
	public void caseAAstFuncDecl(AAstFuncDecl node) {
		print("func");
		print(node.getId().getText().trim());
		print("(");
		
		LinkedList params = node.getAstFuncParam();
		for (Iterator iterator = params.iterator(); iterator.hasNext();) {
			PAstFuncParam param = (PAstFuncParam) iterator.next();
			param.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		
		print(")");
		
		if(node.getAstTypeExp() != null) {
			node.getAstTypeExp().apply(this);
		}
		
		print("\n{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList stmts = node.getAstStm();
		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		print("\n");
	}
	
	@Override
	public void caseAAstFuncParam(AAstFuncParam node) {
		print_idlist(node.getId());
		node.getAstTypeExp().apply(this);
	}
	
	@Override
	public void caseAFieldAccessAstExp(AFieldAccessAstExp node) {
		
	}
	
	@Override
	public void caseAAstSwitchStm(AAstSwitchStm node) {
		node.getAstSwitchCase().apply(this);
		
		LinkedList stms = node.getAstStm();
		if (!stms.isEmpty()) {
			for (Iterator iterator = stms.iterator(); iterator.hasNext();) {
				PAstStm d = (PAstStm) iterator.next();
				d.apply(this);
				print("\n");
			}
		}
		
		PAstFallthroughStm tmp = (PAstFallthroughStm) node.getAstFallthroughStm();
		if (!tmp.toString().isEmpty()) {
			print("fallthrough\n");
		}
	}
	
	@Override
	public void caseADefaultAstSwitchCase(ADefaultAstSwitchCase node) {
		print("default: ");
	}
	
	@Override
	public void caseACaseAstSwitchCase(ACaseAstSwitchCase node) {
		print("case ");
		LinkedList exps = node.getAstExp();
		if (!exps.isEmpty()) {
			for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
				PAstExp d = (PAstExp) iterator.next();
				d.apply(this);
				print(", ");
			}
		}
		print(": ");
	}
	
	@Override
	public void caseAIntAstLiteral(AIntAstLiteral node) {
		print(node.getIntLit().toString().trim());
	}
	
	@Override
	public void caseAFloatAstLiteral(AFloatAstLiteral node) {
		print(node.getFloatLit().toString().trim());
	}
	
	@Override
	public void caseARuneAstLiteral(ARuneAstLiteral node) {
		print(node.getRuneLit().toString().trim());
	}
	
	@Override
	public void caseAStringAstLiteral(AStringAstLiteral node) {
		print(node.getStringLit().toString().trim());
	}
	
	@Override
	public void caseAAddAstBinaryOp(AAddAstBinaryOp node) {
		print("+");
	}
	
	@Override
	public void caseASubAstBinaryOp(ASubAstBinaryOp node) {
		print("-");
	}
	
	@Override
	public void caseAMulAstBinaryOp(AMulAstBinaryOp node) {
		print("*");
	}
	
	@Override
	public void caseADivAstBinaryOp(ADivAstBinaryOp node) {
		print("/");
	}
	
	@Override
	public void caseAModAstBinaryOp(AModAstBinaryOp node) {
		print("%");
	}
	
	@Override
	public void caseABitorAstBinaryOp(ABitorAstBinaryOp node) {
		print("|");
	}
	
	@Override
	public void caseABitandAstBinaryOp(ABitandAstBinaryOp node) {
		print("&");
	}
	
	@Override
	public void caseAEqAstBinaryOp(AEqAstBinaryOp node) {
		print("==");
	}
	
	@Override
	public void caseANoteqAstBinaryOp(ANoteqAstBinaryOp node) {
		print("!=");
	}
	
	@Override
	public void caseALtAstBinaryOp(ALtAstBinaryOp node) {
		print("<");
	}
	
	@Override
	public void caseALeqAstBinaryOp(ALeqAstBinaryOp node) {
		print("<=");
	}
	
	@Override
	public void caseAGtAstBinaryOp(AGtAstBinaryOp node) {
		print(">");
	}
	
	@Override
	public void caseAGeqAstBinaryOp(AGeqAstBinaryOp node) {
		print(">=");
	}
	
	@Override
	public void caseACaretAstBinaryOp(ACaretAstBinaryOp node) {
		print("^");
	}
	
	@Override
	public void caseALshiftAstBinaryOp(ALshiftAstBinaryOp node) {
		print("<<");
	}
	
	@Override
	public void caseARshiftAstBinaryOp(ARshiftAstBinaryOp node) {
		print(">>");
	}
	
	@Override
	public void caseABitclearAstBinaryOp(ABitclearAstBinaryOp node) {
		print("&^");
	}
	
	@Override
	public void caseAOrAstBinaryOp(AOrAstBinaryOp node) {
		print("||");
	}
	
	@Override
	public void caseAAndAstBinaryOp(AAndAstBinaryOp node) {
		print("&&");
	}
	
	@Override
	public void caseAAddEqAstOpAssign(AAddEqAstOpAssign node) {
		print("+=");
	}
	
	@Override
	public void caseASubEqAstOpAssign(ASubEqAstOpAssign node) {
		print("-=");
	}
	
	@Override
	public void caseAMulEqAstOpAssign(AMulEqAstOpAssign node) {
		print("*=");
	}
	
	@Override
	public void caseADivEqAstOpAssign(ADivEqAstOpAssign node) {
		print("/=");
	}
	
	@Override
	public void caseAModEqAstOpAssign(AModEqAstOpAssign node) {
		print("%=");
	}
	
	@Override
	public void caseABitorEqAstOpAssign(ABitorEqAstOpAssign node) {
		print("|=");
	}
	
	@Override
	public void caseABitandEqAstOpAssign(ABitandEqAstOpAssign node) {
		print("&=");
	}
	
	@Override
	public void caseACaretEqAstOpAssign(ACaretEqAstOpAssign node) {
		print("^=");
	}
	
	@Override
	public void caseALshiftEqAstOpAssign(ALshiftEqAstOpAssign node) {
		print("<<=");
	}
	
	@Override
	public void caseARshiftEqAstOpAssign(ARshiftEqAstOpAssign node) {
		print(">>=");
	}
	
	@Override
	public void caseABitclearEqAstOpAssign(ABitclearEqAstOpAssign node) {
		print("&^=");
	}
	
	@Override
	public void caseAPlusAstUnaryOp(APlusAstUnaryOp node) {
		print("+");
	}
	
	@Override
	public void caseAMinusAstUnaryOp(AMinusAstUnaryOp node) {
		print("-");
	}
	
	@Override
	public void caseANotAstUnaryOp(ANotAstUnaryOp node) {
		print("!");
	}
	
	@Override
	public void caseACaretAstUnaryOp(ACaretAstUnaryOp node) {
		print("^");
	}
	
	@Override
	public void caseAIncPostOp(AIncPostOp node) {
		print("++");
	}
	
	@Override
	public void caseADecPostOp(ADecPostOp node) {
		print("--");
	}
	
	@Override
	public void caseAAstFallthroughStm(AAstFallthroughStm node) {
		
	}
}
