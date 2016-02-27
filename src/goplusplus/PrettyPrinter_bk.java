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
		LinkedList fields = node.getAstStructField();
		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
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
	public void caseAEmptyAstStm(AEmptyAstStm node) {
		//do nothing
	}
	
	@Override
	public void caseAExpAstStm(AExpAstStm node) {
		node.getAstExp().apply(this);
	}
	
	@Override
	public void caseAAssignAstStm(AAssignAstStm node) {
		LinkedList lvals = node.getLval();
		for (Iterator iterator = lvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		
		print("=");
		
		LinkedList rvals = node.getRval();
		for (Iterator iterator = rvals.iterator(); iterator.hasNext();) {
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
		LinkedList decls = node.getAstVarDecl();
		for (Iterator iterator = decls.iterator(); iterator.hasNext();) {
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
		LinkedList lvals = node.getIds();
		for (Iterator iterator = lvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
		
		print(":=");
		
		LinkedList rvals = node.getAstExp();
		for (Iterator iterator = rvals.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(",");
		}
	}
	
	@Override
	public void caseATypeDeclAstStm(ATypeDeclAstStm node) {
		LinkedList decls = node.getAstTypeDecl();
		for (Iterator iterator = decls.iterator(); iterator.hasNext();) {
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
		node.getPostOp().apply(this);
	}
	
	@Override
	public void caseAPrintAstStm(APrintAstStm node) {
		print("print(");
		LinkedList exps = node.getAstExp();
		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
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
		LinkedList exps = node.getAstExp();
		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
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
		
		print("\n{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList stmts = node.getAstStm();
		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
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
	public void caseALongifAstStm(ALongifAstStm node) {
		print("if");
		if (node.getInit() != null) {
			node.getInit().apply(this);
			print(";");
		}
		node.getCondition().apply(this);
		
		print("\n{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList if_stmts = node.getIfStms();
		for (Iterator iterator = if_stmts.iterator(); iterator.hasNext();) {
			for (int i = 0; i < mIndentStack.size(); i++)
				print("\t");
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
			print("\n");
		}
		
		mIndentStack.pop();
		print("}\n");
		
		print("else\n{\n");
		mIndentStack.push(mIndentStack.size()+1);
		LinkedList else_stmts = node.getElseStms();
		for (Iterator iterator = else_stmts.iterator(); iterator.hasNext();) {
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
		
		print("\n{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList stmts = node.getBody();
		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
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
	public void caseABlockAstStm(ABlockAstStm node) {
		print("{\n");
		
		mIndentStack.push(mIndentStack.size()+1);
		
		LinkedList stmts = node.getAstStm();
		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
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
		print("println(");
		LinkedList exps = node.getArgs();
		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
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
}
