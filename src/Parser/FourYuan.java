package Parser;

import Window.mainWindow;
import java.awt.geom.FlatteningPathIterator;


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

	private enum OPERATOR {
		PLUS,
		ADD,
		SUB,
	}

	ClassFactory cf = new ClassFactory();

	//识别正则表达式.
	private String _int = "^[+/-]?[0-9]*$";
	private String _float = "(([+])?[0-9]\\d*\\.?\\d*)|((-)?[0-9]\\d*\\.?\\d*)";

	private String constant = "(([+])?[0-9]\\d*\\.?\\d*)|((-)?[0-9]\\d*\\.?\\d*)";
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
		try {
			if (this.oprator.contentEquals("+")) {
				Register[] registers = makeOpsRegister(this.op1, this.op2);
				Register register = registerOperation(registers[0], registers[1], OPERATOR.ADD);
				ClassFactory.Registers.put(this.des, register);
			} else if (this.oprator.contentEquals("$")) {
				ArrayType array = checkAndGetArrayFromHash(this.op1);
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
						Word word = checkAndGetWord(this.des);
						if(word.getValue()==null){
							throw new DynamicException().new unInitializedIdentifierException();
						}
						cf.setArrayElementValue(array, offSet, word.getValue(), word.type);
						break;
					default:
						throw new DynamicException().new defaultException("无法解析的符号");
				}
				//todo 类型check
			} else if (this.oprator.contentEquals("&")) {
				ArrayType array = checkAndGetArrayFromHash(this.op1);

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
						ClassFactory.Registers.put(this.des, reg);
						break;
					case VARIABLE:
						Word word = checkAndGetWord(this.des);
						if(word.getValue()==null){
							throw new DynamicException().new unInitializedIdentifierException();
						}
						if(type== ClassFactory.TYPE.INT_ARRAY){
							cf.setWordValue(word, value, ClassFactory.TYPE.INT);
						}else{
							cf.setWordValue(word, value, ClassFactory.TYPE.FLOAT);
						}
						break;
					default:
						throw new DynamicException().new defaultException("无法解析的目标地址");
				}
			} else if (this.oprator.contentEquals("*")) {
				Register[] registers = makeOpsRegister(this.op1, this.op2);
				Register register = registerOperation(registers[0], registers[1], OPERATOR.PLUS);
				ClassFactory.Registers.put(this.des, register);

				/////TODO 乘法运算
			} else if (this.oprator.contentEquals("JMP")) {
				/////TODO 直接跳转
				mainWindow.j = Integer.parseInt(des) - 1;
			} else if (this.oprator.contentEquals("=")) {
				Word word = checkAndGetWord(this.des);
				switch (regexPat(this.op1)) {
					case CONST:
						cf.setWordValue(word, this.op1);
						break;
					case REGISTER:
						Register register = checkAndGetRegister(this.op1);
						cf.setWordValue(word, register.getValue(), register.getType());
						break;
					case VARIABLE:
						Word temp = checkAndGetWord(this.op1);
						if(temp.getValue()==null){
							throw new DynamicException().new unInitializedIdentifierException();
						}
						cf.setWordValue(word, temp.getValue(), temp.type);
						break;
				}

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
		}
	}


	private int getIndex(String str) throws DynamicException.defaultException,
			DynamicException.undeclaredIdentifierException, DynamicException.unInitializedIdentifierException,
			DynamicException.mismatchOperatorException, DynamicException.numberFormatException {

		switch (regexPatForIndex(str)) {
			case POSITIVE_INT:
				return Integer.parseInt(str);
			case REGISTER:
				Register r = checkAndGetRegister(str);
				//todo  寄存器放数组吗？
				if(r.getType()== ClassFactory.TYPE.INT){
					return (int) r.getValue();
				}
				throw new DynamicException().new numberFormatException();
			//todo 如果此寄存器存的值是float or  则需要更改错误处理
			case VARIABLE:
				Word word = checkAndGetWord(str);
				if(word.type== ClassFactory.TYPE.INT) {
					return (int) word.getValue();
				}
				throw new DynamicException().new numberFormatException();

			default:
				return -1;
		}
	}

	private void con_jmp(String op) throws DynamicException.undeclaredIdentifierException,
			 DynamicException.defaultException,
			DynamicException.mismatchOperatorException {

		Register[] registers = makeOpsRegister(this.op1, this.op2);

		//(int)
		float sub = (float) registerOperation(registers[0], registers[1], OPERATOR.SUB).getValue();

		switch (op) {
			case "<":
				if (sub < 0)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case ">":
				if (sub > 0)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case "<=":
				if (sub <= 0)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case ">=":
				if (sub >= 0)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case "==":
				if (Math.abs(sub) < 1e-7)
					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
			case "!=":
				if (Math.abs(sub) >= 1e-6)

					mainWindow.j = Integer.parseInt(this.des) - 1;
				break;
		}
	}

	private Register[] makeOpsRegister(String op1, String op2) throws DynamicException.defaultException
			, DynamicException.undeclaredIdentifierException,
			DynamicException.mismatchOperatorException {
		Register[] registers = new Register[2];
		if (regexPat(op1) == TokenType.CONST) {
			registers[0] = cf.newRegister(op1);
		} else if (regexPat(op1) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			registers[0] = checkAndGetRegister(op1);
		} else if (regexPat(op1) == TokenType.VARIABLE) {
			Word word = checkAndGetWord(op1);
			registers[0] = new Register<>(word.type,word.getValue());
		} else {
			throw new DynamicException().new defaultException("无法解析的符号");
		}

		if (regexPat(op2) == TokenType.CONST) {
			registers[1] = cf.newRegister(op2);
		} else if (regexPat(op2) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			registers[1] = checkAndGetRegister(op2);
		} else if (regexPat(op2) == TokenType.VARIABLE) {
			Word word = checkAndGetWord(op2);
			registers[1] = new Register<>(word.type, word.getValue());
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
					float temp = (int) r_op1.getValue() - (int) r_op2.getValue();
					return new Register<>(ClassFactory.TYPE.INT, temp);
				}
		}
		throw new DynamicException().new defaultException("找不到匹配的操作符");

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
