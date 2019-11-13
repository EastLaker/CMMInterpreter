package Parser;

import ElementType.ArrayType;
import ElementType.Word;
import Utils.DataSturcture;
import Utils.Regex;
import lexical.Token;

import java.util.*;

/**
 * @author lfz
 */
public class Parser {//////////////////识别完成token读到的应该是;
	//语法分析    识别算术表达式   规约规则⬇️
	//0）S->E+E
	//1) E->E+E
	//2)E->E+E
	//3)E->(E)
	//4)E->id
	public static List<String> errors = new ArrayList<String>();
	public List<String> parsers = new ArrayList<String>();

	public List<Token>  tokens = new ArrayList<Token>();/////用于存放词法分析的结果   测试
	public Token token = null;//读入的词
	public int cur = 0;///用于遍历词法分析的词
	public Stack<Integer> states = new Stack<Integer>();/////状态栈------用于赋值表达式的检测
	public Stack<String> symbols = new Stack<String>();/////符号栈----------用于赋值表达式的检测
	public Stack<Integer> States = new Stack<Integer>();
	public Stack<String> Symbols = new Stack<String>();
	public Stack<E> Es = new Stack<E>();
	public Stack<B> Bs = new Stack<B>();
	public Stack<A> As = new Stack<A>();
	public Stack<O> Os = new Stack<O>();
	public List<FourYuan> fours = new ArrayList<FourYuan>();
    public boolean error = false;//////源程序无错fs96
	ClassFactory cf = new ClassFactory();

	public  void parserE() {///////////词法分析程序
		states.push(0);/////将0状态入栈
		boolean b = true;
		while(b) {
			switch (states.peek()){
				case 0:
				case 2:

					if(token.getString().matches(Regex.variPat)||token.getString().matches(Regex.constant)) {
						states.push(3);////状态栈入栈
						element_of_array_E();
						token = tokens.get(cur++);
					}

					else if(token.getString().equals("(")) {
						states.push(2);
						symbols.push("(");
						token = tokens.get(cur++);
					}

					else
						b = error_parserE();

					break;
				case 1:
					if(token.getString().contentEquals("+")||token.getString().contentEquals("-")) {
						states.push(4);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					}

					else if(token.getString().equals("*")||token.getString().equals("/")) {
						states.push(5);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					}

					else if(token.getString().contentEquals(";")||token.getString().contentEquals(",")||token.getString().contentEquals("}")||token.getString().contentEquals("]")
					||token.getString().contentEquals("<")||token.getString().contentEquals(">")||token.getString().contentEquals(">=")||token.getString().contentEquals("<=")||
					token.getString().contentEquals("==")||token.getString().contentEquals("!=")||token.getString().contentEquals(")")) {
						for(int i = 0; i <2 ;i++)
							states.pop();
						symbols.pop();
						b = false;/////识别表达式语句结束
						////退出识别表达式
						/////token==;
					}
					else
                        b = error_parserE();
					break;
				case 3:
					if(token.getString().contentEquals("+")||token.getString().equals("-")||token.getString().equals("*")||
							token.getString().equals("/")||token.getString().contentEquals(")")||token.getString().equals(";")||
							token.getString().equals(",")||token.getString().equals("}")||token.getString().equals("]")
							||token.getString().equals("<")||token.getString().equals(">")||token.getString().equals(">=")||token.getString().equals("<=")||
							token.getString().equals("==")||token.getString().equals("!=")){
						parsers.add("E->id");
						E e = new E();
						e.des = symbols.pop();//出栈一个id
						Es.push(e);
						states.pop();//出栈
						symbols.push(e.des);///////////////////////////////////////////////////////////////todo 修改
						states_push();
					}
					else
						b = error_parserE();

					break;
				case 4:
				case 5:

					if(token.getString().matches(Regex.variPat)||token.getString().matches(Regex.constant)) {
						states.push(3);
						element_of_array_E();
						token = tokens.get(cur++);
					}

					else if(token.getString().contentEquals("(")) {
						states.push(2);
						symbols.push("(");
						token = tokens.get(cur++);
					}

				    else
					    b  = error_parserE();
					break;
				case 6:

					if(token.getString().contentEquals("+")||token.getString().equals("-")) {
						states.push(4);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					}

					else if(token.getString().contentEquals("*")||token.getString().equals("/")) {
						states.push(5);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					}

					else if(token.getString().equals(")")) {
						states.push(9);
						symbols.push(")");
						token = tokens.get(cur++);
					}

					else
						b = error_parserE();
					break;
				case 7:

					if(token.getString().equals("+")||token.getString().equals("-")||token.getString().equals(")")||token.getString().equals(";")||token.getString().equals(",")||
							token.getString().equals("}")||token.getString().equals("]")
							||token.getString().equals("<")||token.getString().equals(">")||token.getString().equals(">=")||token.getString().equals("<=")||
							token.getString().equals("==")||token.getString().equals("!=")){
						//r1规约
						parsers.add("E->E+E/E-E");
						for(int i=0;i<3;i++) {
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
					}

					else if(token.getString().contentEquals("*")||token.getString().equals("/")) {
						states.push(5);
						symbols.push(token.getString());
						token = tokens.get(cur++);
					}

					else
						b = error_parserE();
					break;
				case 8:

					if(token.getString().equals("+")||token.getString().equals("-")||token.getString().contentEquals("*")||
							token.getString().equals("/")||token.getString().equals(")")||token.getString().contentEquals(";")
							||token.getString().equals(",")||token.getString().equals("}")||token.getString().equals("]")
							||token.getString().equals("<")||token.getString().equals(">")||token.getString().equals(">=")||token.getString().equals("<=")||
							token.getString().equals("==")||token.getString().equals("!=")) {
						//r2规约
						parsers.add("E->E*E /  E/E");
						for(int i=0;i<3;i++) {
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
					}

					else
						b = error_parserE();
					break;
				case 9:

					if(token.getString().equals("+")||token.getString().equals("-")||token.getString().contentEquals("*")||
							token.getString().contentEquals("/")||token.getString().equals(")")||token.getString().contentEquals(";")||
							token.getString().equals(",")||token.getString().equals("}")||token.getString().equals("]")
							||token.getString().equals("<")||token.getString().equals(">")||token.getString().equals(">=")||token.getString().equals("<=")||
							token.getString().equals("==")||token.getString().equals("!=")) {
						//r3规约
						parsers.add("E->(E)");
						for(int i=0;i<3;i++) {
							states.pop();
						}
						symbols.pop();
						String s = symbols.pop();
						symbols.pop();
						symbols.push(s);///将规约得到的E入栈
						states_push();
					}
					else
						b = error_parserE();
					break;
			}

		}
	}

	private boolean error_parserE() {
		boolean b;
		error = true;//程序有错
		b = false;
		errors.add("line :" + token.getLine_no() + "  错误输入   "+token.getString());
		while(!(token.getString().contentEquals(";")||token.getString().contentEquals(",")||token.getString().contentEquals("}")||token.getString().contentEquals("]")
				||token.getString().contentEquals("<")||token.getString().contentEquals(">")||token.getString().contentEquals(">=")||token.getString().contentEquals("<=")||
				token.getString().contentEquals("==")||token.getString().contentEquals("!=")||token.getString().contentEquals(")")))
			token = tokens.get(cur++);///todo 退出本次语法分析程序
		return b;
	}

	private void element_of_array_E() {
		String reg_ = null;
		boolean is_element_of_array = false;
		if(tokens.get(cur).getString().equals("[")){///预读一个单词
			/////是一个数组元素
			if(!token.getString().matches(Regex.variPat)){
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
		if(is_element_of_array)
			symbols.push(reg_);
		else
			symbols.push(token.getString());
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

	//L->SL|$
	//S->a;|{L}|if语句|while语句
	public void L() {
		if(token.getString().contentEquals("{")||token.getString().equals("if")||token.getString().contentEquals("while")||token.getString().matches(Regex.variPat)||
		token.getString().matches(Regex.constant)) {
			parsers.add("L->SL");
			S();
			L();
		}

		else if(token.getString().contentEquals("#")) {
			parsers.add("L->$");
			parsers.add("识别结束！程序正确");
		}
	}
	public void S() {

		if(token.getString().contentEquals("{")) {//复合语句
			parsers.add("S->{L}");
			token = tokens.get(cur++);
			L();
			if(token.getString().contentEquals("}")) {
				token = tokens.get(cur++);
			}
			else
			{
				error = true;
				errors.add("line :" + token.getLine_no() + "缺少}");
			}
		}/////复合语句
		else if(token.getString().contentEquals("if")) {////if语句识别
			parsers.add("S->if语句");
			token = tokens.get(cur++);//读入
			if(token.getString().contentEquals("(")) {
				token=tokens.get(cur++);
				////识别逻辑表达式B();
				//TODO 识别逻辑表达式
				parserB();///////////栈顶一个B，真出口链，假出口链回填

				if(token.getString().equals(")")) {
					token = tokens.get(cur++);
					B b = Bs.peek();
					for(int i=0;i<b.truelist.size();i++)
						fours.get(b.truelist.get(i)).des = FourYuan.no+"";////回填真出口
					S();/////if只有一条语句
					/*
					 * {}|a;|if语句|while语句
					 */

					if(token.getString().equals("else")) {
						token = tokens.get(cur++);
						for(int t=0;t<b.falselist.size();t++) {//////回填假出口
							fours.get(b.falselist.get(t)).des = FourYuan.no+1+"";
						}
						int stru = FourYuan.no;
						FourYuan four = new FourYuan();
						four.op1 = "_";
						four.op2 = "_";
						four.oprator = "JMP";
						four.des = null;
						FourYuan.no++;
						fours.add(four);
						////TODO 回填真出口
						S();
						fours.get(stru).des = FourYuan.no+"";
						//	token = tokens[cur++];
					}
					else {//////不带else
						////回填假出口
						for(int j=0;j<b.falselist.size();j++)
							fours.get(b.falselist.get(j)).des = FourYuan.no+"";
					}
				}
				else {
					System.out.println("缺少）");
				}
			}
			else {
				System.out.println("(");
			}
		}
		else if("while".equals(token.getString())) {
			parsers.add("S->while语句");
			token = tokens.get(cur++);
			if("(".equals(token.getString())) {
				token = tokens.get(cur++);
				int stru = FourYuan.no;
				/*
				生成跳转语句返回的地址
			*/
				parserB();
				//////逻辑表达式识别!
				//TODO 识别逻辑表达式

				if(")".equals(token.getString())) {
					token = tokens.get(cur++);
					B b = Bs.peek();
					for(int i=0;i<b.truelist.size();i++)/////回填真出口
						fours.get(b.truelist.get(i)).des = FourYuan.no+"";
					S();////TODO 生成跳转语句
					FourYuan four = new FourYuan();
					four.op1 = "_";
					four.op2 = "_";
					four.oprator = "JMP";
					four.des = stru+"";
					fours.add(four);
					FourYuan.no++;
					for(int j=0;j<b.falselist.size();j++)
						fours.get(b.falselist.get(j)).des = FourYuan.no+"";/////////回填假出口
					//token = tokens[cur++];
				}
			}
			else
				System.out.println("缺少（");
		}

		else if(token.getString().contentEquals("int")||token.getString().contentEquals("float")) {
			///TODO  建立单词表
			String type = token.getString();
			token = tokens.get(cur++);
			addWord(type);
		}
		else if(token.getString().matches(Regex.variPat)) {//赋值语句
			FourYuan four = new FourYuan();
			parsers.add("S->a;");
			String des = token.getString();
			token = tokens.get(cur++);
			boolean is_element_of_array = false;
			if(token.getString().equals("[")){
				//////是一个数组元素
				four.oprator = "$";
				is_element_of_array = true;
				token = tokens.get(cur++);
				parserE();
				four.op1 = des;
				four.op2 = Es.peek().des;

				if(token.getString().equals("]"))
					token = tokens.get(cur++);
				else
					errors.add(token.getString() + "缺少]");
			}
			if(token.getString().contentEquals("=")) {
				token = tokens.get(cur++);
				parserE();
				if(token.getString().contentEquals(";")) {
					if(!is_element_of_array){
						four.oprator = "=";
						four.op1 = Es.peek().des;
						four.op2 = "_";
						four.des = des;}
					else{
						four.des = Es.peek().des;
					}
					fours.add(four);
					FourYuan.no++;
					token = tokens.get(cur++);
				}
			}

		}
		else if(token.getString().matches(Regex.constant)){
			String s = token.getString();
			int no = token.getLine_no();
			token = tokens.get(cur++);
			if(token.getString().contentEquals("=")){
				Parser.errors.add("line: "+ (no+1) +"error : 给常量" + s + "赋值");
			}
			while(!token.getString().contentEquals(";")&&!token.getString().contentEquals("#"))
				token = tokens.get(cur++);
		}
	}

	private void addWord(String type) {
		if (token.getString().matches(Regex.variPat)) {
			String name = token.getString();
			Word word = cf.newWordFromType(type);
			word.setDes(Word.getDes_start());
			boolean isAdded = putWordIn(word);
			//  if括弧的范围对不对？
			token = tokens.get(cur++);
			if(isAdded){
				is_Array(type, name, word.getDes());
				///是数组吗？
				to_assign(name);
				///赋值吗？
			}
			T(type);
		}
	}

	private void is_Array(String type, String name, int start_des) {
		int size = 0;//数组大小

		if ("[".equals(token.getString())) {
			token = tokens.get(cur++);
			//数组是否以={}的形式初始化.
			boolean length_determined = false;////数组长度没有确定
			if (token.getString().matches(Regex.positiveInt)) {
				///token.getString()有两种情况 1是常数，那么数组长度确定（size确定，length determined为true）
				//2是]直接跳出这一层if
				length_determined = true;/////数组长度确定
				size = Integer.parseInt(token.getString());////数组长度确定
				///////初始化一个word
				ArrayType array = cf.newArrayFromType(type, size);
				array.setDes(start_des);

				Word.getDes_start(size - 1);
				DataSturcture.Wordlist.remove(name);

				DataSturcture.Wordlist.put(name, array);
				token = tokens.get(cur++);
			}

			if (!"]".equals(token.getString())) {
				//todo 缺少右括号
			} else token = tokens.get(cur++);
			//模拟读入 "=" 不做处理 有无都可
			if ("=".equals(token.getString())) token = tokens.get(cur++);
			if ("{".equals(token.getString())) {/////数组需要初始化吗？
				ArrayList<Number> var_array = new ArrayList<>();////用于初始化单词表的数组
				int j = 0;
				token = tokens.get(cur++);
				while (token.getString().matches(Regex.constant)||token.getString().matches(Regex.regPat)) {
					parserE();
					FourYuan four = new FourYuan();
					four.oprator = "$";
					four.des = Es.peek().des;
					four.op1 = name;
					four.op2 = j + "";
					fours.add(four);
					FourYuan.no++;
					j++;

					if (",".equals(token.getString()))
						token = tokens.get(cur++);
					else break;
				}
				//todo  将越界检测移到while中会更安全
				if (length_determined) {
					if (j > size)
						errors.add("数组赋值越界 \n");
					else if (j < size)
						for (; j < size; j++){
							FourYuan fourYuan = new FourYuan();
							fourYuan.op1 = name;
							fourYuan.op2 = j+"";
							fourYuan.oprator = "$";
							fourYuan.des = 0 + "";
							fours.add(fourYuan);
							FourYuan.no++;
						}
					////多余元素用0补足
				}
				ArrayType arrayType = cf.newArrayFromArray(new Number[length_determined ? size : j], type);
				if (!length_determined)
					Word.getDes_start(j - 1);
				arrayType.setDes(start_des);
				DataSturcture.Wordlist.remove(name);
				DataSturcture.Wordlist.put(name, arrayType);

				if (!"}".equals(token.getString())) {
					//todo 错误匹配 数组赋值表达式没有终结符.
				} else {
					token = tokens.get(cur++);
				}
			}
		}
	}


	private void to_assign(String name) {

		if("=".equals(token.getString())){
			//声明时赋值运算
			FourYuan four = new FourYuan();
			four.oprator = token.getString();//=
			four.des = name;
			token = tokens.get(cur++);
			parserE();
			if(error)
				return;
			four.op1 = Es.peek().des;
			four.op2 = "_";
			FourYuan.no++;
			fours.add(four);
		}
	}


	private boolean putWordIn(Word word) {

		if (DataSturcture.Wordlist.containsKey(token.getString())) {
			errors.add("same variable exception");
			return false;
		} else {
			DataSturcture.Wordlist.put(token.getString(), word);
			return true;
		}
	}

	void T(String type) {
		///TODO   声明语句

		if(token.getString().contentEquals(";")) {
			token = tokens.get(cur++);
			return;
		}
		else if(token.getString().contentEquals(",")) {
			token = tokens.get(cur++);
			addWord(type);
		}
	}
	void parserB() {/////布尔表达式
		States.push(0);
		boolean b = true;
		while(b) {
			switch (States.peek()){
				case 0:

					if(token.getString().matches(Regex.variPat)||token.getString().matches(Regex.constant)){
						States.push(1);
						parserE();
						Symbols.push(Es.peek().des);
					}

					else if (token.getString().equals("(")){
						States.push(4);
						Symbols.push("(");
						token = tokens.get(cur++);
					}

					else if(token.getString().equals("!")){
						States.push(5);
						Symbols.push("!");
						token = tokens.get(cur++);
					}

					else if(token.getString().equals(")")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 1:
					if (token.getString().equals(">")||token.getString().equals(">=")||token.getString().equals("<")||token.getString().equals("<=")||token.getString().equals("!=")||token.getString().equals("==")){
						States.push(2);
						Symbols.push(token.getString());//rop符号
						token = tokens.get(cur++);
					} else if (token.getString().equals(")")||token.getString().equals("&&")||token.getString().equals("||")||token.getString().equals(";")){
						parsers.add("B->id");
						States.pop();//出栈状态栈出栈一个
						/*Symbols.pop();**/   //符号栈出栈一个
						B boo = new B();
						boo.op1 = Symbols.pop();
						Symbols.push("B");
						push_States_B();
					}

					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 2:
					if (token.getString().matches(Regex.variPat)||token.getString().matches(Regex.constant)){
						States.push(3);
						parserE();
						Symbols.push(Es.peek().des);
					}

					else if (token.getString().contentEquals(")")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else if(token.getString().contentEquals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 3:
					if (token.getString().equals(")")||token.getString().equals("&&")||token.getString().equals("||")||token.getString().equals(";")){
						parsers.add("B->i rop i");
						for (int i=0;i<3;i++){
							States.pop();
						}
						B boo = new B();
						boo.op2 = Symbols.pop();
						boo.op = Symbols.pop();
						boo.op1 = Symbols.pop();
						boo.truelist.add(FourYuan.no);
						boo.falselist.add(FourYuan.no+1);
						boo.startStat = FourYuan.no;
						Bs.push(boo);
						FourYuan four1 = new FourYuan();
						four1.op1 = boo.op1;
						four1.op2 = boo.op2;
						four1.oprator = "J"+boo.op;
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
					}

					else if(token.getString().contentEquals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 4:
				case 5:
				case 7:
				case 8:

					if (token.getString().matches(Regex.variPat)||token.getString().matches(Regex.constant)){
						States.push(1);
						parserE();
						Symbols.push(Es.peek().des);
					}

					else if (token.getString().equals("(")){
						States.push(4);
						Symbols.push("(");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals("!")){
						States.push(5);
						Symbols.push("!");
						token = tokens.get(cur++);
					}

					else if(token.getString().contentEquals(")")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 6:
					if (token.getString().equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals(")")||token.getString().equals(";")){
						System.out.println("B->!B");
						for (int i=0;i<2;i++){
							States.pop();
							Symbols.pop();
						}
						B boo = Bs.pop();
						List list = boo.truelist;
						boo.truelist = boo.falselist;
						boo.falselist = list;////////真假出口交换
						Bs.push(boo);
						Symbols.push("B");
						push_States_B();
					}

					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 9:
					if (token.getString().equals("(")||token.getString().equals("!")||token.getString().matches(Regex.variPat)||
							token.getString().matches(Regex.constant)){
						parsers.add("A->B&&");
						for (int i=0;i<2;i++){
							Symbols.pop();
							States.pop();
						}
						A a = new A();
						a.b = Bs.peek();
						As.push(a);
						Symbols.push("A");
						push_States_A();
					}

					else if(token.getString().equals(")")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 10:
					if (token.getString().equals("(")||token.getString().equals("!")||token.getString().matches(Regex.variPat)||
							token.getString().matches(Regex.constant)){
						parsers.add("O->B||");
						for (int i=0;i<2;i++){
							Symbols.pop();
							States.pop();
						}
						Symbols.push("O");
						O o = new O();
						o.b = Bs.peek();
						Os.push(o);
						push_States_O();
					}

					else if(token.getString().equals(")")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 11:
					if (token.getString().equals(")")){
						States.push(12);
						Symbols.push(")");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					}

					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) 或者 ; ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 12:
					if (token.getString().equals(")")||token.getString().equals("&&")||token.getString().equals("||")||
							token.getString().equals(";")){
						parsers.add("B->(B)");
						for (int i=0;i<3;i++){
							Symbols.pop();
							States.pop();
						}
						Symbols.push("B");
						push_States_B();
					}

					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 13:
					if (token.getString().equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals(";")||token.getString().contentEquals(")")){
						b = false;/////识别表达式语句结束
						////退出识别表达式
					}
					else if(token.getString().contentEquals(")")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 14:
					if (token.getString().equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}

					else if (token.getString().equals(")")||token.getString().equals(";")||token.getString().contentEquals("||")){
						parsers.add("B->AB");
						for (int i=0;i<2;i++){
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
						for(int i=0;i<b1.truelist.size();i++)
							fours.get(b1.truelist.get(i)).des = b2.startStat+"";
						Bs.push(boo);
						push_States_B();
					}

					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 15:
					if (token.getString().equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur);
						cur++;
					}

					else if (token.getString().equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur);
						cur++;
					}

					else if (token.getString().equals(")")||token.getString().equals(";")){
						parsers.add("B->OB");
						for (int i=0;i<2;i++){
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
						for(int i=0;i<b1.falselist.size();i++)
							fours.get(b1.falselist.get(i)).des = b2.startStat+"";
						Bs.push(boo);
						push_States_B();
					}

					else if(token.getString().equals("(")){
						System.out.println("行：in"+token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
			}
		}
	}
	private void push_States_O() {
		switch(States.peek()) {
			case 0:
			case 4:
			case 5:
			case 7:
			case 8:
				States.push(8);break;
		}
	}
	private void push_States_A() {
		switch(States.peek()) {
			case 0:
			case 4:
			case 5:
			case 7:
			case 8:
				States.push(7);break;
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
