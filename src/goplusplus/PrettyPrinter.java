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
}
