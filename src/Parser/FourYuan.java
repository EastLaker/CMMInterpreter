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

	private enum TokenType {
		CONST,
		REGISTER,
		VARIABLE,
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



	//实现运算的autocast  避免运算过程中出现异常

	///TODO   加法运算
	///TODO   在单词表中找到第一个操作数（按照op1的名）
	///TODO    在单词表中找到第二个操作数（按照op2的名）
	///TODO    运算
	///TODO    保存到寄存器
	public void Exec() {
		if (this.oprator.contentEquals("+")) {
			Register[] registers = makeOpsRegister();
			registerOperation(registers[0], registers[1], OPERATOR.ADD);

		}else if (this.oprator.contentEquals("*")) {
			Register[] registers = makeOpsRegister();
			registerOperation(registers[0], registers[1], OPERATOR.PLUS);

			/////TODO 乘法运算
		} else if (this.oprator.contentEquals("JMP")) {
			/////TODO 直接跳转
			mainWindow.j = Integer.parseInt(des) - 1;
		} else if (this.oprator.contentEquals("=")) {
			Word word = ClassFactory.Wordlist.getOrDefault(this.des, null);
			if (word != null) {
				if (regexPat(this.op1) == TokenType.CONST) {
					//todo 类型判断 .
					cf.setWordValue(word, this.op1);
				} else if (regexPat(this.op1) == TokenType.REGISTER) {
					//todo value间的转换会出bug吗
					word.setValue(ClassFactory.Registers.get(op1).getValue());
				} else if (regexPat(this.op1) == TokenType.VARIABLE) {
					Word temp = ClassFactory.Wordlist.getOrDefault(op1, null);
					if (temp != null) {
						if (temp.getValue() != null) {
							word.setValue(temp.getValue());
						} else {
							Parser.errors.add("assigning from a uninitialized variable\n");
						}
					} else {
						Parser.errors.add("assigning from a undeclared variable\n");
					}
				}
			} else {
				Parser.errors.add("未声明标志符" + this.des + "\n");
			}
		}
		else if (this.oprator.contentEquals("J<"))
			con_jmp("<");

		else if(this.oprator.contentEquals("J>"))
			con_jmp(">");

		else if(this.oprator.equals("J<="))
			con_jmp("<=");
		else if(this.oprator.equals("J>="))
			con_jmp(">=");
		else if(this.oprator.equals("J=="))
			con_jmp("==");
		else if(this.oprator.equals("J!="))
			con_jmp("!=");
	}

	private void con_jmp(String op) {
		int op1_val=-1;
		int op2_val=-1;
		boolean o1=false,o2=false;
		if(regexPat(this.op1)== TokenType.CONST){
			op1_val = Integer.parseInt(this.op1);
			o1=true;///op1获得值
		}
		if(regexPat(this.op2)== TokenType.CONST){
			op2_val = Integer.parseInt(this.op2);
			o2=true;///op2获得值
		}
		/////TODO 选择跳转
		if(!o1){   ////o1还没有赋值 todo 给op1_val赋值
		Word op1 = ClassFactory.Wordlist.getOrDefault(this.op1, null);
		if (op1 == null)
			Parser.errors.add("未声明的标志符" + this.op1 + "\n");
		else
			op1_val = (int) op1.getValue();
		}
		if(!o2) {
			Word op2 = ClassFactory.Wordlist.getOrDefault(this.op2, null);
			if (op2 == null)
				Parser.errors.add("未声明的标志符" + this.op2 + "\n");
			else
				op2_val = (int) op2.getValue();
		}
		switch (op){
			case "<":
				if(op1_val<op2_val)
					mainWindow.j = Integer.parseInt(this.des) -1;
				break;
			case ">":
				if(op1_val>op2_val)
					mainWindow.j = Integer.parseInt(this.des) -1;
				break;
			case "<=":
				if(op1_val<=op2_val)
					mainWindow.j = Integer.parseInt(this.des)-1;
				break;
			case ">=":
				if(op1_val>=op2_val)
					mainWindow.j = Integer.parseInt(this.des) -1;
				break;
			case "==":
				if(op1_val==op2_val)
					mainWindow.j = Integer.parseInt(this.des) -1;
				break;
			case "!=":
				if(op1_val!=op2_val)
					mainWindow.j = Integer.parseInt(this.des) -1;
					break;

		}
	}

	private Register[] makeOpsRegister() {
		Register[] registers = new Register[2];
		if (regexPat(this.op1) == TokenType.CONST) {
			registers[0] = cf.newRegister(this.op1);
		} else if (regexPat(this.op1) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			String str = ClassFactory.Registers.get(this.op1).getValue().toString();
			registers[0] = cf.newRegister(str);
		} else if (regexPat(this.op1) == TokenType.VARIABLE) {
			String str = ClassFactory.Wordlist.get(this.op1).getValue().toString();
			registers[0] = cf.newRegister(str);
		}else{
			throw new IllegalArgumentException("no regex match");
		}

		if (regexPat(this.op2) == TokenType.CONST) {
			registers[1] = cf.newRegister(this.op2);
		} else if (regexPat(this.op2) == TokenType.REGISTER) {
			//todo 能不能直接get?  registers 未初始化完好;
			String str = ClassFactory.Registers.get(this.op2).getValue().toString();
			registers[1] = cf.newRegister(str);
		} else if (regexPat(this.op2) == TokenType.VARIABLE) {
			String str = ClassFactory.Wordlist.get(this.op2).getValue().toString();
			registers[1] = cf.newRegister(str);
		}else{
			throw new IllegalArgumentException("no regex match");
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

	private TokenType regexPat(String str) {
		if (str.indexOf(regPat)==0)
			return TokenType.REGISTER;
		else if (str.matches(constant))
			return TokenType.CONST;
		else if (str.matches(variPat))
			return TokenType.VARIABLE;
		throw new IllegalArgumentException("no regex match");
	}



	private int getTypeNum(String str) {
		if (str.matches(_int)) {
			return INT;
		} else if (str.matches(_float)) {
			return FLOAT;
		}
		throw new IllegalArgumentException("can not match type");
	}
}
