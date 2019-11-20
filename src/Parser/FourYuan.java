package Parser;

import ElementType.*;
import Utils.DataStructure;
import Utils.DynamicException;
import Utils.Regex;
import Window.mainWindow;

import static Utils.DataStructure.*;
import java.util.Stack;

/**
 * @author knight
 */
public class FourYuan {
	//todo 写 局部变量的寻找方式

	public static int no = 0;/////指令序列

	//四元式的类
	public String oprator;//运算符
	public String op1;//第一个源操作数
	public String op2;//第二个源操作数
	public String des;//1⃣️存放中间变量（算术指令）2⃣️地址出口（跳转指令）

	public enum TokenType {
		CONST,
		REGISTER,
		VARIABLE,
		POSITIVE_INT,
	}

	private enum OPERATOR {
		PLUS,
		ADD,
		SUB,
	}

	ClassFactory cf = new ClassFactory();

	//识别正则表达式.
	//实现运算的autocast  避免运算过程中出现异常
	//todo 声明四元式: 强类型
	public void Exec() throws DynamicException.stopMachineException {
		try {
			if (this.oprator.contentEquals("+")) {
				Register[] registers = makeOpsRegister(this.op1, this.op2);
				Register register = registerOperation(registers[0], registers[1], OPERATOR.ADD);
				DataStructure.Registers.put(this.des, register);
			}
			else if(this.oprator.contentEquals("-")){
				Register[] registers = makeOpsRegister(this.op1,this.op2);
				Register register = registerOperation(registers[0],registers[1],OPERATOR.SUB);
				DataStructure.Registers.put(this.des,register);
			}
			else if (this.oprator.contentEquals("$")) {
				ArrayType array = checkAndSearchArray(this.op1);
				int offSet = getIndex(this.op2);

				switch (regexPat(this.des)) {
					case CONST:
						cf.setArrayElementValue(array, offSet, this.des);
						break;
					case REGISTER:
						Register reg = checkAndGetRegister(this.des);
						cf.setArrayElementValue(array, offSet, reg.getValue(), reg.getType());
						break;
					case VARIABLE:
						Word word = checkAndSearchWord(this.des);
						if(word.getValue()==null){
							throw new DynamicException().new unInitializedIdentifierException();
						}
						cf.setArrayElementValue(array, offSet, word.getValue(), word.type);
						break;
					default:
						throw new DynamicException().new defaultException("无法解析的符号");
				}
				//todo 类型check
			}
			else if (this.oprator.contentEquals("&")) {
				ArrayType array = checkAndSearchArray(this.op1);

				int offSet = getIndex(this.op2);
				//类型check 和 隐式类型转换
				Object value = array.getValue(offSet);
				ClassFactory.TYPE type = array.type;

				switch (regexPat(this.des)) {
					case REGISTER:
						Register reg;
						if(type== ClassFactory.TYPE.INT_ARRAY){
							reg = new Register<>(ClassFactory.TYPE.INT, (int)value);
						}else{
							reg = new Register<>(ClassFactory.TYPE.FLOAT, (float)value);
						}
						DataStructure.Registers.put(this.des, reg);
						break;
					case VARIABLE:
						Word word = checkAndSearchWord(this.des);
						if(type == ClassFactory.TYPE.INT_ARRAY){
							cf.setWordValue(word, value, ClassFactory.TYPE.INT);
						}else{
							cf.setWordValue(word, value, ClassFactory.TYPE.FLOAT);
						}
						break;
					default:
						throw new DynamicException().new defaultException("无法解析的目标地址");
				}
			}
			else if (this.oprator.contentEquals("*")) {
				Register[] registers = makeOpsRegister(this.op1, this.op2);
				Register register = registerOperation(registers[0], registers[1], OPERATOR.PLUS);
				DataStructure.Registers.put(this.des, register);
			}
			else if(this.oprator.contentEquals("dw")){
				//todo 形如(declare_word, value_or_null, type, name)
				checkWordIsExisted(this.des);
				switch (cf.getTypeFromStr(this.op2)) {
					case INT:
						Word<Integer> IWord = new Word<>(ClassFactory.TYPE.INT);
						IWord.setDes(Word.getDes_start());
						if(!"_".contentEquals(this.op1)){
							try{
								switch (regexPat(this.op1)){
									case CONST:
										IWord.setValue(Integer.parseInt(this.op1));
										break;
									case REGISTER:
										IWord.setValue((Integer) checkAndGetRegister(this.op1).getValue());
										break;
									case VARIABLE:
										IWord.setValue((Integer)checkAndSearchWord(this.op1).getValue());
										break;
								}
							}catch (NumberFormatException e){
								throw new DynamicException().new numberFormatException();
							}
						}
						saveWord(IWord);
						break;
					case FLOAT:
						Word<Float> FWord = new Word<>(ClassFactory.TYPE.FLOAT);
						FWord.setDes(Word.getDes_start());
						if(!"_".contentEquals(this.op1)){
							try{
								switch (regexPat(this.op1)){
									case CONST:
										FWord.setValue(Float.parseFloat(this.op1));
										break;
									case REGISTER:
										FWord.setValue((Float) checkAndGetRegister(this.op1).getValue());
										break;
									case VARIABLE:
										FWord.setValue((Float) checkAndSearchWord(this.op1).getValue());
										break;
								}
							}catch (NumberFormatException e){
								throw new DynamicException().new numberFormatException();
							}
						}
						saveWord(FWord);
						break;
					default:
						throw new DynamicException().new defaultException("错误的声明四元式");
				}
			}
			else if(this.oprator.contentEquals("da")){
				//todo 形如(declare_array, length, type, name)  length null?
				checkAndSearchArray(this.des);

				final int length = getIndex(this.op1);
				switch (cf.getTypeFromStr(this.op2)){
					case FLOAT:
						ArrayType<Float> FArray;
						try{
							FArray = new ArrayType<>(new Float[length], ClassFactory.TYPE.INT_ARRAY);
							FArray.setDes(Word.getDes_start());
							Word.setDes_start(length-1);
						}catch (NegativeArraySizeException e){
							throw new DynamicException().new illegalArratSizeException();
						}
						saveWord(FArray);
						break;
					case INT:
						ArrayType<Integer> IArray;
						try{
							IArray = new ArrayType<>(new Integer[length], ClassFactory.TYPE.INT_ARRAY);
							IArray.setDes(Word.getDes_start());
							Word.setDes_start(length-1);
						}catch (NegativeArraySizeException e){
							throw new DynamicException().new illegalArratSizeException();
						}
						saveWord(IArray);
						break;
						default:
							throw new DynamicException().new defaultException("四元式声明语句出错");
				}
			}
			else if(this.oprator.contentEquals("df")){
				//todo 形如(DeclardFunction_df, return type, name, function des);
				checkGlobalFieldIsExisted(this.op2);
				FuncSignature function = new FuncSignature(cf.getTypeFromStr(this.op1),Integer.parseInt(des));
				Functions.put(this.op2, function);
			}
			else if(this.oprator.contentEquals("dp")){
				//todo 形如(DeclardParam_dp, Param_Type ,_ ,function name );
				FuncSignature func = getFunction(this.des);
				//在function 中new 一个word
				func.addParam(cf.getTypeFromStr(this.op1));
			}
			else if(this.oprator.contentEquals("sp")){
				//todo 将形参添加到formalParam.
				//todo 形如(sp, op1=reg/vari/cons, _,functionName )
				//todo 当形参类型不匹配时，抛出异常
				FunctionType func = Env.peek();
				Word word = new Word();
				switch (regexPat(this.op1)){
					case CONST:
						try{
							switch (word.type){
								case FLOAT:
									// op1=int
									float f = Float.parseFloat(this.op1);
									word.setValue(f);
									word.type = ClassFactory.TYPE.INT;
									break;
								case INT:
									int i = Integer.parseInt(this.op1);
									word.setValue(i);
									word.type = ClassFactory.TYPE.FLOAT;
									break;
							}
						}catch (NumberFormatException e){
							throw new DynamicException().new dismatchFunctionParameter();
						}
						break;
					case REGISTER:
						Register r = DataStructure.Registers.get(this.op1);
						if(r.getType()==word.type){
							word.setValue(r.getValue());
							word.type = r.getType();
						}else if(r.getType()== ClassFactory.TYPE.INT&&word.type == ClassFactory.TYPE.FLOAT){
							word.setValue(r.getValue());
							word.type = r.getType();
						}else{
							throw new DynamicException().new dismatchFunctionParameter();
						}
						break;
					case VARIABLE:
						Word temp = checkAndSearchWord(this.op1);
						if(temp.type==word.type){
							word.type = temp.type;
							word.setValue(temp.getValue());
						}else if(temp.type == ClassFactory.TYPE.INT&&word.type == ClassFactory.TYPE.FLOAT){
							word.type = temp.type;
							word.setValue(temp.getValue());
						}else{
							throw new DynamicException().new dismatchFunctionParameter();
						}
						break;
				}
				func.addParam(word);
				func.getTop().push(word);
			}
			else if(this.oprator.contentEquals("cal")) {
				//todo 形如(call, _, name, _);
				//todo 填入return dest,
				//todo 跳转到函数语句.
				//todo 返回值寄存器需要保存在哪里？

				if ("main".equals(this.op2)) {
					inMain = true;

					FunctionType main = new FunctionType(MAIN);
					main.checkExecute();
					Env.push(main);
				} else {
					if("_".equals(this.op2)){
						//填完形参后 直接调用(cal, _, _, _)
						Top = Env.pop();
						Top.checkExecute();
						Top.setRet(mainWindow.j-1);
						mainWindow.j = Top.getEnterDes()-1;
					}else{
						//函数调用的起点调用:形如(call, _, name, _);
						FuncSignature signature = getFunction(this.op2);

						FunctionType function = new FunctionType(signature);

						Env.push(function);
					}
				}
			}
			else if(this.oprator.contentEquals("{")) {
				//todo 形如( {,_ , _, des=name or null)
				//todo  null 则不是函数  非null 要将formalParam的量push, 然后释放 remove All
				if("_".equals(this.des)) {
					//normal scope
					Top.GenerateScope();
				}else {
					//function scope
					if (!inMain) {
						//未进入主main函数
						FuncSignature func = getFunction(this.des);
						mainWindow.j = func.getExitDes() - 1;
					}
				}
			}
			else if(this.oprator.contentEquals("}")){
				//todo 形如( },_ , _, des=name or null)
				//todo  null 则不是函数  非null  然后根据name跳转.

				//清除此作用块的所有局部变量.
				if(!"_".equals(this.des)){
					mainWindow.j = Top.getRet();
					//函数栈的作用域释放
					Top = Env.pop();
				}else{
					//普通的作用域释放
					Top.outOfScope();
				}
			}
			else if (this.oprator.contentEquals("JMP")) {
				mainWindow.j = Integer.parseInt(des) - 1;
			}
			else if (this.oprator.contentEquals("=")) {
				//todo  golbal 赋值
				Word word = checkAndSearchWord(this.des);
				switch (regexPat(this.op1)) {
					case CONST:
						cf.setWordValue(word, this.op1);
						break;
					case REGISTER:
						Register register = checkAndGetRegister(this.op1);
						cf.setWordValue(word, register.getValue(), register.getType());
						break;
					case VARIABLE:
						Word temp = checkAndSearchWord(this.op1);
						if(temp.getValue()==null){
							throw new DynamicException().new unInitializedIdentifierException();
						}
						cf.setWordValue(word, temp.getValue(), temp.type);
						break;
				}
			}
			else if (this.oprator.contentEquals("J<")) {
				con_jmp("<");
			} else if (this.oprator.contentEquals("J>")) {
				con_jmp(">");
			} else if (this.oprator.equals("J<=")) {
				con_jmp("<=");
			} else if (this.oprator.equals("J>=")) {
				con_jmp(">=");
			} else if (this.oprator.equals("J==")) {
				con_jmp("==");
			} else if (this.oprator.equals("J!=")) {
				con_jmp("!=");
			}
		} catch (DynamicException.undeclaredIdentifierException e) {
			e.errorInfo();
		} catch (DynamicException.unInitializedIdentifierException e) {
			e.errorInfo();
		} catch (DynamicException.noArrayException e) {
			e.errorInfo();
		} catch (DynamicException.outOfArrayBoundException e) {
			e.errorInfo();
		} catch (DynamicException.mismatchOperatorException e) {
			e.errorInfo();
		} catch (DynamicException.numberFormatException e) {
			e.errorInfo();
		} catch (DynamicException.defaultException e) {
			e.errorInfo();
		} catch (DynamicException.redeclaredIdentifierException e) {
			e.errorInfo();
		} catch (DynamicException.dismatchFunctionParameter e) {
			e.errorInfo();
		} catch (DynamicException.unequalFunctionParameters e) {
			e.errorInfo();
		} catch (DynamicException.illegalArratSizeException e){
			e.errorInfo();
		}
	}


	private int getIndex(String str) throws DynamicException.defaultException,
			DynamicException.undeclaredIdentifierException, DynamicException.mismatchOperatorException,
			DynamicException.numberFormatException {
		switch (regexPatForIndex(str)) {
			case POSITIVE_INT:
				return Integer.parseInt(str);
			case REGISTER:
				Register r = checkAndGetRegister(str);
				//todo  寄存器放数组 放
				if(r.getType()== ClassFactory.TYPE.INT){
					return (int) r.getValue();
				}
				throw new DynamicException().new numberFormatException();
			//todo 如果此寄存器存的值是float or  则需要更改错误处理
			case VARIABLE:
				Word word = checkAndSearchWord(str);
				if(word.type== ClassFactory.TYPE.INT) {
					return (int) word.getValue();
				}
				throw new DynamicException().new numberFormatException();
			default:
				throw new DynamicException().new numberFormatException();
		}
	}

	private void con_jmp(String op) throws DynamicException.undeclaredIdentifierException,
			 DynamicException.defaultException,
			DynamicException.mismatchOperatorException {

		Register[] registers = makeOpsRegister(this.op1, this.op2);

		//(int)
		float sub;
		if(registerOperation(registers[0],registers[1],OPERATOR.SUB).getValue().getClass()==int.class){
			sub = (float) (int) registerOperation(registers[0], registers[1], OPERATOR.SUB).getValue();
		}
		else {
			sub = (float) registerOperation(registers[0], registers[1], OPERATOR.SUB).getValue();
		}

		switch (op) {
			case "<":
				if (sub < 0) {
					mainWindow.j = Integer.parseInt(this.des) - 1;
				}
				break;
			case ">":
				if (sub > 0) {
					mainWindow.j = Integer.parseInt(this.des) - 1;
				}
				break;
			case "<=":
				if (sub <= 0) {
					mainWindow.j = Integer.parseInt(this.des) - 1;
				}
				break;
			case ">=":
				if (sub >= 0) {
					mainWindow.j = Integer.parseInt(this.des) - 1;
				}
				break;
			case "==":
				if (Math.abs(sub) < 1e-7) {
					mainWindow.j = Integer.parseInt(this.des) - 1;
				}
				break;
			case "!=":
				if (Math.abs(sub) >= 1e-6) {
					mainWindow.j = Integer.parseInt(this.des) - 1;
				}
				break;
		}
	}

	private Register[] makeOpsRegister(String op1, String op2) throws DynamicException.defaultException
			, DynamicException.undeclaredIdentifierException,
			DynamicException.mismatchOperatorException {
		Register[] registers = new Register[2];
		if (regexPat(op1) == TokenType.CONST) {
			registers[0] = cf.newRegisterFromStr(op1);
		} else if (regexPat(op1) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			registers[0] = checkAndGetRegister(op1);
		} else if (regexPat(op1) == TokenType.VARIABLE) {
			Word word = checkAndSearchWord(op1);
			registers[0] = new Register<>(word.type,word.getValue());
		} else {
			throw new DynamicException().new defaultException("无法解析的符号");
		}

		if (regexPat(op2) == TokenType.CONST) {
			registers[1] = cf.newRegisterFromStr(op2);
		} else if (regexPat(op2) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			registers[1] = checkAndGetRegister(op2);
		} else if (regexPat(op2) == TokenType.VARIABLE) {
			Word word = checkAndSearchWord(op2);
			registers[1] = new Register<>(word.type,word.getValue());
		} else {
			throw new DynamicException().new defaultException("无法解析的操作符");
		}
		return registers;
	}



	//暂时没有考虑溢出
	private Register registerOperation(Register r_op1, Register r_op2, OPERATOR o) {
		switch (o){
			case ADD:
				if (r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.FLOAT) {
					float temp = (float) r_op1.getValue() + (float)r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				} else if(r_op1.getType() == ClassFactory.TYPE.INT && r_op2.getType() == ClassFactory.TYPE.FLOAT){
					float temp = (int) r_op1.getValue() + (float) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				}else if(r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.INT){
					float temp = (float) r_op1.getValue() + (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				}else{
					int temp = (int) r_op1.getValue() + (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.INT, temp);
				}
			case PLUS:
				if (r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.FLOAT) {
					float temp = (float) r_op1.getValue() * (float)r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				} else if(r_op1.getType() == ClassFactory.TYPE.INT && r_op2.getType() == ClassFactory.TYPE.FLOAT){
					float temp = (int) r_op1.getValue() * (float) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				}else if(r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.INT){
					float temp = (float) r_op1.getValue() * (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				}else{
					int temp = (int) r_op1.getValue() * (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.INT, temp);
				}
			case SUB:
				if (r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.FLOAT) {
					float temp = (float) r_op1.getValue() - (float)r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				} else if(r_op1.getType() == ClassFactory.TYPE.INT && r_op2.getType() == ClassFactory.TYPE.FLOAT){
					float temp = (int) r_op1.getValue() - (float) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				}else if(r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.INT){
					float temp = (float) r_op1.getValue() - (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.FLOAT, temp);
				}else{
					int temp = (int) r_op1.getValue() - (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.INT, temp);
				}
		}
		throw new DynamicException().new defaultException("找不到匹配的操作符");

	}

	public String get_four_str() {
		return "(" + this.oprator + "," + this.op1 + "," + this.op2 + "," + this.des + ")";
	}

	public  TokenType regexPat(String str) throws DynamicException.defaultException {
		if (str.indexOf(Regex.regPat)==0) {
			return TokenType.REGISTER;
		} else if (str.matches(Regex.variPat)) {
			return TokenType.VARIABLE;
		} else if (str.matches(Regex.constant)) {
			return TokenType.CONST;
		}
		throw new DynamicException().new defaultException("无法解析的操作符");
	}

	public  TokenType regexPatForIndex(String str) throws DynamicException.defaultException {
		if (str.indexOf(Regex.regPat)==0) {
			return TokenType.REGISTER;
		} else if(str.matches(Regex.positiveInt)) {
			return TokenType.POSITIVE_INT;
		} else if (str.matches(Regex.variPat)) {
			return TokenType.VARIABLE;
		}
		throw new DynamicException().new defaultException("无法解析的操作符");
	}

	private FuncSignature getFunction(String str) throws DynamicException.undeclaredIdentifierException {
		FuncSignature sig = Functions.getOrDefault(str, null);
		if(sig==null){
			throw new DynamicException().new undeclaredIdentifierException();
		}
		return sig;
	}

	private void checkGlobalFieldIsExisted(String name) throws DynamicException.redeclaredIdentifierException {
		if(Datas.getOrDefault(name,null)!=null||Functions.getOrDefault(name,null)!=null){
			throw new DynamicException().new redeclaredIdentifierException();
		}
	}

	private void checkWordIsExisted(String name) throws DynamicException.redeclaredIdentifierException {
		//全局变量
		if(Top ==null){
			if(Functions.getOrDefault(name, null)!=null||Datas.getOrDefault(name,null)!=null){
				throw new DynamicException().new redeclaredIdentifierException();
			}
		}else{
			Stack stack = Top.getScope();
			for(Object iter:stack) {
				Stack eachStack = (Stack)iter;
				for (Object o : eachStack) {
					Word word = (Word)o;
					if (name.equals(word.getName())) {
						throw new DynamicException().new redeclaredIdentifierException();

					}
				}
			}
		}
	}

	private Register checkAndGetRegister(String des) throws DynamicException.defaultException {
		Register reg = Registers.getOrDefault(des, null);
		if (reg == null) {
			throw new DynamicException().new defaultException("找不到寄存器");
		}
		return reg;
	}

	private Word checkAndSearchField(String name) {
		Word pick = Datas.getOrDefault(name, null);

		if (Top != null) {
			Stack stack = Top.getScope();
			for (Object object : stack) {
				Stack iter = (Stack) object;
				for (Object o : iter) {
					Word word = (Word) o;
					if (word.getName().equals(name)) {
						pick = word;
					}
				}
			}
		}
		return pick;
	}

	public Word checkAndSearchWord(String name) throws DynamicException.undeclaredIdentifierException, DynamicException.mismatchOperatorException {
		Word pick = checkAndSearchField(name);

		if(pick==null){
			throw new DynamicException().new undeclaredIdentifierException();
		}

		if (pick.getClass() != Word.class) {
			throw new DynamicException().new mismatchOperatorException();
		}
		return pick;
	}

	public ArrayType checkAndSearchArray(String name) throws DynamicException.noArrayException {
		Word pick = checkAndSearchField(name);

		if (pick == null) {
			throw new DynamicException().new noArrayException();
		}

		if (pick.getClass() != ArrayType.class) {
			throw new DynamicException().new noArrayException();
		}
		return (ArrayType) pick;
	}

	public void saveWord(Word word){
		if(Top ==null){
			Datas.put(word.getName(),word);
		}else{
			Top.getScope().peek().push(word);
		}
	}
}
