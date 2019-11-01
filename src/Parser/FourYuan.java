package Parser;

import Window.mainWindow;

/**
 * @author knight
 */
public class FourYuan {
	//todo when : int+float = float, no exception

	public static int no = 0;/////指令序列

	//四元式的类
	public String oprator;//运算符
	public String op1;//第一个源操作数
	public String op2;//第二个源操作数
	public String des;//1⃣️存放中间变量（算术指令）2⃣️地址出口（跳转指令）

	private final int FLOAT = 1;
	private final int INT = 0;

	public enum TokenType {
		CONST,
		REGISTER,
		VARIABLE,
		POSITIVE_INT,
	}

	private enum OPERATOR{
		PLUS,
		ADD,
	}

	ClassFactory cf = new ClassFactory();

	//识别正则表达式.
	private String _int = "^[+/-]?[0-9]*$";
	private String _float = "[0-9]+\\.?[0-9]+";

	private String constant = "^[+/-]?[0-9]*$|[0-9]+\\.?[0-9]+";
	private String regPat = "reg";
	private String variPat = "^[A-Za-z_][A-Za-z0-9_]*$";
	private String positiveInt = "^(0|[1-9][0-9]*)$";



	//实现运算的autocast  避免运算过程中出现异常

	///TODO   加法运算
	///TODO   在单词表中找到第一个操作数（按照op1的名）
	///TODO    在单词表中找到第二个操作数（按照op2的名）
	///TODO    运算
	///TODO    保存到寄存器
	public void Exec() throws DynamicException.stopMachineException {
		try{
			if (this.oprator.contentEquals("+")) {
					Register[] registers = makeOpsRegister();
					registerOperation(registers[0], registers[1], OPERATOR.ADD);
			} else if (this.oprator.contentEquals("$")) {
					ArrayType array = checkAndGetArrayFromHash(this.op1);
					//todo 怎样停下程序？当返回-1;
					int offSet = getIndex(this.op2);
					int value;
					switch (regexPat(this.des)) {
						case CONST:
							value = Integer.parseInt(this.des);
							break;
						case REGISTER:
							Register reg = checkAndGetRegister(this.des);
							value = (int) reg.getValue();
							break;
						case VARIABLE:
							Object o = checkAndGetValueFromWord(this.des);
							value = (int) o;
							break;
						default:
							throw new DynamicException().new defaultException("无法解析的符号");
					}
					//todo 类型check
					array.setValue(offSet, value);

			} else if (this.oprator.contentEquals("&")) {
					ArrayType array = checkAndGetArrayFromHash(this.op1);

					int offSet = getIndex(this.op2);
					//类型check 和 隐式类型转换
					Object value = array.getValue(offSet);
					switch (regexPat(this.des)) {
						case REGISTER:
							Register reg = cf.newRegister(value.toString());
							ClassFactory.Registers.put(this.des, reg);
							break;
						case VARIABLE:
							Word word = checkAndGetWord(this.des);
							cf.setWordValue(word,value.toString());
							break;
						default:
							throw new DynamicException().new defaultException("无法解析的目标地址");
					}

			} else if (this.oprator.contentEquals("*")) {
					Register[] registers = makeOpsRegister();
					registerOperation(registers[0], registers[1], OPERATOR.PLUS);

				/////TODO 乘法运算
			} else if (this.oprator.contentEquals("JMP")) {
				/////TODO 直接跳转
				mainWindow.j = Integer.parseInt(des) - 1;
			} else if (this.oprator.contentEquals("=")) {
				String value = "";
					Word word = checkAndGetWord(this.des);
					switch (regexPat(this.op1)) {
						case CONST:
							value = this.op1;
							break;
						case REGISTER:
							value = checkAndGetRegister(this.op1).getValue().toString();
							break;
						case VARIABLE:
							value = checkAndGetValueFromWord(this.op1).toString();
							break;
					}
				cf.setWordValue(word, value);
			} else if (this.oprator.contentEquals("J<"))
				con_jmp("<");

			else if (this.oprator.contentEquals("J>"))
				con_jmp(">");

			else if (this.oprator.equals("J<="))
				con_jmp("<=");
			else if (this.oprator.equals("J>="))
				con_jmp(">=");
			else if (this.oprator.equals("J=="))
				con_jmp("==");
			else if (this.oprator.equals("J!="))
				con_jmp("!=");
		} catch (DynamicException.undeclaredIdentifierException e) {
			e.errorInfo();
		}  catch (DynamicException.unInitializedIdentifierException e) {
			e.errorInfo();
		} catch (DynamicException.noArrayException e) {
			e.errorInfo();
		} catch (DynamicException.outOfArrayBoundException e) {
			e.errorInfo();
		} catch (DynamicException.mismatchOperatorException e) {
			e.errorInfo();
		} catch (DynamicException.defaultException e) {
			e.errorInfo();
		}
	}


	private int getIndex(String str) throws DynamicException.defaultException,
			DynamicException.undeclaredIdentifierException, DynamicException.unInitializedIdentifierException,
			DynamicException.mismatchOperatorException {
		switch (regexPatForIndex(str)) {
			case POSITIVE_INT:
				return Integer.parseInt(str);
			case REGISTER:
				Register r = checkAndGetRegister(str);
				return (int) r.getValue();
				//todo 如果此寄存器存的值是float or  则需要更改错误处理
			case VARIABLE:
				Object v = checkAndGetValueFromWord(str);
				return (int) v;
			default:
				return -1;
		}
	}

	private void con_jmp(String op) throws DynamicException.undeclaredIdentifierException,
			DynamicException.unInitializedIdentifierException, DynamicException.defaultException,
			DynamicException.mismatchOperatorException {

		Register [] registers = makeOpsRegister();

		int op1_val = (Integer)registers[0].getValue();
		int op2_val = (Integer)registers[1].getValue();
		switch (op){
			case "<":
				if (op1_val < op2_val)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case ">":
				if (op1_val > op2_val)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case "<=":
				if (op1_val <= op2_val)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case ">=":
				if (op1_val >= op2_val)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case "==":
				if (op1_val == op2_val)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case "!=":
				if (op1_val != op2_val)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
		}
	}

	private Register[] makeOpsRegister() throws DynamicException.defaultException
			, DynamicException.undeclaredIdentifierException, DynamicException.unInitializedIdentifierException,
			DynamicException.mismatchOperatorException {
		Register[] registers = new Register[2];
		if (regexPat(this.op1) == TokenType.CONST) {
			registers[0] = cf.newRegister(this.op1);
		} else if (regexPat(this.op1) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			String str = ClassFactory.Registers.get(this.op1).getValue().toString();
			registers[0] = cf.newRegister(str);
		} else if (regexPat(this.op1) == TokenType.VARIABLE) {
			Object o = checkAndGetValueFromWord(this.op1);
			String str = o.toString();
			registers[0] = cf.newRegister(str);
		}else{
			throw new DynamicException().new defaultException("无法解析的符号");
		}

		if (regexPat(this.op2) == TokenType.CONST) {
			registers[1] = cf.newRegister(this.op2);
		} else if (regexPat(this.op2) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			String str = ClassFactory.Registers.get(this.op2).getValue().toString();
			registers[1] = cf.newRegister(str);
		} else if (regexPat(this.op2) == TokenType.VARIABLE) {
			Object o = checkAndGetValueFromWord(this.op2);
			String str = o.toString();
			registers[1] = cf.newRegister(str);
		}else{
			throw new DynamicException().new defaultException("无法解析的操作符");
		}

		return registers;
	}

	//暂时没有考虑溢出
	private void registerOperation(Register r_op1, Register r_op2, OPERATOR o) {
		switch (o){
			case ADD:
				if (r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.FLOAT) {
					float temp = (float) r_op1.getValue() + (float)r_op2.getValue();
					Register<Float> register = new Register<Float>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				} else if(r_op1.getType() == ClassFactory.TYPE.INT && r_op2.getType() == ClassFactory.TYPE.FLOAT){
					float temp = (int) r_op1.getValue() + (float) r_op2.getValue();
					Register<Float> register = new Register<Float>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				}else if(r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.INT){
					float temp = (float) r_op1.getValue() + (int) r_op2.getValue();
					Register<Float> register = new Register<Float>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				}else{
					int temp = (int) r_op1.getValue() + (int) r_op2.getValue();
					Register<Integer> register = new Register<Integer>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				}
				break;
			case PLUS:
				if (r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.FLOAT) {
					float temp = (float) r_op1.getValue() * (float)r_op2.getValue();
					Register<Float> register = new Register<Float>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				} else if(r_op1.getType() == ClassFactory.TYPE.INT && r_op2.getType() == ClassFactory.TYPE.FLOAT){
					float temp = (int) r_op1.getValue() * (float) r_op2.getValue();
					Register<Float> register = new Register<Float>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				}else if(r_op1.getType() == ClassFactory.TYPE.FLOAT && r_op2.getType() == ClassFactory.TYPE.INT){
					float temp = (float) r_op1.getValue() * (int) r_op2.getValue();
					Register<Float> register = new Register<Float>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				}else{
					int temp = (int) r_op1.getValue() * (int) r_op2.getValue();
					Register<Integer> register = new Register<Integer>(ClassFactory.TYPE.FLOAT);
					register.setValue(temp);
					ClassFactory.Registers.put(this.des,register);
				}
				break;
		}

	}


	public void print() {
		System.out.println("(" + this.oprator + "," + this.op1 + "," + this.op2 + "," + this.des + ")");
	}

	public String get_four_str() {
		return "(" + this.oprator + "," + this.op1 + "," + this.op2 + "," + this.des + ")";
	}

	public  TokenType regexPat(String str) throws DynamicException.defaultException {
		if (str.indexOf(regPat)==0)
			return TokenType.REGISTER;
		else if (str.matches(variPat))
			return TokenType.VARIABLE;
		else if (str.matches(constant))
			return TokenType.CONST;
		throw new DynamicException().new defaultException("无法解析的操作符");
	}

	public  TokenType regexPatForIndex(String str) throws DynamicException.defaultException {
		if (str.indexOf(regPat)==0)
			return TokenType.REGISTER;
		else if(str.matches(positiveInt))
			return TokenType.POSITIVE_INT;
		else if (str.matches(variPat))
			return TokenType.VARIABLE;
		throw new DynamicException().new defaultException("无法解析的操作符");
	}

	private int getTypeNum(String str) throws DynamicException.defaultException {
		if (str.matches(_int)) {
			return INT;
		} else if (str.matches(_float)) {
			return FLOAT;
		}
		throw new DynamicException().new defaultException("无法解析的数学表达式");

	}

	private Register checkAndGetRegister(String des) throws DynamicException.defaultException {
		Register reg = ClassFactory.Registers.getOrDefault(des, null);
		if (reg == null) {
			throw new DynamicException().new defaultException("找不到寄存器");
		}
		return reg;
	}

	private ArrayType checkAndGetArrayFromHash(String str) throws DynamicException.noArrayException {
		Word wordTemp = ClassFactory.Wordlist.getOrDefault(str, null);

		if (wordTemp == null) {
			throw new DynamicException().new noArrayException();
		}

		if (wordTemp.getClass() != ArrayType.class) {
			throw new DynamicException().new noArrayException();
		}

		return (ArrayType) wordTemp;
	}

	private Object checkAndGetValueFromWord(String str) throws DynamicException.undeclaredIdentifierException
			, DynamicException.unInitializedIdentifierException, DynamicException.mismatchOperatorException {

		Word word = checkAndGetWord(str);
		Object o = word.getValue();
		if (o == null) {
			throw new DynamicException().new unInitializedIdentifierException();
		}
		return o;
	}

	private Word checkAndGetWord(String str) throws DynamicException.undeclaredIdentifierException, DynamicException.mismatchOperatorException {
		Word word = ClassFactory.Wordlist.getOrDefault(str, null);
		if (word == null) {
			throw new DynamicException().new undeclaredIdentifierException();
		}
		if (word.getClass() != Word.class) {
			throw new DynamicException().new mismatchOperatorException();
		}
		return word;
	}


}
