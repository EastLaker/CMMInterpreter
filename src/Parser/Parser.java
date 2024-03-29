package Parser;

import Utils.DataStructure;
import Utils.Regex;
import Window.mainWindow;
import java.util.*;

import lexical.*;

/**
 * @author lfz
 */

public class Parser {//////////////////识别完成token读到的应该是;
	//语法分析    识别算术表达式   规约规则⬇️
	//0）S->E+E
	//1) E->E+E
	//2) E->E+E
	//3) E->(E)
	//4) E->id
	public static List<String> errors = new ArrayList<String>();
	public List<String> parsers = new ArrayList<String>();
	public static List<String> Console = new ArrayList<String>();

	public List<Token> tokens = new ArrayList<Token>();/////用于存放词法分析的结果   测试
	public Token token = null;//读入的词
	public int cur = 0;///用于遍历词法分析的词
	public Stack<Integer> states = new Stack<Integer>();/////状态栈------用于赋值表达式的检测
	public Stack<String> symbols = new Stack<String>();/////符号栈------用于赋值表达式的检测
	public Stack<Integer> States = new Stack<Integer>();
	public Stack<String> Symbols = new Stack<String>();
	public Stack<E> Es = new Stack<E>();
	public Stack<B> Bs = new Stack<B>();
	public Stack<A> As = new Stack<A>();
	public Stack<O> Os = new Stack<O>();
	public List<FourYuan> fours = new ArrayList<FourYuan>();
	public boolean hadReturn = false;   // 用来函数体是否包含返回值,若类型为"void"则视为已有返回值

	public boolean error = false;//////源程序无错


	public void parserE() {///////////词法分析程序
		states.push(0);/////将0状态入栈
		boolean b = true;
		while (b) {
			switch (states.peek()) {
				case 0:
				case 2:

					if (token.getString().matches(Regex.variPat) || token.getString().matches(Regex.constant)) {
						states.push(3);////状态栈入栈
						element_of_array_E();
						token = tokens.get(cur++);
					} else if (token.getString().equals("(")) {
						states.push(2);
						symbols.push("(");
						token = tokens.get(cur++);
					} else
						b = error_parserE();
					break;
				case 1:
					if (token.getString().contentEquals("+") || token.getString().contentEquals("-")) {
						states.push(4);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					} else if (token.getString().equals("*") || token.getString().equals("/")) {
						states.push(5);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					} else if (token.getString().contentEquals(";") || token.getString().contentEquals(",") || token.getString().contentEquals("}") || token.getString().contentEquals("]")
							|| token.getString().contentEquals("<") || token.getString().contentEquals(">") || token.getString().contentEquals(">=") || token.getString().contentEquals("<=") ||
							token.getString().contentEquals("==") || token.getString().contentEquals("!=") || token.getString().contentEquals(")")
							|| token.getString().contentEquals("&&") || token.getString().contentEquals("||")) {
						for (int i = 0; i < 2; i++)
							states.pop();
						symbols.pop();
						b = false;/////识别表达式语句结束
						////退出识别表达式
						/////token==;
					} else
						b = error_parserE();
					break;
				case 3:
					if (token.getString().contentEquals("+") || token.getString().equals("-") || token.getString().equals("*") ||
							token.getString().equals("/") || token.getString().contentEquals(")") || token.getString().equals(";") ||
							token.getString().equals(",") || token.getString().equals("}") || token.getString().equals("]")
							|| token.getString().equals("<") || token.getString().equals(">") || token.getString().equals(">=") || token.getString().equals("<=") ||
							token.getString().equals("==") || token.getString().equals("!=") || token.getString().contentEquals("&&") ||
							token.getString().contentEquals("||")) {
						parsers.add("E->id");
						E e = new E();
						e.des = symbols.pop();//出栈一个id
						Es.push(e);
						states.pop();//出栈
						symbols.push(e.des);///////////////////////////////////////////////////////////////todo 修改
						states_push();
					} else
						b = error_parserE();

					break;
				case 4:
				case 5:

					if (token.getString().matches(Regex.variPat) || token.getString().matches(Regex.constant)) {
						states.push(3);
						element_of_array_E();
						token = tokens.get(cur++);
					} else if (token.getString().contentEquals("(")) {
						states.push(2);
						symbols.push("(");
						token = tokens.get(cur++);
					} else
						b = error_parserE();
					break;
				case 6:

					if (token.getString().contentEquals("+") || token.getString().equals("-")) {
						states.push(4);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					} else if (token.getString().contentEquals("*") || token.getString().equals("/")) {
						states.push(5);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					} else if (token.getString().equals(")")) {
						states.push(9);
						symbols.push(")");
						token = tokens.get(cur++);
					} else
						b = error_parserE();
					break;
				case 7:
					if (token.getString().equals("+") || token.getString().equals("-") || token.getString().equals(")") || token.getString().equals(";") || token.getString().equals(",") ||
							token.getString().equals("}") || token.getString().equals("]")
							|| token.getString().equals("<") || token.getString().equals(">") || token.getString().equals(">=") || token.getString().equals("<=") ||
							token.getString().equals("==") || token.getString().equals("!=")
							|| token.getString().contentEquals("&&") || token.getString().contentEquals("||")) {
						//r1规约
						parsers.add("E->E+E/E-E");
						for (int i = 0; i < 3; i++) {
							states.pop();/////语法动作，符号栈出栈三次，状态栈出栈三次
						}
						E e = new E();
						e.des = E.getReg();
						String op2 = symbols.pop();/////出栈E
						String op = symbols.pop();//////出栈运算符+/-
						String op1 = symbols.pop();/////出栈E
						FourYuan four = new FourYuan();
						four.des = e.des;
						four.op1 = op1;
						four.op2 = op2;
						four.oprator = op;
						FourYuan.no++;////指令序号加一
						fours.add(four);
						Es.push(e);
						symbols.push(e.des);/////////////////////////////todo修改
						states_push();
					} else if (token.getString().contentEquals("*") || token.getString().equals("/")) {
						states.push(5);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					} else
						b = error_parserE();
					break;
				case 8:

					if (token.getString().equals("+") || token.getString().equals("-") || token.getString().contentEquals("*") ||
							token.getString().equals("/") || token.getString().equals(")") || token.getString().contentEquals(";")
							|| token.getString().equals(",") || token.getString().equals("}") || token.getString().equals("]")
							|| token.getString().equals("<") || token.getString().equals(">") || token.getString().equals(">=") || token.getString().equals("<=") ||
							token.getString().equals("==") || token.getString().equals("!=")
							|| token.getString().contentEquals("&&") || token.getString().contentEquals("||")) {
						//r2规约
						parsers.add("E->E*E /  E/E");
						for (int i = 0; i < 3; i++) {
							states.pop();
						}
						E e = new E();
						e.des = E.getReg();
						String op2 = symbols.pop();
						String op = symbols.pop();
						String op1 = symbols.pop();
						Es.push(e);
						symbols.push(e.des);////////////////////////////todo修改
						FourYuan four = new FourYuan();
						four.oprator = op;
						four.op1 = op1;
						four.op2 = op2;
						four.des = e.des;
						fours.add(four);
						FourYuan.no++;///
						states_push();
					} else
						b = error_parserE();
					break;
				case 9:

					if (token.getString().equals("+") || token.getString().equals("-") || token.getString().contentEquals("*") ||
							token.getString().contentEquals("/") || token.getString().equals(")") || token.getString().contentEquals(";") ||
							token.getString().equals(",") || token.getString().equals("}") || token.getString().equals("]")
							|| token.getString().equals("<") || token.getString().equals(">") || token.getString().equals(">=") || token.getString().equals("<=") ||
							token.getString().equals("==") || token.getString().equals("!=")
							|| token.getString().contentEquals("&&") || token.getString().contentEquals("||")) {
						//r3规约
						parsers.add("E->(E)");
						for (int i = 0; i < 3; i++) {
							states.pop();
						}
						symbols.pop();
						String s = symbols.pop();
						symbols.pop();
						symbols.push(s);///将规约得到的E入栈
						states_push();
					} else
						b = error_parserE();
					break;
			}

		}
	}

	private boolean error_parserE() {
		boolean b;
		error = true;//程序有错
		b = false;
		errors.add("line :" + token.getLine_no() + "  错误输入   " + token.getString());
		while (!(token.getString().contentEquals(";") || token.getString().contentEquals(",") || token.getString().contentEquals("}") || token.getString().contentEquals("]")
				|| token.getString().contentEquals("<") || token.getString().contentEquals(">") || token.getString().contentEquals(">=") || token.getString().contentEquals("<=") ||
				token.getString().contentEquals("==") || token.getString().contentEquals("!=") || token.getString().contentEquals(")")
				|| token.getString().contentEquals("&&") || token.getString().contentEquals("||")))
			token = tokens.get(cur++);///todo 退出本次语法分析程序
		return b;
	}
	private void element_of_array_E() {
		String reg_ = null;
		boolean is_element_of_array = false;
		boolean is_element_of_func = false;
		if (tokens.get(cur).getString().equals("[")) {///预读一个单词
			/////是一个数组元素
			if (!token.getString().matches(Regex.variPat)) {
				error = true;
				errors.add("line :" + token.getLine_no() + "错误数组基址 " + token.getString());
				return;
			}
			is_element_of_array = true;
			String s = token.getString();  ////此时s token == id
			token = tokens.get(cur++);////此时token == [
			token = tokens.get(cur++);
			parserE();
			///结束返回时token  == ]
			FourYuan four = new FourYuan();
			four.oprator = "&";
			four.op1 = s;
			four.op2 = Es.peek().des;
			E e = new E();
			e.des = E.getReg();
			Es.push(e);
			four.des = e.des;
			fours.add(four);
			FourYuan.no++;
			reg_ = e.des + "";
		}
		else if(tokens.get(cur).getString().contentEquals("(")){//////预读一个为（  那么参与运算的是函数调用的返回值
			is_element_of_func = true;
			String function = token.getString();  // "function"为函数名
			token = tokens.get(cur++);////此时token应该为左括号
			List<String> parameters = new ArrayList<>();  // 形参表
            if(token.getString().contentEquals("(")){
            	token = tokens.get(cur++);/////token应该为第一个形参的开始
            	while (!")".contentEquals(token.getString())){
            		parserE();
            		parameters.add(Es.peek().des);
            		if(token.getString().contentEquals(","))
            			token = tokens.get(cur++);
            		else if(token.getString().contentEquals(")"))
            			break;
				}
			}
			///此时token应该为右括号
			///生成跳转语句

            fouryuan_call(function, parameters);
        }
		if (is_element_of_array)
			symbols.push(reg_);
		else if(is_element_of_func)
			symbols.push("reg_rax");
		else
			symbols.push(token.getString());
	}

    private void fouryuan_call(String function, List<String> parameters) {
        FourYuan four = new FourYuan();
        four.oprator = "cal";
        four.op2 = function;
        four.op1 = "_";
        four.des = "_";
        fours.add(four);
        FourYuan.no++;
        DataStructure.Ret = mainWindow.j + 1;

        for (String parameter : parameters) {
            FourYuan fourYuan = new FourYuan();
            fourYuan.oprator = "sp";
            fourYuan.op1 = parameter;
            fourYuan.op2 = "_";
            fourYuan.des = function;
            fours.add(fourYuan);
            FourYuan.no++;

        }
        FourYuan fourYuan = new FourYuan();
        fourYuan.oprator = "cal";
        fourYuan.op1 = "_";
        fourYuan.op2 = "_";
        fourYuan.des = "_";
        fours.add(fourYuan);
        FourYuan.no++;
    }

    private void states_push() {
		switch (states.peek()) {
			case 0:
				states.push(1);
				break;
			case 2:
				states.push(6);
				break;
			case 4:
				states.push(7);
				break;
			case 5:
				states.push(8);
				break;
		}
	}


	// 语法分析"程序"入口
	// Program -> Block Program | #
	public void Program() {
		if (token.getString().contentEquals("int") || token.getString().contentEquals("float") || token.getString().contentEquals("void")) {
			parsers.add("Program -> Block Program");
			Block();
			Program();
		} else if (token.getString().contentEquals("#")) {
			parsers.add("Program -> #");
			parsers.add("识别结束！程序正确");
		} else {
			errors.add("行" + token.getLine_no() + ": 非法的语句开始");

		}
	}

	// 语法分析"代码块"入口
	public void Block() {
		if (token.getString().contentEquals("int") || token.getString().contentEquals("float")) {
			String TYPE = token.getString();    // TYPE指代变量或函数数据类型
			if (tokens.get(cur+1).getString().contentEquals("=") || tokens.get(cur+1).getString().contentEquals(",")
					|| tokens.get(cur+1).getString().contentEquals(";")||tokens.get(cur+1).getString().contentEquals("[")) {    // 进入"全局变量"声明分支
				token = tokens.get(cur++);
				Statement(TYPE);
			} else if (tokens.get(cur + 1).getString().contentEquals("(")) {    // 进入"函数"定义分支
				token = tokens.get(cur++);
				Function(TYPE);
			} else {
				errors.add("行" + token.getLine_no() + ": 非法的程序语句");
			}
		} else if (token.getString().contentEquals("void")) {
			String TYPE = "void";
			token = tokens.get(cur++);
			Function(TYPE);
		}
	}

	// 语法分析"全局变量声明"入口
	private void Statement(String type) {
		if (token.getString().matches(Regex.variPat)) {
			String name = token.getString();
			if (tokens.get(cur).getString().contentEquals("["))//////预读一个是[ 说明是一个全局数组
				declare_array(type, name);
			else {
				FourYuan fourYuan = new FourYuan();
				fourYuan.oprator = "dw";
				fourYuan.op1 = "_";
				fourYuan.op2 = type;
				fourYuan.des = name;
				fours.add(fourYuan);
				FourYuan.no++;
				////TODO 此处需判断该name是否为声明过的全局变量，若不是则加入单词表，若是则报错
				token = tokens.get(cur++);
				if (token.getString().contentEquals("=")) {
					////TODO 全局变量声明语义动作
					FourYuan four = new FourYuan();
					four.oprator = token.getString();//=
					four.des = name;
					token = tokens.get(cur++);
					parserE();
					four.op1 = Es.peek().des;
					four.op2 = "_";
					FourYuan.no++;
					fours.add(four);
				} else {
					////TODO 全局变量只声明未赋值语义动作
				}

				if (token.getString().contentEquals(";")) {
					token = tokens.get(cur++);
					return;
				} else if (token.getString().contentEquals(",")) {
					token = tokens.get(cur++);
					Statement(type);
				}
			}
		}else{
				errors.add("行" + token.getLine_no() + ": 不合法标识符");
			}

	}

	// 语法分析"函数"入口
	private void Function(String type) {
		if (token.getString().matches(Regex.variPat)) {
			String NAME = token.getString();// NAME指代函数名称
			//todo 自动识别main
//			if (NAME.contentEquals("main"))
//				DataStructure.Main = FourYuan.no;
			int no_df_statement ;
			if(NAME.contentEquals("main")){
				no_df_statement = FourYuan.no;
				FourYuan fourYuan = new FourYuan();
				fourYuan.oprator = "cal";
				fourYuan.op1 = "_";
				fourYuan.op2 = "main";
				fourYuan.des = "_";
				FourYuan.no++;
				fours.add(fourYuan);
				token = tokens.get(cur++);/////此时token应该为（
				if(token.getString().contentEquals("(")){
					token = tokens.get(cur++);////此时token应该为）
					if(token.getString().contentEquals(")")){
						token = tokens.get(cur++);////此时token应该为{
					}
				}
			}
			else {
				no_df_statement = FourYuan.no;
				FourYuan fourYuan = new FourYuan();
				fourYuan.oprator = "df";
				fourYuan.op1 = type;
				fourYuan.op2 = NAME;
				fourYuan.des = null;
				fours.add(fourYuan);
				FourYuan.no++;
				token = tokens.get(cur + 1);    // token此时需指向"形参"的首个单词
				cur += 2;
				Parameter(NAME);    // 读取函数形参
			}
			if (token.getString().contentEquals("{")) { // 进入"函数体"部分
				int no_left = FourYuan.no;
				fours.get(no_df_statement).des = FourYuan.no + "";
				FourYuan fourYuan1 = new FourYuan();
				fourYuan1.oprator = "{";
				fourYuan1.op1 = "_";
				fourYuan1.op2 = "_";
				fourYuan1.des = NAME;
				fours.add(fourYuan1);
				FourYuan.no++;
				token = tokens.get(cur++);
				hadReturn = type.contentEquals("void");
				L(type, NAME);
				if (token.getString().contentEquals("}")) {
					fours.get(no_left).op1 = FourYuan.no+"";
					FourYuan fourYuan2 = new FourYuan();
					fourYuan2.oprator = "}";
					fourYuan2.op1 = "_";
					fourYuan2.op2 = "_";
					fourYuan2.des = NAME;
					fours.add(fourYuan2);
					FourYuan.no++;
					if (!hadReturn) {
						errors.add("行" + token.getLine_no() + ": 函数缺少返回值");
					}
					hadReturn = false;    // 重置为false
					token = tokens.get(cur++);
					////TODO 单个函数扫描完成，存储该函数相关数据及操作，重新激活后续语义动作
				} else {
					errors.add("行" + token.getLine_no() + ": 函数体缺少'}'");
				}
			} else {
				errors.add("行" + token.getLine_no() + ": 函数体缺少'{'");
			}
		} else {
			errors.add("行" + token.getLine_no() + ": 不合法标识符");
		}
	}

	// 语法分析"函数形参"入口
	private void Parameter(String funcName) {
		if (token.getString().contentEquals("int") || token.getString().contentEquals("float")) {
			String type = token.getString();    // type此处为形参数据类型
			token = tokens.get(cur++);
			if (token.getString().matches(Regex.variPat)) {
				String parName = token.getString();
				////TODO 函数名funcName,形参类型type,形参名parName,插入语义动作
				token = tokens.get(cur++);
				FourYuan fourYuan = new FourYuan();
				fourYuan.oprator = "dp";
				fourYuan.op1 = type;
				fourYuan.op2 = parName;
				fourYuan.des = funcName;
				fours.add(fourYuan);
				FourYuan.no++;
				if (token.getString().contentEquals(",")) {  // 继续读取下一个形参
					token = tokens.get(cur++);
					Parameter(funcName);
				} else if (token.getString().contentEquals(")")) {  // 形参读取结束
					token = tokens.get(cur++);
				} else {
					errors.add("行" + token.getLine_no() + ": 形参缺少'）'");
				}
			} else {
				errors.add("行" + token.getLine_no() + ": 形参命名错误");
			}
		} else if (token.getString().contentEquals(")")) {  // 没有形参
			token = tokens.get(cur++);
		} else {
			errors.add("行" + token.getLine_no() + ": 形参数据类型错误");
		}
	}

	// 语法分析"函数体"入口
	// L->SL|$
	// S->a;|{L}|if语句|while语句
	public void L(String funcType, String funcName) {
		// 函数类型funcType，函数名funcName
		while (token.getString().contentEquals("return") || token.getString().contentEquals("{") || token.getString().equals("if") || token.getString().contentEquals("while") || token.getString().matches("write")||token.getString().matches(Regex.variPat)
				|| token.getString().matches(Regex._float) || token.getString().matches(Regex._int)) {
			parsers.add("L->SL");
			S(funcType, funcName);
			L(funcType, funcName);
		}
		if (token.getString().contentEquals("}")) {
			parsers.add("L -> null");

		} else {
			////TODO （语法分析）此处还需要后续识别是否存在'}'
		}
	}

	// 语法分析"单个语句"入口
	public void S(String funcType, String funcName) {
		// return语句
		if (token.getString().contentEquals("return")) {
			// 类型为"int"或"float"，需要进一步读取返回值
			if (!funcType.contentEquals("void")) {
				token = tokens.get(cur++);
				//TODO 读取后续返回值并存储
				parserE();
				//todo
					FourYuan fourYuan = new FourYuan();
					fourYuan.oprator =  "=";
					fourYuan.op1 = Es.peek().des;
					fourYuan.op2 = "_";
					fourYuan.des = "reg_rax";
					fours.add(fourYuan);
					FourYuan.no++;
	//			DataStructure.rax.setValue(Es.peek().des);
					hadReturn = true;
			} else {
					//todo fix
	//				DataStructure.rax.setValue(null);
				token = tokens.get(cur++);
			}
			if (token.getString().contentEquals(";")) {
				token = tokens.get(cur++);
			} else {
				errors.add("行" + token.getLine_no() + ": 缺少';'");
			}
			//TODO 扫描完return语句后，应该停止该函数中后续所有的语义动作
		}
		// 复合语句
		else if (token.getString().contentEquals("{")) {
			FourYuan fourYuan = new FourYuan();
			fourYuan.op1 = "_";
			fourYuan.op2 = "_";
			fourYuan.des = "_";
			fourYuan.oprator = "{";
			fours.add(fourYuan);
			FourYuan.no++;
			parsers.add("S->{L}");
			token = tokens.get(cur++);
			L(funcType, funcName);
			if (token.getString().contentEquals("}")) {
				FourYuan fourYuan1 = new FourYuan();
				fourYuan1.oprator = "}";
				fourYuan1.op1 = "_";
				fourYuan1.op2 = "_";
				fourYuan1.des = "_";
				fours.add(fourYuan1);
				FourYuan.no++;
				token = tokens.get(cur++);
			} else {
				error = true;
				errors.add("line :" + token.getLine_no() + "缺少}");
			}
		}/////复合语句
		else if (token.getString().contentEquals("write")) {
			token = tokens.get(cur++);
			if (token.getString().contentEquals("(")) {
				token = tokens.get(cur++);
				if (token.getString().matches(Regex._string)) {
					Console.add(token.getString().substring(1));
					token = tokens.get(cur++);
					if (token.getString().contentEquals(")")) {
						System.out.println("识别右括号");
						token = tokens.get(cur++);
						if (token.getString().contentEquals(";")) {
							token = tokens.get(cur++);
						}
					}
				} else {
					parserE();
					FourYuan four = new FourYuan();
					four.oprator = "wrt";
					four.op1 = "_";
					four.op2 = "_";
					four.des = Es.peek().des;
					fours.add(four);
					FourYuan.no++;
					if (token.getString().contentEquals(")")) {
						token = tokens.get(cur++);
						if (token.getString().contentEquals(";"))
							token = tokens.get(cur++);
					}
				}


			}
		}

		else if(token.getString().contentEquals("if")){
			token = tokens.get(cur++);
			if(token.getString().contentEquals("(")){
				token = tokens.get(cur++);
				parserB();
				if(token.getString().contentEquals(")")){
					token = tokens.get(cur++);
					B b = Bs.peek();/////此时b为if分支的B
					for(int i = 0 ; i<b.truelist.size();i++)
						fours.get(b.truelist.get(i)).des = FourYuan.no + "";
					S(funcType,funcName);
					List<Integer> jmp_list = new ArrayList<>();
					jmp_list.add(FourYuan.no);
					FourYuan fourYuan = new FourYuan();
					fourYuan.oprator = "JMP";
					fourYuan.op1 = "_";
					fourYuan.op2 = "_";
					fourYuan.des = null;
					fours.add(fourYuan);
					FourYuan.no++;
					for(int i=0 ; i<jmp_list.size();i++)
						fours.get(jmp_list.get(i)).des = FourYuan.no+ "";
					while (token.getString().contentEquals("else")&&tokens.get(cur).getString().contentEquals("if")){//else if循环
						for(int i = 0; i<b.falselist.size(); i++)
							fours.get(b.falselist.get(i)).des = FourYuan.no + "";
						token = tokens.get(cur++);//token应该为if
						token = tokens.get(cur++);//token应该为左括号
						if(token.getString().contentEquals("(")){
							token = tokens.get(cur++);
							parserB();
							if(token.getString().contentEquals(")")){
								token = tokens.get(cur++);
								b = Bs.peek();
								for(int i = 0;i<b.truelist.size();i++)
									fours.get(b.truelist.get(i)).des = FourYuan.no+"";
								S(funcType,funcName);
								jmp_list.add(FourYuan.no);
								FourYuan fourYuan1 = new FourYuan();
								fourYuan1.oprator = "JMP";
								fourYuan1.op1 = "_";
								fourYuan1.op2 = "_";
								fourYuan1.des = null;
								fours.add(fourYuan1);
								FourYuan.no++;
								for(int i=0 ; i<jmp_list.size();i++)
									fours.get(jmp_list.get(i)).des = FourYuan.no + "";
							}
						}
					}
					if(token.getString().contentEquals("else")){
						// else带{}
						token = tokens.get(cur++);
						for(int i = 0;i<b.falselist.size();i++)
							fours.get(b.falselist.get(i)).des = FourYuan.no + "";
						S(funcType,funcName);
						for(int i=0 ; i<jmp_list.size();i++)
							fours.get(jmp_list.get(i)).des = FourYuan.no + "";
					}
					//// 只有if语句
					else {////回填假出口
						while (fours.get(b.falselist.get(0)).des == null) {
							for (int j = 0; j < b.falselist.size(); j++)
								fours.get(b.falselist.get(j)).des = FourYuan.no + "";
							b = Bs.pop();
						}
					}
				}
			}
		}

		// while语句
		else if ("while".equals(token.getString())) {
			parsers.add("S->while语句");
			token = tokens.get(cur++);
			if ("(".equals(token.getString())) {
				token = tokens.get(cur++);
				int stru = FourYuan.no;
				/*
				生成跳转语句返回的地址
				*/
				parserB();
				//////逻辑表达式识别!
				//TODO 识别逻辑表达式

				if (")".equals(token.getString())) {
					token = tokens.get(cur++);
					B b = Bs.peek();
					for (int i = 0; i < b.truelist.size(); i++)/////回填真出口
						fours.get(b.truelist.get(i)).des = FourYuan.no + "";
					S(funcType, funcName);////TODO 生成跳转语句
					FourYuan four = new FourYuan();
					four.op1 = "_";
					four.op2 = "_";
					four.oprator = "JMP";
					four.des = stru + "";
					fours.add(four);
					FourYuan.no++;
					for (int j = 0; j < b.falselist.size(); j++)
						fours.get(b.falselist.get(j)).des = FourYuan.no + "";/////////回填假出口
					//token = tokens[cur++];
				}
			} else
				System.out.println("缺少（");
		}
		// 变量声明
		else if (token.getString().contentEquals("int") || token.getString().contentEquals("float")) {
			///TODO  建立单词表
			String type = token.getString();
			token = tokens.get(cur++);
			addWord1(type);
		}
		// 函数调用或赋值语句
		else if (token.getString().matches(Regex.variPat)) {
			// 函数调用语句

			// 赋值语句
				FourYuan four = new FourYuan();
				parsers.add("S->a;");
				String des = token.getString();
				token = tokens.get(cur++);
				boolean is_element_of_array = false;
				if (token.getString().equals("[")) {
					//////是一个数组元素
					four.oprator = "$";
					is_element_of_array = true;
					token = tokens.get(cur++);
					parserE();
					four.op1 = des;
					four.op2 = Es.peek().des;

					if (token.getString().equals("]"))
						token = tokens.get(cur++);
					else
						errors.add(token.getString() + "缺少]");
				}
				if (token.getString().contentEquals("=")) {/////赋值语句
					token = tokens.get(cur++);
					parserE();
					if (token.getString().contentEquals(";")) {
						if (!is_element_of_array) {
							four.oprator = "=";
							four.op1 = Es.peek().des;
							four.op2 = "_";
							four.des = des;
						} else {
							four.des = Es.peek().des;
						}
						fours.add(four);
						FourYuan.no++;
						token = tokens.get(cur++);
					}

			} else if(token.getString().contentEquals("(")){///单独的函数调用
				    token = tokens.get(cur++);////token应该为）或者形参列表的开始
				    List<String> parameters = new ArrayList<>();
				    while(!token.getString().contentEquals(")")){
				        parserE();
				        parameters.add(Es.peek().des);
				        if(token.getString().contentEquals(","))
				            token = tokens.get(cur++);
				        else if(token.getString().contentEquals(")"))
				            break;
                    }
				    //此时token应该为）
				fouryuan_call(des,parameters);
				    token = tokens.get(cur++);////此时token应该为;
					token = tokens.get(cur++);
                }

		}
		// 常变量声明
		else if (token.getString().matches(Regex.constant)) {
			String s = token.getString();
			int no = token.getLine_no();
			token = tokens.get(cur++);
			if (token.getString().contentEquals("=")) {
				Parser.errors.add("line: " + (no + 1) + "error : 给常量" + s + "赋值");
			}
			while (!token.getString().contentEquals(";") && !token.getString().contentEquals("#"))
				token = tokens.get(cur++);
		}
	}

	private void addWord1(String type) {
		String name = token.getString();///name 为变量名或数组名
		if (tokens.get(cur).getString().contentEquals("[")) {
			declare_array(type, name);
		} else {
			/////普通变量  不是数组
			FourYuan fourYuan = new FourYuan();
			fourYuan.oprator = "dw";
			fourYuan.des = name;
			fourYuan.op2 = type;
			fourYuan.op1 = "_";
			fours.add(fourYuan);
			FourYuan.no++;
			token = tokens.get(cur++);
			if (token.getString().contentEquals("=")) {
				token = tokens.get(cur++);
				parserE();
				FourYuan fourYuan1 = new FourYuan();
				fourYuan1.oprator = "=";
				fourYuan1.op1 = Es.peek().des;
				fourYuan1.op2 = "_";
				fourYuan1.des = name;
				fours.add(fourYuan1);
				FourYuan.no++;
			}
			T1(type);
		}
	}

	private void declare_array(String type, String name) {
		//////预读一个字符为[  是个数组的声明
		token = tokens.get(cur++);///此时token 应该为[
		if (token.getString().contentEquals("[")) {
			boolean length_determined = false;
			int size = 0;
			token = tokens.get(cur++);
			int no_of_declare_statement = FourYuan.no;/////声明这个赋值语句的序号
			if (token.getString().matches(Regex.positiveInt)) {
				length_determined = true;
				size = Integer.parseInt(token.getString());
				FourYuan fourYuan = new FourYuan();
				fourYuan.oprator = "da";
				fourYuan.op1 = size + "";
				fourYuan.op2 = type;
				fourYuan.des = name;
				fours.add(fourYuan);
				FourYuan.no++;
				token = tokens.get(cur++);
			} else if (token.getString().contentEquals("]")) {
				FourYuan fourYuan = new FourYuan();
				fourYuan.oprator = "da";
				fourYuan.op1 = null;
				fourYuan.op2 = type;
				fourYuan.des = name;
				fours.add(fourYuan);
				FourYuan.no++;
			}
			if (!"]".contentEquals(token.getString())) {
				error = true;
				errors.add("line :" + token.getLine_no() + "   缺少]\n");
				return;
			} else token = tokens.get(cur++);
			if (token.getString().contentEquals("=")) {//////数组元素需要初始化
				token = tokens.get(cur++);
				if ((token.getString().matches(Regex.constant) || token.getString().matches(Regex.variPat)) && length_determined) {
					parserE();
					for (int i = 0; i < size; i++) {
						FourYuan fourYuan = new FourYuan();
						fourYuan.oprator = "$";
						fourYuan.des = Es.peek().des;
						fourYuan.op1 = name;
						fourYuan.op2 = i + "";
					}
				} else if (token.getString().contentEquals("{")) {
					int j = 0;
					token = tokens.get(cur++);///////////////////////////////////////////////////////////////
					while (token.getString().matches(Regex.variPat) || token.getString().matches(Regex.constant)) {
						parserE();
						FourYuan fourYuan = new FourYuan();
						fourYuan.oprator = "$";
						fourYuan.des = Es.peek().des;
						fourYuan.op1 = name;
						fourYuan.op2 = j + "";
						fours.add(fourYuan);
						FourYuan.no++;
						j++;
						if (token.getString().contentEquals(","))
							token = tokens.get(cur++);
						else break;
					}
					if (token.getString().contentEquals("}")) { // 数组初始化完毕
						if (fours.get(no_of_declare_statement).oprator.contentEquals("da") && !length_determined) {
							fours.get(no_of_declare_statement).op1 = j + "";/////回填声明数组大小
						}
						token = tokens.get(cur++);
						if (token.getString().contentEquals(";")){
							token = tokens.get(cur++);
						} else {
							errors.add("行" + token.getLine_no() + ": 语句缺少';'");
						}
					} else {
						errors.add("行" + token.getLine_no() + ": 数组初始化缺少'}'");
					}
				}
			}
		}
	}

	private void T1(String type) {
		if (token.getString().contentEquals(";")) {
			token = tokens.get(cur++);
		} else if (token.getString().contentEquals(",")) {
			token = tokens.get(cur++);
			addWord1(type);
		}
	}

	//语句识别"布尔表达式"
	void parserB() {
		States.push(0);
		boolean b = true;
		while (b) {
			switch (States.peek()) {
				case 0:

					if (token.getString().matches(Regex.variPat) || token.getString().matches(Regex.constant)) {
						States.push(1);
						parserE();
						Symbols.push(Es.peek().des);
					} else if (token.getString().equals("(")) {
						States.push(4);
						Symbols.push("(");
						token = tokens.get(cur++);
					} else if (token.getString().equals("!")) {
						States.push(5);
						Symbols.push("!");
						token = tokens.get(cur++);
					} else if (token.getString().equals(")")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ( ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 1:
					if (token.getString().equals(">") || token.getString().equals(">=") || token.getString().equals("<") || token.getString().equals("<=") || token.getString().equals("!=") || token.getString().equals("==")) {
						States.push(2);
						Symbols.push(token.getString());//rop符号
						token = tokens.get(cur++);
					} else if (token.getString().equals(")") || token.getString().equals("&&") || token.getString().equals("||") || token.getString().equals(";")) {
						parsers.add("B->id");
						States.pop();//出栈状态栈出栈一个
						/*Symbols.pop();**/   //符号栈出栈一个
						B boo = new B();
						boo.op1 = Symbols.pop();
						Symbols.push("B");
						push_States_B();
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 2:
					if (token.getString().matches(Regex.variPat) || token.getString().matches(Regex.constant)) {
						States.push(3);
						parserE();
						Symbols.push(Es.peek().des);
					} else if (token.getString().contentEquals(")")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ( ");
						break;
					} else if (token.getString().contentEquals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 3:
					if (token.getString().equals(")") || token.getString().equals("&&") || token.getString().equals("||") || token.getString().equals(";")) {
						parsers.add("B->i rop i");
						for (int i = 0; i < 3; i++) {
							States.pop();
						}
						B boo = new B();
						boo.op2 = Symbols.pop();
						boo.op = Symbols.pop();
						boo.op1 = Symbols.pop();
						boo.truelist.add(FourYuan.no);
						boo.falselist.add(FourYuan.no + 1);
						boo.startStat = FourYuan.no;
						Bs.push(boo);
						FourYuan four1 = new FourYuan();
						four1.op1 = boo.op1;
						four1.op2 = boo.op2;
						four1.oprator = "J" + boo.op;
						four1.des = null;
						fours.add(four1);
						FourYuan.no++;
						FourYuan four2 = new FourYuan();
						four2.op1 = "_";
						four2.op2 = "_";
						four2.oprator = "JMP";
						four2.des = null;
						FourYuan.no++;
						fours.add(four2);
						Symbols.push("B");
						push_States_B();
					} else if (token.getString().contentEquals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 4:
				case 5:
				case 7:
				case 8:

					if (token.getString().matches(Regex.variPat) || token.getString().matches(Regex.constant)) {
						States.push(1);
						parserE();
						Symbols.push(Es.peek().des);
					} else if (token.getString().equals("(")) {
						States.push(4);
						Symbols.push("(");
						token = tokens.get(cur++);
					} else if (token.getString().equals("!")) {
						States.push(5);
						Symbols.push("!");
						token = tokens.get(cur++);
					} else if (token.getString().contentEquals(")")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ( ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 6:
					if (token.getString().equals("&&")) {
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					} else if (token.getString().equals("||")) {
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					} else if (token.getString().equals(")") || token.getString().equals(";")) {
						System.out.println("B->!B");
						for (int i = 0; i < 2; i++) {
							States.pop();
							Symbols.pop();
						}
						B boo = Bs.pop();
						List list = boo.truelist;
						boo.truelist = boo.falselist;
						//真假出口交换
						boo.falselist = list;
						Bs.push(boo);
						Symbols.push("B");
						push_States_B();
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 9:
					if (token.getString().equals("(") || token.getString().equals("!") || token.getString().matches(Regex.variPat) ||
							token.getString().matches(Regex.constant)) {
						parsers.add("A->B&&");
						for (int i = 0; i < 2; i++) {
							Symbols.pop();
							States.pop();
						}
						A a = new A();
						a.b = Bs.peek();
						As.push(a);
						Symbols.push("A");
						push_States_A();
					} else if (token.getString().equals(")")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ( ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 10:
					if (token.getString().equals("(") || token.getString().equals("!") || token.getString().matches(Regex.variPat) ||
							token.getString().matches(Regex.constant)) {
						parsers.add("O->B||");
						for (int i = 0; i < 2; i++) {
							Symbols.pop();
							States.pop();
						}
						Symbols.push("O");
						O o = new O();
						o.b = Bs.peek();
						Os.push(o);
						push_States_O();
					} else if (token.getString().equals(")")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ( ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 11:
					if (token.getString().equals(")")) {
						States.push(12);
						Symbols.push(")");
						token = tokens.get(cur++);
					} else if (token.getString().equals("&&")) {
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					} else if (token.getString().equals("||")) {
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) 或者 ; ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 12:
					if (token.getString().equals(")") || token.getString().equals("&&") || token.getString().equals("||") ||
							token.getString().equals(";")) {
						parsers.add("B->(B)");
						for (int i = 0; i < 3; i++) {
							Symbols.pop();
							States.pop();
						}
						Symbols.push("B");
						push_States_B();
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) 或者 ;  ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 13:
					if (token.getString().equals("&&")) {
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					} else if (token.getString().equals("||")) {
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					} else if (token.getString().equals(";") || token.getString().contentEquals(")")) {
						b = false;/////识别表达式语句结束
						////退出识别表达式
					} else if (token.getString().contentEquals(")")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ( ");
						break;
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) 或者 ;  ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 14:
					if (token.getString().equals("&&")) {
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					} else if (token.getString().equals(")") || token.getString().equals(";") || token.getString().contentEquals("||")) {
						parsers.add("B->AB");
						for (int i = 0; i < 2; i++) {
							Symbols.pop();
							States.pop();
						}
						Symbols.push("B");
						B boo = new B();
						B b2 = Bs.pop();//////B2
						B b1 = Bs.pop();//////B1
						boo.truelist.addAll(b2.truelist);
						boo.falselist.addAll(b1.falselist);
						boo.falselist.addAll(b2.falselist);
						boo.startStat = b1.startStat;
						for (int i = 0; i < b1.truelist.size(); i++)
							fours.get(b1.truelist.get(i)).des = b2.startStat + "";
						Bs.push(boo);
						push_States_B();
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) 或者 ;  ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
				case 15:
					if (token.getString().equals("&&")) {
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur);
						cur++;
					} else if (token.getString().equals("||")) {
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur);
						cur++;
					} else if (token.getString().equals(")") || token.getString().equals(";")) {
						parsers.add("B->OB");
						for (int i = 0; i < 2; i++) {
							Symbols.pop();
							States.pop();
						}
						Symbols.push("B");
						B boo = new B();
						B b2 = Bs.pop();/////B2
						B b1 = Bs.pop();/////B1
						boo.startStat = b1.startStat;
						boo.falselist.addAll(b2.falselist);
						boo.truelist.addAll(b1.truelist);
						boo.truelist.addAll(b2.truelist);
						for (int i = 0; i < b1.falselist.size(); i++)
							fours.get(b1.falselist.get(i)).des = b2.startStat + "";
						Bs.push(boo);
						push_States_B();
					} else if (token.getString().equals("(")) {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：可能缺少 ) 或者 ;  ");
						break;
					} else {
						System.out.println("行：in" + token.getLine_no() + "	错误提示：这是无效输入！");
					}
					break;
			}
		}
	}

	private void push_States_O() {
		switch (States.peek()) {
			case 0:
			case 4:
			case 5:
			case 7:
			case 8:
				States.push(8);
				break;
		}
	}

	private void push_States_A() {
		switch (States.peek()) {
			case 0:
			case 4:
			case 5:
			case 7:
			case 8:
				States.push(7);
				break;
		}
	}

	private void push_States_B() {
		switch (States.peek()) {
			case 0:
				States.push(13);
				break;
			case 4:
				States.push(11);
				break;
			case 5:
				States.push(6);
				break;
			case 7:
				States.push(14);
				break;
			case 8:
				States.push(15);
				break;
		}
	}
}
