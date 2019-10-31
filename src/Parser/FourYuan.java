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
	public void Exec() {
		if (this.oprator.contentEquals("+")) {
			Register[] registers = makeOpsRegister();
			registerOperation(registers[0], registers[1], OPERATOR.ADD);
		} else if (this.oprator.contentEquals("$")) {
			try {
				ArrayType array = (ArrayType) ClassFactory.Wordlist.getOrDefault(this.op1, null);

				if(array==null){
					//todo 如何实现中断下面的分析? 已catch exception
					Parser.errors.add("未定义的数组标识符");
				}

				//todo 怎样停下程序？当返回-1;
				int offSet = getIndex(this.op2);

				int value = 0;
				switch (regexPat(this.des)) {
					case CONST:
						value = Integer.parseInt(this.des);
						break;
					case REGISTER:
						Register reg = ClassFactory.Registers.getOrDefault(this.des, null);
						if (reg != null) {
							//todo 类型判断  现在全部实现为int类型
							value = (int) reg.getValue();
						} else {
							Parser.errors.add("无法为数组进行非法赋值");
						}
						break;
					case VARIABLE:
						Word word = ClassFactory.Wordlist.getOrDefault(this.des, null);
						if (word != null) {
							Object o = word.getValue();
							if (o != null) {
								value = (int) o;
							} else {
								Parser.errors.add("未初始化的标识符");
							}
						} else {
							Parser.errors.add("未定义的标识符");
						}
						break;
					default:
						Parser.errors.add("无法解析赋值对象");
				}
				//todo 类型check
				array.setValue(offSet, value);

			} catch (ClassCastException e) {
				Parser.errors.add(this.op1 + " 并不是数组类型");
			} catch (IllegalArgumentException e) {
				Parser.errors.add(this.op2 + " 是非法的数组下标");
			}catch (NullPointerException e){
				System.out.println(e.getMessage());
			}
		} else if (this.oprator.contentEquals("&")) {
			try {
				ArrayType array = (ArrayType) ClassFactory.Wordlist.getOrDefault(this.op1, null);

				if(array==null){
					//todo 如何实现中断下面的分析?
					Parser.errors.add("未定义的数组标识符");
				}

				int offSet = getIndex(this.op2);
				//类型check 和 隐式类型转换
				Object value = array.getValue(offSet);
				switch (regexPat(this.des)) {
					case REGISTER:
						Register reg = cf.newRegister(value.toString());
						ClassFactory.Registers.put(this.des,reg);
						break;
					case VARIABLE:
						Word word = ClassFactory.Wordlist.getOrDefault(this.des, null);
						if (word != null) {
							word.setValue(value);
						} else {
							Parser.errors.add("未定义的标识符");
						}
						break;
					default:
						Parser.errors.add("无法解析赋值对象");
				}

			} catch (ClassCastException e) {
				Parser.errors.add(this.op1 + " 并不是数组类型");
			} catch (IllegalArgumentException e) {
				Parser.errors.add(this.op2 + " 是非法的数组下标");
			}catch (NullPointerException e){}

		} else if (this.oprator.contentEquals("*")) {
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
				Parser.errors.add("未声明标志符-------" + this.des + "\n");
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
	}

	private int getIndex(String op2) {
		switch (regexPatForIndex(op2)) {
			case POSITIVE_INT:
				return Integer.parseInt(op2);
			case REGISTER:
				Register r = ClassFactory.Registers.getOrDefault(op2, null);
				//寄存器不存在getvalue 没有值的情况
				if (r != null)
					return (int) r.getValue();
				else {
					Parser.errors.add("数组下标的格式错误");
					return -1;
				}
				//todo 如果此寄存器存的值是float or  则需要更改错误处理
			case VARIABLE:
				Word word = ClassFactory.Wordlist.getOrDefault(op2, null);
				if (word != null) {
					Object v = word.getValue();
					if (v != null) {
						return (int) v;
					} else {
						//todo 中断程序继续执行
						Parser.errors.add("未初始化的标识符");
						return -1;
					}
				} else {
					Parser.errors.add("未定义的标识符");
					return -1;
				}
			default:
				return -1;
		}
	}

	private void con_jmp(String op) {
		int op1_val=-1;
		int op2_val=-1;
		Register [] registers = makeOpsRegister();
		op1_val = (Integer)registers[0].getValue();
		op2_val = (Integer)registers[1].getValue();
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

	public  TokenType regexPat(String str) {
		if (str.indexOf(regPat)==0)
			return TokenType.REGISTER;
		else if (str.matches(variPat))
			return TokenType.VARIABLE;
		else if (str.matches(constant))
			return TokenType.CONST;
		throw new IllegalArgumentException("no regex match");
	}

	public  TokenType regexPatForIndex(String str) {
		if (str.indexOf(regPat)==0)
			return TokenType.REGISTER;
		else if(str.matches(positiveInt))
			return TokenType.POSITIVE_INT;
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
