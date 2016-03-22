package goplusplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.sun.org.apache.xpath.internal.functions.Function;

import java.util.Iterator;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;
import type.*;

public class Typechecker extends DepthFirstAdapter{
	private LinkedList<HashMap<String, Type>> symbolTable;
	
	public Typechecker() {
		symbolTable = new LinkedList<HashMap<String, Type> >();
		symbolTable.addFirst(new HashMap<String, Type>());
	}
	
	public void check(Node node) {
		node.apply(this);
	}
	
	// print the symbol table to the console
	public void printSymbolTable() {
		for (int i = 0; i < symbolTable.size(); i++) {
			HashMap<String, Type> temp = symbolTable.get(i);
			System.out.println("Layer " + i + ":");
			for (HashMap.Entry<String, Type> entry : temp.entrySet()) {
				System.out.println("Key = " + entry.getKey() + ", Type = " + entry.getValue());
			}
		}
	}
	
	// ast_program		---------------------------------------------------
	@Override
	public void caseAAstProgram(AAstProgram node) {
		LinkedList<PAstDecl> decl = node.getDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstDecl> iterator = decl.iterator(); iterator.hasNext();) {
				PAstDecl d = (PAstDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	// ast_decl			---------------------------------------------------
	@Override
	public void caseAVarDecAstDecl(AVarDecAstDecl node) {
		LinkedList<PAstVarDecl> decl = node.getAstVarDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstVarDecl> iterator = decl.iterator(); iterator.hasNext();) {
				PAstVarDecl d = (PAstVarDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseATypeDecAstDecl(ATypeDecAstDecl node) {
		LinkedList<PAstTypeDecl> decl = node.getAstTypeDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstTypeDecl> iterator = decl.iterator(); iterator.hasNext();) {
				PAstTypeDecl d = (PAstTypeDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseAFuncDecAstDecl(AFuncDecAstDecl node) {
		node.getAstFuncDecl().apply(this);
	}
	
	// ast_var_decl		---------------------------------------------------
	@Override
	public void caseATypeAstVarDecl(ATypeAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAExpAstVarDecl(AExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		LinkedList<PAstExp> exps = node.getAstExp();
		
//		if (idlist.size() != exps.size()) {
//			printSymbolTable();
//			String errorMsg = "Declaration Error at line " + idlist.getFirst().getLine();
//			throw new TypeException(errorMsg);
//		}
		
		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseATypeExpAstVarDecl(ATypeExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type typeExp = forPAstTypeExp(node.getAstTypeExp());
		LinkedList<PAstExp> exps = node.getAstExp();
		
//		if (idlist.size() != exps.size()) {
//			printSymbolTable();
//			String errorMsg = "Declaration Error at line " + idlist.getFirst().getLine();
//			throw new TypeException(errorMsg);
//		}
		
		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			if (!varType.assign(typeExp)) {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	// ast_type_decl	---------------------------------------------------
	@Override
	public void caseAAstTypeDecl(AAstTypeDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
			
			// for the fields of a struct
			if (varType.toString().equals("struct")) {
				AStructAstTypeExp tempNode = (AStructAstTypeExp) node.getAstTypeExp();
				LinkedList<PAstStructField> fieldList = tempNode.getAstStructField();
				for (Iterator<PAstStructField> iter = fieldList.iterator(); iter.hasNext();) {
					AAstStructField tempField = (AAstStructField) iter.next();
					LinkedList<TId> fieldIdList = tempField.getId();
					for (Iterator<TId> fieldIter = fieldIdList.iterator(); fieldIter.hasNext();) {
						TId field = (TId) fieldIter.next();
						String fieldName = d.getText().trim() + "." + field.toString().trim();
						Type fieldType = forPAstTypeExp(tempField.getAstTypeExp());
						if (!symbolTable.getFirst().containsKey(fieldName)) {
							symbolTable.getFirst().put(fieldName, fieldType);
						} else {
							printSymbolTable();
							String errorMsg = "Struct Declaration Error at line " + field.getLine();
							throw new TypeException(errorMsg);
						}
					}
				}
			}
		}
	}
	
	// ast_func_decl	---------------------------------------------------
	@Override
	public void caseAAstFuncDecl(AAstFuncDecl node) {
		FunctionType funcType = new FunctionType();
		Type returnType;
		if (!symbolTable.getFirst().containsKey(node.getId().getText().trim())) {
			if (node.getAstTypeExp() != null) {
				returnType = forPAstTypeExp(node.getAstTypeExp());
			} else {
				returnType = Type.VOID;
			}
		} else {
			printSymbolTable();
			String errorMsg = "Function Declaration Error at line " + node.getId().getLine();
			throw new TypeException(errorMsg);
		}
		
		funcType.returnType = returnType;
		
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
		
		LinkedList<PAstFuncParam> params = node.getAstFuncParam();
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		for (Iterator<PAstFuncParam> iterator = params.iterator(); iterator.hasNext();) {
			PAstFuncParam param = (PAstFuncParam) iterator.next();
			Type pType = forPAstFuncParam(param);
			paramTypes.add(pType);
			param.apply(this);
		}
		
		funcType.paramType = paramTypes;
		
		symbolTable.getFirst().put(node.getId().getText().trim(), funcType);
		
		LinkedList<PAstStm> stmts = node.getAstStm();
		for (Iterator<PAstStm> iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
		
		symbolTable.removeFirst();
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
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Function Parameter Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
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
			arrayType.size = Integer.parseInt(temp.getSize().toString());
			return arrayType;
		} else if (node.getClass().isInstance(new AStructAstTypeExp())) {
			AStructAstTypeExp temp = (AStructAstTypeExp) node;
			LinkedList<PAstStructField> structList = temp.getAstStructField();
			ArrayList<Type> typesList = new ArrayList<Type>();
			for (Iterator<PAstStructField> iterator = structList.iterator(); iterator.hasNext();) {
				AAstStructField field = (AAstStructField) iterator.next();
				Type eleType = forPAstTypeExp(field.getAstTypeExp());
				typesList.add(eleType);
			}
			StructType structType = new StructType();
			structType.elementTypes = typesList;
			return structType;
		} else if (node.getClass().isInstance(new AAliasAstTypeExp())) {
			AAliasAstTypeExp temp = (AAliasAstTypeExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					return symbolTable.get(i).get(temp.getId().getText().trim());
				}
			}
			printSymbolTable();
			System.out.println("In forPAstTypeExp");
			String errorMsg = "Declaration Error at line " + temp.getId().getLine();
			throw new TypeException(errorMsg);
		}
		return null;
	}
	
//	@Override
//	public void caseABasicAstTypeExp(ABasicAstTypeExp node) {
//		print(node.getBasicTypes().getText().trim());
//	}
//	
//	@Override
//	public void caseASliceAstTypeExp(ASliceAstTypeExp node) {
//		print("[]");
//		node.getAstTypeExp().apply(this);
//	}
//	
//	@Override
//	public void caseAArrayAstTypeExp(AArrayAstTypeExp node) {
//		print("["+node.getSize().getText().trim()+"]");
//		node.getAstTypeExp().apply(this);
//	}
//	
//	@Override
//	public void caseAStructAstTypeExp(AStructAstTypeExp node) {
//		LinkedList fields = node.getAstStructField();
//		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
//			PAstStructField field = (PAstStructField) iterator.next();
//			field.apply(this);
//		}
//	}
//	
//	@Override
//	public void caseAAliasAstTypeExp(AAliasAstTypeExp node) {
//		print(node.getId().getText().trim());
//	}
//	
	
	// ast_struct_field ---------------------------------------------------
//	@Override
//	public void caseAAstStructField(AAstStructField node) {
//		LinkedList<TId> idlist = node.getId();
//		Type varType = forPAstTypeExp(node.getAstTypeExp());
//		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
//			TId d = (TId) iterator.next();
//			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
//				symbolTable.getFirst().put(d.getText().trim(), varType);
//			} else {
//				printSymbolTable();
//				String errorMsg = "Declaration Error at line " + d.getLine();
//				throw new TypeException(errorMsg);
//			}
//		}
//	}
	
	// ast_stm			---------------------------------------------------
	public void casePAstStm(PAstStm node) {
		if (node.getClass().isInstance(new AEmptyAstStm())) {
			AEmptyAstStm temp = (AEmptyAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AExpAstStm())) {
			AExpAstStm temp = (AExpAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AAssignAstStm())) {
			AAssignAstStm temp = (AAssignAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AOpAssignAstStm())) {
			AOpAssignAstStm temp = (AOpAssignAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AVarDeclAstStm())) {
			AVarDeclAstStm temp = (AVarDeclAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AShortDeclAstStm())) {
			AShortDeclAstStm temp = (AShortDeclAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new ATypeDeclAstStm())) {
			ATypeDeclAstStm temp = (ATypeDeclAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AIncDecAstStm())) {
			AIncDecAstStm temp = (AIncDecAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new APrintAstStm())) {
			APrintAstStm temp = (APrintAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new APrintlnAstStm())) {
			APrintlnAstStm temp = (APrintlnAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AReturnAstStm())) {
			AReturnAstStm temp = (AReturnAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AShortifAstStm())) {
			AShortifAstStm temp = (AShortifAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new ALongifAstStm())) {
			ALongifAstStm temp = (ALongifAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new ASwitchAstStm())) {
			ASwitchAstStm temp = (ASwitchAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AForAstStm())) {
			AForAstStm temp = (AForAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new ABlockAstStm())) {
			ABlockAstStm temp = (ABlockAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new ABreakAstStm())) {
			ABreakAstStm temp = (ABreakAstStm) node;
			temp.apply(this);
		} else if (node.getClass().isInstance(new AContinueAstStm())) {
			AContinueAstStm temp = (AContinueAstStm) node;
			temp.apply(this);
		}
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
		AAssignAstStm temp = (AAssignAstStm) node;
		LinkedList<PAstExp> leftList = temp.getLval();
		LinkedList<PAstExp> rightList = temp.getRval();
		
		for (int i = 0; i < leftList.size(); i++) {
			Type leftType = forPAstExp(leftList.get(i));
			Type rightType = forPAstExp(rightList.get(i));
			if (!leftType.is(rightType)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				String errorMsg = "Assignment Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAOpAssignAstStm(AOpAssignAstStm node) {
		TId d = node.getL();
		Type varType = forPAstExp(node.getR());
		if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
			symbolTable.getFirst().put(d.getText().trim(), varType);
		} else {
			printSymbolTable();
			String errorMsg = "Assignment Error at line " + d.getLine();
			throw new TypeException(errorMsg);
		}
	}
	
	@Override
	public void caseAVarDeclAstStm(AVarDeclAstStm node) {
		LinkedList<PAstVarDecl> decls = node.getAstVarDecl();
		for (Iterator<PAstVarDecl> iterator = decls.iterator(); iterator.hasNext();) {
			PAstVarDecl exp = (PAstVarDecl) iterator.next();
			exp.apply(this);
		}
	}
	
	@Override
	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
		AShortDeclAstStm temp = (AShortDeclAstStm) node;
		LinkedList<PAstExp> leftList = temp.getIds();
		LinkedList<PAstExp> rightList = temp.getAstExp();
		
		for (int i = 0; i < leftList.size(); i++) {
			Type leftType = forPAstExp(leftList.get(i));
			Type rightType = forPAstExp(rightList.get(i));
			if (!leftType.is(rightType)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				String errorMsg = "Declaration Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseATypeDeclAstStm(ATypeDeclAstStm node) {
		LinkedList<PAstTypeDecl> decls = node.getAstTypeDecl();
		for (Iterator<PAstTypeDecl> iterator = decls.iterator(); iterator.hasNext();) {
			PAstTypeDecl exp = (PAstTypeDecl) iterator.next();
			exp.apply(this);
		}
	}
	
	@Override
	public void caseAIncDecAstStm(AIncDecAstStm node) {
		String temp = forPAstPostOp(node.getAstPostOp());
		if (temp.equals("++") || temp.equals("--")) {
			Type t = forPAstExp(node.getAstExp());
			if (!t.is(Type.INT)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(node);
				String errorMsg = "Post Increment/Decrement Error at line " + pos.getLine(node);
				throw new TypeException(errorMsg);
			}
		} else {
			printSymbolTable();
			Position pos = new Position();
			pos.defaultCase(node);
			String errorMsg = "Post Increment/Decrement Error at line " + pos.getLine(node);
			throw new TypeException(errorMsg);
		}
	}
	
	@Override
	public void caseAPrintAstStm(APrintAstStm node) {
		LinkedList<PAstExp> exps = node.getAstExp();
		for (Iterator<PAstExp> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			if (!typeCheckPAstExp(exp)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(exp);
				String errorMsg = "Print Error at line " + pos.getLine(exp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAPrintlnAstStm(APrintlnAstStm node) {
		LinkedList<PAstExp> exps = node.getAstExp();
		for (Iterator<PAstExp> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			if (!typeCheckPAstExp(exp)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(exp);
				String errorMsg = "Println Error at line " + pos.getLine(exp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAReturnAstStm(AReturnAstStm node) {
		if (node.getAstExp() != null) {
			if (!typeCheckPAstExp(node.getAstExp())) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(node.getAstExp());
				String errorMsg = "Return Error at line " + pos.getLine(node.getAstExp());
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAShortifAstStm(AShortifAstStm node) {
		if (node.getInit() != null) {
			node.getInit().apply(this);
		}
		
		Type condType = forPAstExp(node.getCondition());
		if (!condType.is(Type.BOOL)) {
			printSymbolTable();
			Position pos = new Position();
			pos.defaultCase(node.getCondition());
			String errorMsg = "If loop Condition Error at line " + pos.getLine(node.getCondition());
			throw new TypeException(errorMsg);
		}
		
		LinkedList<PAstStm> stmts = node.getAstStm();
		for (Iterator<PAstStm> iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
	}
	
	@Override
	public void caseALongifAstStm(ALongifAstStm node) {
		if (node.getInit() != null) {
			node.getInit().apply(this);
		}
		
		Type condType = forPAstExp(node.getCondition());
		if (!condType.is(Type.BOOL)) {
			printSymbolTable();
			Position pos = new Position();
			pos.defaultCase(node.getCondition());
			String errorMsg = "If loop Condition Error at line " + pos.getLine(node.getCondition());
			throw new TypeException(errorMsg);
		}
		
		LinkedList<PAstStm> if_stmts = node.getIfStms();
		for (Iterator<PAstStm> iterator = if_stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
		
		LinkedList<PAstStm> else_stmts = node.getElseStms();
		for (Iterator<PAstStm> iterator = else_stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
	}
	
	// TODO: implement the method below
//	@Override
//	public void caseASwitchAstStm(ASwitchAstStm node) {
//		if (node.getAstStm() != null) {
//			node.getAstStm().apply(this);
//		}
//		
//		Type expType;
//		if (node.getAstExp() != null) {
//			expType = forPAstExp(node.getAstExp());
//		} else {
//			expType = Type.VOID;
//		}
//		
//		LinkedList<PAstSwitchStm> stms = node.getAstSwitchStm();
//		for (Iterator<PAstSwitchStm> iterator = stms.iterator(); iterator.hasNext();) {
//			PAstSwitchStm stm = (PAstSwitchStm) iterator.next();
//			stm.apply(this);
//		}
//	}
	
	@Override
	public void caseAForAstStm(AForAstStm node) {
		if (node.getInit() != null) {
			node.getInit().apply(this);
		}
		
		if (node.getCondition() != null) {
			Type condType = forPAstExp(node.getCondition());
			if (!condType.is(Type.BOOL)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(node.getCondition());
				String errorMsg = "For loop Condition Error at line " + pos.getLine(node.getCondition());
				throw new TypeException(errorMsg);
			}
		}
		
		if (node.getPost() != null) {
			node.getPost().apply(this);
		}
		
		LinkedList<PAstStm> stmts = node.getBody();
		for (Iterator<PAstStm> iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
	}
	
	@Override
	public void caseABlockAstStm(ABlockAstStm node) {
		
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
		
		LinkedList<PAstStm> stmts = node.getAstStm();
		for (Iterator<PAstStm> iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
		
		symbolTable.removeFirst();
	}
	
	@Override
	public void caseABreakAstStm(ABreakAstStm node) {
		// do nothing
	}
	
	@Override
	public void caseAContinueAstStm(AContinueAstStm node) {
		// do nothing
	}
	
	// ast_exp			---------------------------------------------------
	public boolean typeCheckPAstExp(PAstExp node) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			typeCheckPAstExp(temp);
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					return true;
				}
			}
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			return true;
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			// just to check that forPAstExp(temp) does not give an error msg
			forPAstExp(temp);
			return true;
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			// just to check that forPAstExp(temp) does not give an error msg
			forPAstExp(temp);
			return true;
		// TODO: implement the methods below
		} else if (node.getClass().isInstance(new AFuncCallAstExp())) {
//			AFuncCallAstExp temp = (AFuncCallAstExp) node;
//			
//			HashMap<String, Type> firstMap = symbolTable.getFirst();
//			if (firstMap.containsKey(temp.getName()))
//			LinkedList funcParam = ;
//			
//			LinkedList<PAstExp> argList = temp.getArgs();
//			for (Iterator<PAstExp> iterator = argList.iterator(); iterator.hasNext();) {
//				Type argType = forPAstExp(iterator.next());
//				
//			}
		} else if (node.getClass().isInstance(new AAppendAstExp())) {
			
		} else if (node.getClass().isInstance(new ABasicCastAstExp())) {
			
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			
		}
		return false;
	}
	
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
			printSymbolTable();
			System.out.println("In forPAstExp");
			String errorMsg = "Declaration Error at line " + temp.getId().getLine();
			throw new TypeException(errorMsg);
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			ALitAstExp temp = (ALitAstExp) node;
			return forPAstLiteral(temp.getAstLiteral());
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			String unop = forPAstUnaryOp(temp.getAstUnaryOp());
			if (unop.equals("+")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.INT) || check.is(Type.FLOAT64) || check.is(Type.RUNE))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("-")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.INT) || check.is(Type.FLOAT64) || check.is(Type.RUNE))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("!")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.BOOL))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("^")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.INT) || check.is(Type.RUNE))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			Type leftType = forPAstExp(temp.getLeft());
			String binOp = forPAstBinaryOp(temp.getAstBinaryOp());
			Type rightType = forPAstExp(temp.getRight());
			
			if (leftType.is(rightType) && leftType.is(Type.BOOL)) {
				if (binOp.equals("||")) {
					return Type.BOOL;
				} else if (binOp.equals("&&")) {
					return Type.BOOL;
				}
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else if (leftType.is(rightType) && leftType.is(Type.STRING)) {
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
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else if (leftType.is(rightType) && leftType.is(Type.FLOAT64)) {
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
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else if (leftType.is(rightType) && leftType.is(Type.INT)) {
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
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		} else if (node.getClass().isInstance(new AFuncCallAstExp())) {
			AFuncCallAstExp temp = (AFuncCallAstExp)node;
			
			Type type = forPAstExp(temp.getName());
			if (type.is(Type.FUNC)) {
				FunctionType funcType = (FunctionType)type;
				LinkedList<PAstExp> args = temp.getArgs();
				ArrayList<Type> params = funcType.paramType;
				boolean match = true;
				if(args.size() == params.size()) {
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
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Type error at line " + pos.getLine(temp) + " : Function parameters do not match";
				throw new TypeException(errorMsg);
			}
			printSymbolTable();
			Position pos = new Position();
			pos.defaultCase(temp);
			System.out.println("In forPAstExp");
			String errorMsg = "Type error at line " + pos.getLine(temp) + " : Not a function";
			throw new TypeException(errorMsg);
		} else if (node.getClass().isInstance(new AAppendAstExp())) {
			AAppendAstExp temp = (AAppendAstExp)node;
			String id = temp.getId().getText().trim();
			for (Iterator iterator = symbolTable.iterator(); iterator.hasNext();) {
				HashMap<String, Type> table = (HashMap<String, Type>) iterator.next();
				if (table.containsKey(id)) {
					return forPAstExp(temp.getAstExp());
				}
			}
			
			System.out.println("In forPAstExp");
			String errorMsg = "Type error at line " + temp.getId().getLine() + " : Identifier " + id + " undeclared";
			throw new TypeException(errorMsg);
		} else if (node.getClass().isInstance(new ABasicCastAstExp())) {
			ABasicCastAstExp temp = (ABasicCastAstExp)node;
			Type basic = forPAstTypeExp(new ABasicAstTypeExp(temp.getBasicTypes()));
			Type other = forPAstExp(temp.getAstExp());
			if (basic.is(Type.INT)) {
				//TODO find out casting rules
			} else if (basic.is(Type.FLOAT64)) {
				
			} else if (basic.is(Type.BOOL)) {
				
			} else if (basic.is(Type.RUNE)) {
				
			} else if (basic.is(Type.STRING)) {
				
			} else {
				System.out.println("In forPAstExp");
				String errorMsg = "Type error at line " + temp.getBasicTypes().getLine() + " : Type " + other + " cannot be casted to " + basic;
				throw new TypeException(errorMsg);
			}
			//TODO finish the following cases
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			
		}
		return null;
	}
	
	// ast_switch_stm	---------------------------------------------------
//	@Override
//	public void caseAAstSwitchStm(AAstSwitchStm node) {
//		node.getAstSwitchCase().apply(this);
//		
//		LinkedList stms = node.getAstStm();
//		if (!stms.isEmpty()) {
//			for (Iterator iterator = stms.iterator(); iterator.hasNext();) {
//				PAstStm d = (PAstStm) iterator.next();
//				d.apply(this);
//				print("\n");
//			}
//		}
//		
//		node.getAstFallthroughStm().apply(this);
//		print("\n");
//	}
	
	// ast_switch_case	---------------------------------------------------
//	@Override
//	public void caseADefaultAstSwitchCase(ADefaultAstSwitchCase node) {
//		print("default: ");
//	}
//	
//	@Override
//	public void caseACaseAstSwitchCase(ACaseAstSwitchCase node) {
//		print("case ");
//		LinkedList exps = node.getAstExp();
//		if (!exps.isEmpty()) {
//			for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
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
			return "!+";
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
		} else if (node.getClass().isInstance(new ABitclearAstBinaryOp())) {
			return "&^";
		} else if (node.getClass().isInstance(new AOrAstBinaryOp())) {
			return "||";
		} else if (node.getClass().isInstance(new AAndAstBinaryOp())) {
			return "&&";
		}
		return null;
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
			return "^=";
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
	
	// ast_fallthrough_stm	---------------------------------------------------
	// return fallthrough_stm of PAstFallthroughStm
	public String forPAstFallthroughStm(PAstFallthroughStm node) {
		if (node.getClass().isInstance(new AAstFallthroughStm())) {
			return "fallthrough";
		}
		return null;
	}
}