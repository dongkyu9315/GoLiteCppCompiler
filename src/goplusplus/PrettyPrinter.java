package goplusplus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class PrettyPrinter extends DepthFirstAdapter{
	public void print(Node node) {
		node.apply(this);
	}
	
	Stack<Integer> mIndentStack;
	FileWriter mFileWriter;
	Typechecker typechecker;
	
	public PrettyPrinter(String filename, Position pos) {
		try {
			mFileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mIndentStack = new Stack<Integer>();
		typechecker = new Typechecker(pos, false);
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
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
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
		print("\n\n");
		LinkedList<?> decl = node.getDecl();
		if (!decl.isEmpty()) {
			for (Iterator<?> iterator = decl.iterator(); iterator.hasNext();) {
				PAstDecl d = (PAstDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseAVarDecAstDecl(AVarDecAstDecl node) {
		LinkedList<PAstVarDecl> decl = node.getAstVarDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstVarDecl> iterator = decl.iterator(); iterator.hasNext();) {
				PAstVarDecl d = (PAstVarDecl) iterator.next();
				d.apply(this);
				print("\n");
			}
		}
	}
	
	@Override
	public void caseATypeDecAstDecl(ATypeDecAstDecl node) {
		LinkedList<?> decl = node.getAstTypeDecl();
		if (!decl.isEmpty()) {
			for (Iterator<?> iterator = decl.iterator(); iterator.hasNext();) {
				PAstTypeDecl d = (PAstTypeDecl) iterator.next();
				d.apply(this);
				print("\n");
			}
		}
	}
	
	@Override
	public void caseAFuncDecAstDecl(AFuncDecAstDecl node) {
		node.getAstFuncDecl().apply(this);
		print("//");
		node.apply(typechecker);
		AAstFuncDecl temp = (AAstFuncDecl)node.getAstFuncDecl();
		print(typechecker.symbolTable.getLast().get(temp.getId().getText().trim()).toString());
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
		
		LinkedList<?> exps = node.getAstExp();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
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
		
		LinkedList<?> exps = node.getAstExp();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
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
		
		LinkedList<?> params = node.getAstFuncParam();
		for (Iterator<?> iterator = params.iterator(); iterator.hasNext();) {
			PAstFuncParam param = (PAstFuncParam) iterator.next();
			param.apply(this);
			if (iterator.hasNext())
				print(",");
		}
	
		print(")");
		
		if(node.getAstTypeExp() != null) {
			node.getAstTypeExp().apply(this);
		}
		
		print("{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList<?> stmts = node.getAstStm();
		for (Iterator<?> iterator = stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		print("}\n");
	}
	
	@Override
	public void caseAAstFuncParam(AAstFuncParam node) {
		print_idlist(node.getId());
		node.getAstTypeExp().apply(this);
	}
	
		@Override
	public void caseABasicAstTypeExp(ABasicAstTypeExp node) {
		print(node.getBasicTypes().getText().trim());
	}
	
	@Override
	public void caseASliceAstTypeExp(ASliceAstTypeExp node) {
		print("[]");
		node.getAstTypeExp().apply(this);
	}
	
	@Override
	public void caseAArrayAstTypeExp(AArrayAstTypeExp node) {
		print("["+node.getSize().getText().trim()+"]");
		node.getAstTypeExp().apply(this);
	}
	
	@Override
	public void caseAStructAstTypeExp(AStructAstTypeExp node) {
		LinkedList<?> fields = node.getAstStructField();
		for (Iterator<?> iterator = fields.iterator(); iterator.hasNext();) {
			PAstStructField field = (PAstStructField) iterator.next();
			field.apply(this);
			print("\n");
		}
	}
	
	@Override
	public void caseAAstStructField(AAstStructField node) {
		print_idlist(node.getId());
		node.getAstTypeExp().apply(this);
	}
	
	@Override
	public void caseAAliasAstTypeExp(AAliasAstTypeExp node) {
		print(node.getId().getText().trim());
	}
	
	@Override
	public void caseAEmptyAstStm(AEmptyAstStm node) {
		//do nothing
	}
	
	@Override
	public void caseAExpAstStm(AExpAstStm node) {
		node.getAstExp().apply(this);
	}
	
	@Override
	public void caseAAssignAstStm(AAssignAstStm node) {
		LinkedList<?> lvals = node.getLval();
		for (Iterator<?> iterator = lvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		
		print("=");
		
		LinkedList<?> rvals = node.getRval();
		for (Iterator<?> iterator = rvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
	}
	
	@Override
	public void caseAOpAssignAstStm(AOpAssignAstStm node) {
		print(node.getL().getText().trim());
		node.getAstOpAssign().apply(this);
		node.getR().apply(this);
	}
	
	@Override
	public void caseAVarDeclAstStm(AVarDeclAstStm node) {
		LinkedList<?> decls = node.getAstVarDecl();
		for (Iterator<?> iterator = decls.iterator(); iterator.hasNext();) {
			PAstVarDecl exp = (PAstVarDecl) iterator.next();
			exp.apply(this);
			if (iterator.hasNext()){
				print("\n");
				for(int i = 0; i < mIndentStack.size(); i++) {
					print("\t");
				}
			}
		}
	}
	
	@Override
	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
		LinkedList<?> lvals = node.getIds();
		for (Iterator<?> iterator = lvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		
		print(":=");
		
		LinkedList<?> rvals = node.getAstExp();
		for (Iterator<?> iterator = rvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
	}
	
	@Override
	public void caseATypeDeclAstStm(ATypeDeclAstStm node) {
		LinkedList<?> decls = node.getAstTypeDecl();
		for (Iterator<?> iterator = decls.iterator(); iterator.hasNext();) {
			PAstTypeDecl exp = (PAstTypeDecl) iterator.next();
			exp.apply(this);
			if (iterator.hasNext()){
				print("\n");
				for(int i = 0; i < mIndentStack.size(); i++) {
					print("\t");
				}
			}
		}
	}
	
	@Override
	public void caseAIncDecAstStm(AIncDecAstStm node) {
		node.getAstExp().apply(this);
		node.getAstPostOp().apply(this);
	}
	
	@Override
	public void caseAPrintAstStm(APrintAstStm node) {
		print("print(");
		LinkedList<?> exps = node.getAstExp();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		print(")");
	}
	
	@Override
	public void caseAPrintlnAstStm(APrintlnAstStm node) {
		print("println(");
		LinkedList<?> exps = node.getAstExp();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		print(")");
	}
	
	@Override
	public void caseAReturnAstStm(AReturnAstStm node) {
		print("return");
		if (node.getAstExp() != null)
			node.getAstExp().apply(this);
	}
	
	@Override
	public void caseAShortifAstStm(AShortifAstStm node) {
		print("if");
		if (node.getInit() != null) {
			node.getInit().apply(this);
			print(";");
		}
		node.getCondition().apply(this);
		
		print("{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList<?> stmts = node.getAstStm();
		for (Iterator<?> iterator = stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();

		for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
		print("}\n");
	}
	
	@Override
	public void caseALongifAstStm(ALongifAstStm node) {
		print("if");
		if (node.getInit() != null) {
			node.getInit().apply(this);
			print(";");
		}
		node.getCondition().apply(this);
		
		print("{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList<?> if_stmts = node.getIfStms();
		for (Iterator<?> iterator = if_stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
		print("}\n");
		
		print("else {\n");
		mIndentStack.push(mIndentStack.size()+1);
		LinkedList<?> else_stmts = node.getElseStms();
		for (Iterator<?> iterator = else_stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
		print("}\n");
	}
	
	@Override
	public void caseASwitchAstStm(ASwitchAstStm node) {
		print("switch");
		node.getAstStm().apply(this);
		node.getAstExp().apply(this);
		print(" {\n");
		LinkedList<?> stms = node.getAstSwitchStm();
		for (Iterator<?> iterator = stms.iterator(); iterator.hasNext();) {
			PAstSwitchStm stm = (PAstSwitchStm) iterator.next();
			stm.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		print("}\n");
	}
	
	@Override
	public void caseAForAstStm(AForAstStm node) {
		print("for");
		if (node.getInit() != null) {
			node.getInit().apply(this);
			print(";");
		}
		if (node.getCondition() != null) {
			node.getCondition().apply(this);
		}
		if (node.getPost() != null) {
			print(";");
			node.getPost().apply(this);
		}
		
		print("{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList<?> stmts = node.getBody();
		for (Iterator<?> iterator = stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
		print("}\n");
	}
	
	@Override
	public void caseABlockAstStm(ABlockAstStm node) {
		print("{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList<?> stmts = node.getAstStm();
		for (Iterator<?> iterator = stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		print("}\n");
	}
	
	@Override
	public void caseABreakAstStm(ABreakAstStm node) {
		print("break");
	}
	
	@Override
	public void caseAContinueAstStm(AContinueAstStm node) {
		print("continue");
	}
	
	@Override
	public void caseAParenAstExp(AParenAstExp node) {
		print("(");
		node.getAstExp().apply(this);
		print(")");
	}
	
	@Override
	public void caseAIdAstExp(AIdAstExp node) {
		print(node.getId().getText().trim());
	}
	
	@Override
	public void caseALitAstExp(ALitAstExp node) {
		node.getAstLiteral().apply(this);
	}
	
	@Override
	public void caseAUnaryOpAstExp(AUnaryOpAstExp node) {
		node.getAstUnaryOp().apply(this);
		node.getAstExp().apply(this);
	}
	
	@Override
	public void caseABinaryOpAstExp(ABinaryOpAstExp node) {
		node.getLeft().apply(this);
		node.getAstBinaryOp().apply(this);
		node.getRight().apply(this);
	}
	
	@Override
	public void caseAFuncCallAstExp(AFuncCallAstExp node) {
		node.getName().apply(this);
		print("(");
		LinkedList<?> exps = node.getArgs();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		print(")");
	}
	
	@Override
	public void caseAAppendAstExp(AAppendAstExp node) {
		print("append(");
		print(node.getId().getText().trim());
		print(",");
		node.getAstExp().apply(this);
		print(")");
	}
	
	@Override
	public void caseABasicCastAstExp(ABasicCastAstExp node) {
		print(node.getBasicTypes().toString().trim());
		print("(");
		node.getAstExp().apply(this);
		print(")");
	}
	
	@Override
	public void caseAArrayAccessAstExp(AArrayAccessAstExp node) {
		node.getArray().apply(this);
		print("[");
		node.getIndex().apply(this);
		print("]");
	}
	
	@Override
	public void caseAFieldAccessAstExp(AFieldAccessAstExp node) {
		node.getStruct().apply(this);
		print(".");
		print(node.getField().toString().trim());
	}
	
	@Override
	public void caseAAstSwitchStm(AAstSwitchStm node) {
		node.getAstSwitchCase().apply(this);
		
		LinkedList<?> stms = node.getAstStm();
		if (!stms.isEmpty()) {
			for (Iterator<?> iterator = stms.iterator(); iterator.hasNext();) {
				PAstStm d = (PAstStm) iterator.next();
				d.apply(this);
				print("\n");
			}
		}
		
		node.getAstFallthroughStm().apply(this);
		print("\n");
	}
	
	@Override
	public void caseADefaultAstSwitchCase(ADefaultAstSwitchCase node) {
		print("default: ");
	}
	
	@Override
	public void caseACaseAstSwitchCase(ACaseAstSwitchCase node) {
		print("case ");
		LinkedList<?> exps = node.getAstExp();
		if (!exps.isEmpty()) {
			for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
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
	public void caseAIncAstPostOp(AIncAstPostOp node) {
		// TODO Auto-generated method stub
		print("++");
	}
	
	@Override
	public void caseADecAstPostOp(ADecAstPostOp node) {
		print("--");
	}
	
	@Override
	public void caseAAstFallthroughStm(AAstFallthroughStm node) {
		print("fallthrough");
	}
}
