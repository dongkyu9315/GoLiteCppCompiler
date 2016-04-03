package goplusplus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import type.*;
import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class CppGenerator extends DepthFirstAdapter{
	public LinkedList<HashMap<String, Type>> symbolTable;
	private Position pos;
	FileWriter mFileWriter;
	Stack<Integer> mIndentStack;
	private final String INT = "int";
	private final String FLOAT64 = "float";
	private final String BOOL = "boolean";
	private final String STRING = "string";
	private final String RUNE = "char";
	
	public CppGenerator(String filename, Position p) {
		symbolTable = new LinkedList<HashMap<String, Type> >();
		symbolTable.addFirst(new HashMap<String, Type>());
		pos = p;
		mIndentStack = new Stack<Integer>();
		try {
			mFileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void print(String s) {
		try {
//			mFileWriter.append(s + " ");
//			mFileWriter.flush();
			System.out.print(s+" ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void print(Node n) {
		n.apply(this);
	}
	
	private void newScope() {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
		mIndentStack.push(mIndentStack.size()+1);
	}
	
	private void exitScope() {
		symbolTable.removeFirst();
		mIndentStack.pop();
	}
	
	private void printTab(){
		for (int i = 0; i < mIndentStack.size(); i++)
			print("\t");
	}
	
	public void caseTId(TId node){
		print(node.getText());
	}
	
	// ast_program		---------------------------------------------------
	@Override
	public void caseAAstProgram(AAstProgram node) {
		print("#include <stdlib.h>");
		print("\n#include <iostream>");
		print("\n#include <string>");
		print("\n");
		LinkedList<PAstDecl> decl = node.getDecl();
		if (!decl.isEmpty()) {
			//first pass to collect root level vars and functions			
			for (PAstDecl d : decl) {
				if (d instanceof AFuncDecAstDecl) {
					addFunction((AAstFuncDecl) ((AFuncDecAstDecl)d).getAstFuncDecl());
				} else {
					d.apply(this);
					print(";");
				}
			}

			for (PAstDecl d : decl) {
				if (d instanceof AFuncDecAstDecl) {
					d.apply(this);
					print(";");
				}
			}
		}
	}
	
	// ast_var_decl		---------------------------------------------------
	@Override
	public void caseATypeAstVarDecl(ATypeAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		PAstTypeExp typeExp = node.getAstTypeExp();
		typeExp.apply(this);
		Type varType = forPAstTypeExp(typeExp);
		
		String sep = "";
		for (TId d : idlist) {
			symbolTable.getFirst().put(d.getText().trim(), varType);
			print(sep);
			d.apply(this);
			sep = ",";
		}
	}
	
	@Override
	public void caseAExpAstVarDecl(AExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		LinkedList<PAstExp> exps = node.getAstExp();

		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			symbolTable.getFirst().put(d.getText().trim(), varType);
		}
	}

	@Override
	public void caseATypeExpAstVarDecl(ATypeExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type typeExp = forPAstTypeExp(node.getAstTypeExp());
		LinkedList<PAstExp> exps = node.getAstExp();

		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			symbolTable.getFirst().put(d.getText().trim(), varType);
		}
	}

	// ast_type_decl	---------------------------------------------------
	@Override
	public void caseAAstTypeDecl(AAstTypeDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (TId d : idlist) {
			AliasType aType = new AliasType();
			aType.type = varType;
			symbolTable.getFirst().put(d.getText().trim(), aType);

			// for the fields of a struct
			if (varType.is(Type.STRUCT)) {
				AStructAstTypeExp tempNode = (AStructAstTypeExp) node.getAstTypeExp();
				LinkedList<PAstStructField> fieldList = tempNode.getAstStructField();
				for (PAstStructField f : fieldList) {
					AAstStructField tempField = (AAstStructField) f;
					LinkedList<TId> fieldIdList = tempField.getId();
					for (Iterator<TId> fieldIter = fieldIdList.iterator(); fieldIter.hasNext();) {
						TId field = (TId) fieldIter.next();
						String fieldName = d.getText().trim() + "." + field.toString().trim();
						Type fieldType = forPAstTypeExp(tempField.getAstTypeExp());
						symbolTable.getFirst().put(fieldName, fieldType);
					}
				}
			}
		}
	}

	
	public void inAAstFuncDecl(AAstFuncDecl node) {
		newScope();
	}
	
	public void outAAstFuncDecl(AAstFuncDecl node) {
		exitScope();
	}

	public void addFunction(AAstFuncDecl node) {
		FunctionType funcType = new FunctionType();
		Type returnType;
		if (node.getAstTypeExp() != null) {
			returnType = forPAstTypeExp(node.getAstTypeExp());
		} else {
			returnType = Type.VOID;
		}

		funcType.returnType = returnType;

		LinkedList<PAstFuncParam> params = node.getAstFuncParam();
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		for (Iterator<PAstFuncParam> iterator = params.iterator(); iterator.hasNext();) {
			PAstFuncParam param = (PAstFuncParam) iterator.next();
			Type pType = forPAstFuncParam(param);
			for (int i = 0; i < ((AAstFuncParam)param).getId().size(); i++)
				paramTypes.add(pType);
		}

		funcType.paramType = paramTypes;

		symbolTable.getFirst().put(node.getId().getText().trim(), funcType);
	}


	// ast_func_param	---------------------------------------------------
	public Type forPAstFuncParam(PAstFuncParam node) {
		if (node.getClass().isInstance(new AAstFuncParam())) {
			AAstFuncParam temp = (AAstFuncParam) node;
			return forPAstTypeExp(temp.getAstTypeExp());
		}
		return null;
	}

	@Override
	public void caseAAstFuncParam(AAstFuncParam node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			symbolTable.getFirst().put(d.getText().trim(), varType);
		}
	}

	// ast_type_exp 	---------------------------------------------------
	// returns the type of PAstTypeExp
	public Type forPAstTypeExp(PAstTypeExp node) {
		if (node.getClass().isInstance(new ABasicAstTypeExp())) {
			ABasicAstTypeExp temp = (ABasicAstTypeExp) node;
			String t = temp.getBasicTypes().toString().trim();
			if (t.equals("int"))
				return Type.INT;
			else if (t.equals("float64"))
				return Type.FLOAT64;
			else if (t.equals("rune"))
				return Type.RUNE;
			else if (t.equals("string"))
				return Type.STRING;
			else if (t.equals("bool"))
				return Type.BOOL;
			else {
				return null;
			}
		} else if (node.getClass().isInstance(new ASliceAstTypeExp())) {
			ASliceAstTypeExp temp = (ASliceAstTypeExp) node;
			Type eleType = forPAstTypeExp(temp.getAstTypeExp());
			SliceType sliceType = new SliceType();
			sliceType.elementType = eleType;
			return sliceType;
		} else if (node.getClass().isInstance(new AArrayAstTypeExp())) {
			AArrayAstTypeExp temp = (AArrayAstTypeExp) node;
			Type eleType = forPAstTypeExp(temp.getAstTypeExp());
			ArrayType arrayType = new ArrayType();
			arrayType.elementType = eleType;
			arrayType.size = Integer.parseInt(temp.getSize().toString().trim());
			return arrayType;
		} else if (node.getClass().isInstance(new AStructAstTypeExp())) {
			AStructAstTypeExp temp = (AStructAstTypeExp) node;
			LinkedList<PAstStructField> structList = temp.getAstStructField();
			HashMap<String, Type> typesList = new HashMap<String, Type>();
			for (Iterator<PAstStructField> iterator = structList.iterator(); iterator.hasNext();) {
				AAstStructField field = (AAstStructField) iterator.next();
				Type eleType = forPAstTypeExp(field.getAstTypeExp());
				LinkedList<TId> idList = field.getId();
				for (Iterator<TId> iter = idList.iterator(); iter.hasNext();) {
					typesList.put(iter.next().getText().trim(), eleType);
				}
			}
			StructType structType = new StructType();
			structType.attributes = typesList;
			return structType;
		} else if (node.getClass().isInstance(new AAliasAstTypeExp())) {
			AAliasAstTypeExp temp = (AAliasAstTypeExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					Type attType = symbolTable.get(i).get(temp.getId().getText().trim());
					if (attType.is(Type.ALIAS)) {
						return ((AliasType) attType).type;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void caseABasicAstTypeExp(ABasicAstTypeExp node){
		Type type = forPAstTypeExp(node);
		if(type.is(Type.INT)){
			print(INT);
		}
		else if(type.is(Type.BOOL)){
			print(BOOL);
		}
		else if(type.is(Type.RUNE)){
			print(RUNE);
		}
		else if(type.is(Type.STRING)){
			print(STRING);
		}
		else if(type.is(Type.FLOAT64)){
			print(FLOAT64);
		}
	}
	
	// ast_stm
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
		PAstOpAssign opAssign = node.getAstOpAssign();
		if (opAssign.getClass().isInstance(new ABitclearEqAstOpAssign())) {
			print(node.getL().getText().trim());
			print("&=");
			node.getR().apply(this);
			print(";");
			print(node.getL().getText().trim());
			print("^=");
			node.getR().apply(this);
			print(";");
		} else {
			print(node.getL().getText().trim());
			opAssign.apply(this);
			node.getR().apply(this);
		}
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
	
	// TODO: implement this method
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
	
	// return true if the variable on the left is in the current scope
	public boolean helperForShortDecl(PAstExp node, Type rightType) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			return helperForShortDecl(temp.getAstExp(), rightType);
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			if (!symbolTable.getFirst().containsKey(temp.getId().getText().trim())) {
				symbolTable.getFirst().put(temp.getId().getText().trim(), rightType);
				return false;
			}
			return true;
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			return true;
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			return true;
		}
		return false;
	}
	
	@Override
	public void caseATypeDeclAstStm(ATypeDeclAstStm node) {
		LinkedList<?> decls = node.getAstTypeDecl();
		for (Iterator<?> iterator = decls.iterator(); iterator.hasNext();) {
			PAstTypeDecl exp = (PAstTypeDecl) iterator.next();
			exp.apply(this);
			if (iterator.hasNext()) {
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
		print("std::cout << ");
		LinkedList<?> exps = node.getAstExp();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(" << ");
		}
		print(";");
	}
	
	@Override
	public void caseAPrintlnAstStm(APrintlnAstStm node) {
		print("std::cout << ");
		LinkedList<?> exps = node.getAstExp();
		for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			exp.apply(this);
			if (iterator.hasNext())
				print(" << ");
		}
		print("<< std::endl;");
	}
	
	@Override
	public void caseAReturnAstStm(AReturnAstStm node) {
		print("return");
		if (node.getAstExp() != null) {
			node.getAstExp().apply(this);
		}
		print(";");
	}
	
	@Override
	public void caseAShortifAstStm(AShortifAstStm node) {
		if (node.getInit() != null) {
			node.getInit().apply(this);
			print(";\n");
		}
		PAstExp condExp = node.getCondition();
		if (condExp.getClass().isInstance(new AParenAstExp())) {
			print("if");
			condExp.apply(this);
			print("{\n");
		} else {
			print("if (");
			condExp.apply(this);
			print(") {\n");
		}
		
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
	public void inAShortifAstStm(AShortifAstStm node) {
		newScope();
	}
	
	@Override
	public void outAShortifAstStm(AShortifAstStm node) {
		exitScope();
	}
	
	@Override
	public void caseALongifAstStm(ALongifAstStm node) {
		if (node.getInit() != null) {
			node.getInit().apply(this);
			print(";\n");
		}
		PAstExp condExp = node.getCondition();
		if (condExp.getClass().isInstance(new AParenAstExp())) {
			print("if");
			condExp.apply(this);
			print("{\n");
		} else {
			print("if (");
			condExp.apply(this);
			print(") {\n");
		}
		
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
	public void inALongifAstStm(ALongifAstStm node) {
		newScope();
	}
	
	@Override
	public void outALongifAstStm(ALongifAstStm node) {
		exitScope();
	}
	
	// TODO: implement below
//	@Override
//	public void caseASwitchAstStm(ASwitchAstStm node) {
//		print("switch");
//		node.getAstStm().apply(this);
//		node.getAstExp().apply(this);
//		print(" {\n");
//		LinkedList<?> stms = node.getAstSwitchStm();
//		for (Iterator<?> iterator = stms.iterator(); iterator.hasNext();) {
//			PAstSwitchStm stm = (PAstSwitchStm) iterator.next();
//			stm.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		print("}\n");
//	}
	
	@Override
	public void inASwitchAstStm(ASwitchAstStm node) {
		newScope();
	}
	
	@Override
	public void outASwitchAstStm(ASwitchAstStm node) {
		exitScope();
	}
	
	@Override
	public void caseAForAstStm(AForAstStm node) {
		print("for (");
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
		
		print(") {\n");
		
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
	public void inAForAstStm(AForAstStm node) {
		newScope();
	}
	
	@Override
	public void outAForAstStm(AForAstStm node) {
		exitScope();
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
	public void inABlockAstStm(ABlockAstStm node) {
		newScope();
	}
	
	@Override
	public void outABlockAstStm(ABlockAstStm node) {
		exitScope();
	}
	
	@Override
	public void caseABreakAstStm(ABreakAstStm node) {
		print("break;");
	}
	
	@Override
	public void caseAContinueAstStm(AContinueAstStm node) {
		print("continue;");
	}
	
	// ast_exp ---------------------------------------------------------------------
	// returns the type of PAstExp
	public Type forPAstExp(PAstExp node) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			return forPAstExp(temp.getAstExp());
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					return symbolTable.get(i).get(temp.getId().getText().trim());
				}
			}
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			ALitAstExp temp = (ALitAstExp) node;
			return forPAstLiteral(temp.getAstLiteral());
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			String unop = forPAstUnaryOp(temp.getAstUnaryOp());
			if (unop.equals("+")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} else if (unop.equals("-")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} else if (unop.equals("!")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} else if (unop.equals("^")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} 
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			Type leftType = forPAstExp(temp.getLeft());
			String binOp = forPAstBinaryOp(temp.getAstBinaryOp());
			Type rightType = forPAstExp(temp.getRight());

			if (leftType.assign(Type.BOOL) && rightType.assign(Type.BOOL)) {
				if (binOp.equals("||")) {
					return Type.BOOL;
				} else if (binOp.equals("&&")) {
					return Type.BOOL;
				}
			} else if (leftType.assign(Type.INT) && rightType.assign(Type.INT)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
				} else if (binOp.equals("+")) {
					return leftType;
				} else if (binOp.equals("-")) {
					return leftType;
				} else if (binOp.equals("*")) {
					return leftType;
				} else if (binOp.equals("/")) {
					return leftType;
				} else if (binOp.equals("%")) {
					return leftType;
				} else if (binOp.equals("|")) {
					return leftType;
				} else if (binOp.equals("&")) {
					return leftType;
				} else if (binOp.equals("<<")) {
					return leftType;
				} else if (binOp.equals(">>")) {
					return leftType;
				} else if (binOp.equals("&^")) {
					return leftType;
				} else if (binOp.equals("^")) {
					return leftType;
				}
			} else if (leftType.assign(Type.STRING) && rightType.assign(Type.STRING)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
				} else if (binOp.equals("+")) {
					return Type.STRING;
				}
			} else if (leftType.assign(Type.FLOAT64) && rightType.assign(Type.FLOAT64)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
				} else if (binOp.equals("+")) {
					return leftType;
				} else if (binOp.equals("-")) {
					return leftType;
				} else if (binOp.equals("*")) {
					return leftType;
				} else if (binOp.equals("/")) {
					return leftType;
				} else if (binOp.equals("%")) {
					return leftType;
				}
			} else if (leftType.assign(Type.RUNE) && rightType.assign(Type.RUNE)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
				} else if (binOp.equals("+")) {
					return leftType;
				} else if (binOp.equals("-")) {
					return leftType;
				} else if (binOp.equals("*")) {
					return leftType;
				} else if (binOp.equals("/")) {
					return leftType;
				} else if (binOp.equals("%")) {
					return leftType;
				} else if (binOp.equals("|")) {
					return leftType;
				} else if (binOp.equals("&")) {
					return leftType;
				} else if (binOp.equals("<<")) {
					return leftType;
				} else if (binOp.equals(">>")) {
					return leftType;
				} else if (binOp.equals("&^")) {
					return leftType;
				} else if (binOp.equals("^")) {
					return leftType;
				}
			} else {
			}
		} else if (node.getClass().isInstance(new AFuncCallAstExp())) {
			AFuncCallAstExp temp = (AFuncCallAstExp) node;
			Type type = forPAstExp(temp.getName());
			if (type.is(Type.FUNC)) {
				FunctionType funcType = (FunctionType) type;
				LinkedList<PAstExp> args = temp.getArgs();
				ArrayList<Type> params = funcType.paramType;
				boolean match = true;
				if (args.size() == params.size()) {
					Iterator<PAstExp> it = args.iterator();
					for (int i = 0; i < params.size() && it.hasNext(); i++) {
						PAstExp exp = it.next();
						Type argType = forPAstExp(exp);
						if (!params.get(i).assign(argType))	{
							match = false;
							break;
						}
					}
					if (match) {
						return funcType.returnType;
					}
				}
			} else if (temp.getName() instanceof AIdAstExp){
				String id = ((AIdAstExp) temp.getName()).getId().getText().trim();
				for (int i = 0; i < symbolTable.size(); i++) {
					if (symbolTable.get(i).containsKey(id)) {
						Type t = symbolTable.get(i).get(id);
						if (t.is(Type.ALIAS)) {
							return ((AliasType)t).type;
						} else {
						}
					}
				}
			}
		} else if (node.getClass().isInstance(new AAppendAstExp())) {
			AAppendAstExp temp = (AAppendAstExp) node;
			String id = temp.getId().getText().trim();
			for (Iterator<HashMap<String, Type>> iterator = symbolTable.iterator(); iterator.hasNext();) {
				HashMap<String, Type> table = (HashMap<String, Type>) iterator.next();
				if (table.containsKey(id)) {
					Type sliceType = table.get(id);
					Type astType = forPAstExp(temp.getAstExp());
					AppendType appendType = new AppendType();
					appendType.type = astType;
					if (sliceType.assign(appendType)) {
						return appendType;
					}
				}
			}
		} else if (node.getClass().isInstance(new ABasicCastAstExp())) {
			ABasicCastAstExp temp = (ABasicCastAstExp) node;
			Type basic = forPAstTypeExp(new ABasicAstTypeExp(temp.getBasicTypes()));
			Type other = forPAstExp(temp.getAstExp());
			if (basic.is(Type.INT)) {
				if (other.is(Type.FLOAT64) || other.is(Type.INT) || other.is(Type.RUNE)) {
					return basic;
				}
			} else if (basic.is(Type.FLOAT64)) {
				if (other.is(Type.INT) || other.is(Type.FLOAT64) || other.is(Type.RUNE)) {
					return basic;
				}
			} else if (basic.is(Type.BOOL)) {
				if (other.is(Type.BOOL)) {
					return basic;
				}
			} else if (basic.is(Type.RUNE)) {
				if (other.is(Type.RUNE) || other.is(Type.INT) || other.is(Type.FLOAT64)) {
					return basic;
				}
			}
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			AArrayAccessAstExp temp = (AArrayAccessAstExp) node;
			Type arrayType = forPAstExp(temp.getArray());
			if (arrayType.is(Type.ARRAY)) {
				Type indexType = forPAstExp(temp.getIndex());
				if (indexType.is(Type.INT)) {
					ArrayType arrT = (ArrayType) arrayType;
					int maxIndex = arrT.size;
					IntType intTypeIndex = (IntType) indexType;
					int index = intTypeIndex.value;
					if (index < maxIndex) {
						return intTypeIndex;
					}
				}
			} else if (arrayType.is(Type.SLICE)) {
				Type indexType = forPAstExp(temp.getIndex());
				if (indexType.is(Type.INT)) {
					IntType intTypeIndex = (IntType) indexType;
					return intTypeIndex;
				}
			}
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			AFieldAccessAstExp temp = (AFieldAccessAstExp)node;
			Type t = forPAstExp(temp.getStruct());
			if (t.is(Type.STRUCT)) {
				StructType struct = (StructType)t;
				String field = temp.getField().getText().trim();
				if (struct.attributes.containsKey(field)) {
					return struct.attributes.get(field);
				}
			}
		}
		return null;
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
	
	// TODO: check if the binaryOp is ABitclearAstBinaryOp and separate the & and ^
	@Override
	public void caseABinaryOpAstExp(ABinaryOpAstExp node) {
		PAstBinaryOp binaryOp = node.getAstBinaryOp();
		if (binaryOp.getClass().isInstance(new ABitclearAstBinaryOp())) {
			// implement
		} else {
			node.getLeft().apply(this);
			binaryOp.apply(this);
			node.getRight().apply(this);
		}
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
		print(node.getId().getText().trim());
		print(".push_back(");
		node.getAstExp().apply(this);
		print(")");
	}
	
	@Override
	public void caseABasicCastAstExp(ABasicCastAstExp node) {
		print("static_cast<");
		print(node.getBasicTypes().toString().trim());
		print(">(");
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
	
	// ast_switch_stm	-------------------------------------------------------
//	@Override
//	public void caseAAstSwitchStm(AAstSwitchStm node) {
//		node.getAstSwitchCase().apply(this);
//		
//		LinkedList<?> stms = node.getAstStm();
//		if (!stms.isEmpty()) {
//			for (Iterator<?> iterator = stms.iterator(); iterator.hasNext();) {
//				PAstStm d = (PAstStm) iterator.next();
//				d.apply(this);
//				print("\n");
//			}
//		}
//		
//		node.getAstFallthroughStm().apply(this);
//		print("\n");
//	}
	
	// ast_switch_case	------------------------------------------------------
//	@Override
//	public void caseADefaultAstSwitchCase(ADefaultAstSwitchCase node) {
//		print("default: ");
//	}
//	
//	@Override
//	public void caseACaseAstSwitchCase(ACaseAstSwitchCase node) {
//		print("case ");
//		LinkedList<?> exps = node.getAstExp();
//		if (!exps.isEmpty()) {
//			for (Iterator<?> iterator = exps.iterator(); iterator.hasNext();) {
//				PAstExp d = (PAstExp) iterator.next();
//				d.apply(this);
//				print(", ");
//			}
//		}
//		print(": ");
//	}
	
	// ast_literal	---------------------------------------------------
	// returns the type of PAstLiteral
	public Type forPAstLiteral(PAstLiteral node) {
		if (node instanceof AIntAstLiteral) {
			return Type.INT;
		} else if (node instanceof AFloatAstLiteral) {
			return Type.FLOAT64;
		} else if (node instanceof ARuneAstLiteral) {
			return Type.RUNE;
		} else if (node instanceof AStringAstLiteral) {
			return Type.STRING;
		} else if (node instanceof ABoolAstLiteral) {
			return Type.BOOL;
		}
		return null;
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
	
	// ast_binary_op	---------------------------------------------------
	// return the binary_op of PAstBinaryOp
	public String forPAstBinaryOp(PAstBinaryOp node) {
		if (node.getClass().isInstance(new AAddAstBinaryOp())) {
			return "+";
		} else if (node.getClass().isInstance(new ASubAstBinaryOp())) {
			return "-";
		} else if (node.getClass().isInstance(new AMulAstBinaryOp())) {
			return "*";
		} else if (node.getClass().isInstance(new ADivAstBinaryOp())) {
			return "/";
		} else if (node.getClass().isInstance(new AModAstBinaryOp())) {
			return "%";
		} else if (node.getClass().isInstance(new ABitorAstBinaryOp())) {
			return "|";
		} else if (node.getClass().isInstance(new ABitandAstBinaryOp())) {
			return "&";
		} else if (node.getClass().isInstance(new AEqAstBinaryOp())) {
			return "==";
		} else if (node.getClass().isInstance(new ANoteqAstBinaryOp())) {
			return "!=";
		} else if (node.getClass().isInstance(new ALtAstBinaryOp())) {
			return "<";
		} else if (node.getClass().isInstance(new ALeqAstBinaryOp())) {
			return "<=";
		} else if (node.getClass().isInstance(new AGtAstBinaryOp())) {
			return ">";
		} else if (node.getClass().isInstance(new AGeqAstBinaryOp())) {
			return ">=";
		} else if (node.getClass().isInstance(new ACaretAstBinaryOp())) {
			return "^";
		} else if (node.getClass().isInstance(new ALshiftAstBinaryOp())) {
			return "<<";
		} else if (node.getClass().isInstance(new ARshiftAstBinaryOp())) {
			return ">>";
		// TODO: might need to change according to caseABitclearAstBinaryOp
		} else if (node.getClass().isInstance(new ABitclearAstBinaryOp())) {
			return "&^";
		} else if (node.getClass().isInstance(new AOrAstBinaryOp())) {
			return "||";
		} else if (node.getClass().isInstance(new AAndAstBinaryOp())) {
			return "&&";
		}
		return null;
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
	
	// TODO: need to separate &^ to & and ^
//	@Override
//	public void caseABitclearAstBinaryOp(ABitclearAstBinaryOp node) {
//		print("&^");
//	}
	
	@Override
	public void caseAOrAstBinaryOp(AOrAstBinaryOp node) {
		print("||");
	}
	
	@Override
	public void caseAAndAstBinaryOp(AAndAstBinaryOp node) {
		print("&&");
	}
	
	// ast_op_assign	---------------------------------------------------
	// return op_assign of PAstOpAssign
	public String forPAstOpAssign(PAstOpAssign node) {
		if (node.getClass().isInstance(new AAddEqAstOpAssign())) {
			return "+=";
		} else if (node.getClass().isInstance(new ASubEqAstOpAssign())) {
			return "-=";
		} else if (node.getClass().isInstance(new AMulEqAstOpAssign())) {
			return "*=";
		} else if (node.getClass().isInstance(new ADivEqAstOpAssign())) {
			return "/=";
		} else if (node.getClass().isInstance(new AModEqAstOpAssign())) {
			return "%=";
		} else if (node.getClass().isInstance(new ABitorEqAstOpAssign())) {
			return "|=";
		} else if (node.getClass().isInstance(new ABitandEqAstOpAssign())) {
			return "&=";
		} else if (node.getClass().isInstance(new ACaretEqAstOpAssign())) {
			return "^=";
		} else if (node.getClass().isInstance(new ALshiftEqAstOpAssign())) {
			return "<<=";
		} else if (node.getClass().isInstance(new ARshiftEqAstOpAssign())) {
			return ">>=";
		} else if (node.getClass().isInstance(new ABitclearEqAstOpAssign())) {
			return "&^=";
		}
		return null;
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
	
//	@Override
//	public void caseABitclearEqAstOpAssign(ABitclearEqAstOpAssign node) {
//		print("&^=");
//	}
	
	// ast_unary_op		---------------------------------------------------
	// return the unary_op of PAstUnaryOp
	public String forPAstUnaryOp(PAstUnaryOp node) {
		if (node.getClass().isInstance(new APlusAstUnaryOp())) {
			return "+";
		} else if (node.getClass().isInstance(new AMinusAstUnaryOp())) {
			return "-";
		} else if (node.getClass().isInstance(new ANotAstUnaryOp())) {
			return "!";
		} else if (node.getClass().isInstance(new ACaretAstUnaryOp())) {
			return "^";
		}
		return null;
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
		print("~");
	}
	
	// ast_post_op	---------------------------------------------------
	// return the post_op of PAstPostOp
	public String forPAstPostOp(PAstPostOp node) {
		if (node.getClass().isInstance(new AIncAstPostOp())) {
			return "++";
		} else if (node.getClass().isInstance(new ADecAstPostOp())) {
			return "--";
		}
		return null;
	}
	
	@Override
	public void caseAIncAstPostOp(AIncAstPostOp node) {
		print("++");
	}
	
	@Override
	public void caseADecAstPostOp(ADecAstPostOp node) {
		print("--");
	}

	// ast_fallthrough_stm	---------------------------------------------------
	// return fallthrough_stm of PAstFallthroughStm
	public String forPAstFallthroughStm(PAstFallthroughStm node) {
		if (node.getClass().isInstance(new AAstFallthroughStm())) {
			return "fallthrough";
		}
		return null;
	}
	
	@Override
	public void caseAAstFallthroughStm(AAstFallthroughStm node) {
		print("fallthrough;");
	}
}
