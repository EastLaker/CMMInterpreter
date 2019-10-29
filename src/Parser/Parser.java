package Parser;

import java.awt.FileDialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {//////////////////识别完成token读到的应该是;
//语法分析    识别算术表达式   规约规则⬇️
	//0）S->E+E
	//1) E->E+E
	//2)E->E+E
	//3)E->(E)
	//4)E->id
	public static List<String> errors = new ArrayList<String>();
	public List<String> parsers = new ArrayList<String>();
	public List<String>  tokens = new ArrayList<String>();/////用于存放词法分析的结果   测试
	public String token = null;//读入的词
	public int cur = 0;///用于遍历词法分析的词
	public String m_id = "^[A-Za-z_][A-Za-z0-9_]*$";
	public String m_int = "^[+/-]?[0-9]*$";
	public Stack<Integer> states = new Stack<Integer>();/////状态栈------用于赋值表达式的检测
	public Stack<String> symbols = new Stack<String>();/////符号栈----------用于赋值表达式的检测
	public Stack<Integer> States = new Stack<Integer>();
	public Stack<String> Symbols = new Stack<String>();
	public Stack<E> Es = new Stack<E>();
	public Stack<B> Bs = new Stack<B>();
	public Stack<A> As = new Stack<A>();
	public Stack<O> Os = new Stack<O>();
	public Token line_token = new Token();
	public List<FourYuan> fours = new ArrayList<FourYuan>();

	ClassFactory cf = new ClassFactory();

	public  void parserE() {///////////词法分析程序
		states.push(0);/////将0状态入栈
		//token = tokens[cur];////
		//cur++;/////模拟读入
		boolean b = true;
        while(b) {
        	switch (states.peek()){
        	case 0:
        		if(token.matches(m_id)||token.matches(m_int)) {
        			states.push(3);////状态栈入栈
        			symbols.push(token);
        			token = tokens.get(cur++);
        		}
        		else if(token.equals("(")) {
        			states.push(2);
        			symbols.push("(");
        			token = tokens.get(cur++);
        		}
        		else if(token.equals(")")){
        			errors.add("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
				}
        		else {
					errors.add("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 1:
        		if(token.equals("+")) {
        			states.push(4);
        			symbols.push("+");
        			token = tokens.get(cur);
        			cur++;
        		}
        		else if(token.equals("*")) {
        			states.push(5);
        			symbols.push("*");
        			token = tokens.get(cur);
        			cur++;
        		}
        		else if(token.equals(";")||token.equals(",")) {
        			b = false;/////识别表达式语句结束
        			////退出识别表达式
        			/////token==;
        		}
				else if(token.equals(")")){
					errors.add("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
				}
				else if(token.equals("(")){
					errors.add("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
				}
				else {
					errors.add("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 2:
        		if(token.matches(m_id)||token.matches(m_int)) {
        			states.push(3);
        			symbols.push(token);
        			token = tokens.get(cur++);
        		}
        		else if(token.equals("(")) {
        			states.push(2);
        			symbols.push("(");
        			token = tokens.get(cur++);
        		}
				else if(token.equals(")")){
					errors.add("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
				}
				else {
					errors.add("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 3:
        		if(token.contentEquals("+")||token.equals("*")||token.contentEquals(")")||token.equals(";")||
				token.equals(",")){
        			parsers.add("E->id");
        			E e = new E();
        			e.des=symbols.pop();//出栈一个id
        			Es.push(e);
        			states.pop();//出栈
        			symbols.push("E");
					states_push();
				}
        		else	{
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 4:
        	case 5:
        		if(token.matches(m_id)||token.matches(m_int)) {
        			states.push(3);
        			symbols.push(token);
        			token = tokens.get(cur++);
        		}
        		else if(token.contentEquals("(")) {
        			states.push(2);
        			symbols.push("(");
        			token = tokens.get(cur++);
        		}
				else if(token.equals(")")){
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
				}
				else {
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 6:
        		if(token.contentEquals("+")) {
        			states.push(4);
        			symbols.push("+");
        			token = tokens.get(cur);
        			cur++;
        		}
        		else if(token.contentEquals("*")) {
        			states.push(5);
        			symbols.push("*");
        			token = tokens.get(cur);
        			cur++;
        		}
        		else if(token.equals(")")) {
        			states.push(9);
        			symbols.push(")");
        			token = tokens.get(cur);
        			cur++;
        		}
				else if(token.equals("(")){
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
				}
				else {
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 7:
        		if(token.equals("+")||token.equals(")")||token.equals(";")||token.equals(",")){
        			//r1规约   
        			parsers.add("E->E+E");
        			for(int i=0;i<3;i++) {
        				states.pop();/////语法动作，符号栈出栈三次，状态栈出栈三次
        			}
        			E e = new E();
        			e.des = E.getReg();
        			symbols.pop();/////出栈E
        			String op2 = Es.pop().des;///////获得源操作数2
        			String op = symbols.pop();//////出栈运算符+
        			symbols.pop();/////出栈E
        			String op1 = Es.pop().des;
        			FourYuan four = new FourYuan();
        			four.des = e.des;
        			four.op1 = op1;
        			four.op2 = op2;
        			four.oprator = op;
        			FourYuan.no++;////指令序号加一
        			fours.add(four);
        			Es.push(e);
        			symbols.push("E");
					states_push();
				}
        		else if(token.contentEquals("*")) {
        			states.push(5);
        			symbols.push("*");
        			token = tokens.get(cur);
        			cur++;
        		}
				else if(token.equals("(")){
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
				}
				else {
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 8:
        		if(token.equals("+")||token.contentEquals("*")||token.equals(")")||token.contentEquals(";")
				||token.equals(",")) {
        			//r2规约
        			parsers.add("E->E*E");
        			for(int i=0;i<3;i++) {
        				states.pop();
        			}
        			E e = new E();
        			e.des = E.getReg();
        			symbols.pop();
        			String op2 = Es.pop().des;
        			String op = symbols.pop();
        			symbols.pop();
        			String op1 = Es.pop().des;
        			Es.push(e);
        			symbols.push("E");///将规约得到的E入栈
        			FourYuan four = new FourYuan();
        			four.oprator = op;
        			four.op1 = op1;
        			four.op2 = op2;
        			four.des = e.des;
        			fours.add(four);
        			FourYuan.no++;///
					states_push();
				}
				else if(token.equals("(")){
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
				}
				else {
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	case 9:
        		if(token.equals("+")||token.contentEquals("*")||token.equals(")")||token.contentEquals(";")||
				token.equals(",")) {
        			//r3规约
        			parsers.add("E->(E)");
        			for(int i=0;i<3;i++) {
        				states.pop();
        				symbols.pop();
        			}
        			symbols.push("E");///将规约得到的E入栈
					states_push();
				}
				else if(token.equals("(")){
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
				}
				else {
					System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
				}
        		break;
        	}
        		
        }
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
		if(token.contentEquals("{")||token.equals("if")||token.contentEquals("while")||token.matches(m_id)) {
			parsers.add("L->SL");
			S();
			L();
		}
		else if(token.contentEquals("#")) {
			parsers.add("L->$");
			parsers.add("识别结束！程序正确");
		}
	}
	public void S() {

		if(token.contentEquals("{")) {//复合语句
			parsers.add("S->{L}");
			token = tokens.get(cur++);
			L();
	        if(token.contentEquals("}")) {
	        	token = tokens.get(cur++);
	        }
		}
		else if(token.contentEquals("if")) {////if语句识别
			parsers.add("S->if语句");
			token = tokens.get(cur++);//读入
			if(token.contentEquals("(")) {
				token=tokens.get(cur++);
			    ////识别逻辑表达式B();
				//TODO 识别逻辑表达式
				parserB();///////////栈顶一个B，真出口链，假出口链回填
			    if(token.equals(")")) {
			    	token = tokens.get(cur++);
			    	B b = Bs.peek();
			    	for(int i=0;i<b.truelist.size();i++)
			    		fours.get(b.truelist.get(i)).des = FourYuan.no+"";////回填真出口
			    	S();/////if只有一条语句    
			    	/*
			    	 * {}|a;|if语句|while语句
			    	 */
			    	if(token.equals("else")) {
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
		else if(token.equals("while")) {
			parsers.add("S->while语句");
			token = tokens.get(cur++);
			if(token.equals("(")) {
				token = tokens.get(cur++);
				int stru = FourYuan.no;
				/*
				生成跳转语句返回的地址
			*/
				parserB();
				//////逻辑表达式识别!
				//TODO 识别逻辑表达式
				if(token.equals(")")) {
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
		else if(token.contentEquals("int")||token.contentEquals("float")) {
			///TODO  建立单词表
			String type = token;
			token = tokens.get(cur++);
			addWord(type);
		}
		else if(token.matches(m_id)) {//赋值语句
			parsers.add("S->a;");
			String des = token;
			token = tokens.get(cur++);
			if(token.contentEquals("=")) {
				token = tokens.get(cur++);			
			parserE();
			if(token.contentEquals(";")) {
				FourYuan four = new FourYuan();
				four.oprator = "=";
				four.op1 = Es.peek().des;
				four.op2 = "_";
				four.des = des;
				fours.add(four);
				FourYuan.no++;
				token = tokens.get(cur++);
				}
			}
		}
	}

	private void addWord(String type) {
		if (token.matches(m_id)) {
			String name = token;
			Word word = cf.newWordFromType(type);
			word.des = Word.des_start + "";
			Word.des_start += 4;
			putWordIn(word);
			token = tokens.get(cur++);
			is_Array(type, name);
			///是数组吗？
			to_assign(name);
			///赋值吗？
			T(type);
		}
	}

	private void to_assign(String name) {
		if(token.equals("=")){
			//声明时赋值运算
			FourYuan four = new FourYuan();
			four.oprator = token;//=
			four.des = name;
			token = tokens.get(cur++);
			parserE();
			four.op1 = Es.peek().des;
			four.op2 = "_";
			FourYuan.no++;
			fours.add(four);
		}
	}

	private void is_Array(String type, String name) {
		if(token.equals("[")) {
			token = tokens.get(cur++);
			int size = Integer.parseInt(token);///数组大小
			Word word0 = ClassFactory.Wordlist.get(name);
			ClassFactory.Wordlist.remove(name);
			ClassFactory.Wordlist.put(name+"["+0+"]",word0);
				for(int i=1;i<=size-1;i++){
					Word word1 = cf.newWordFromType(type);
					word1.des = Word.des_start+"";
					Word.des_start += 4;
					ClassFactory.Wordlist.put(name+"["+i+"]",word1);
				}
			token = tokens.get(cur++);
			if (!token.equals("]")) {
				//todo 缺少右括号
			}
			else token = tokens.get(cur++);
		}
	}

	private void putWordIn(Word word) {
		if (ClassFactory.Wordlist.containsKey(token)) {
			errors.add("same variable exception");
		} else {
			ClassFactory.Wordlist.put(token, word);
		}
	}

	void T(String type) {
	///TODO   声明语句
	if(token.contentEquals(";")) {
		token = tokens.get(cur++);
		return;
	}
	else if(token.contentEquals(",")) {
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
					if(token.matches(m_id)||token.matches(m_int)){
						States.push(1);
						Symbols.push(token);
						token = tokens.get(cur++);
					}
					else if (token.equals("(")){
						States.push(4);
						Symbols.push("(");
						token = tokens.get(cur++);
					}
					else if(token.equals("!")){
						States.push(5);
						Symbols.push("!");
						token = tokens.get(cur++);
					}
					else if(token.equals(")")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 1:
					if (token.equals(">")||token.equals(">=")||token.equals("<")||token.equals("<=")||token.equals("!=")||token.equals("==")){
						States.push(2);
						Symbols.push(token);//rop符号
						token = tokens.get(cur++);
					} else if (token.equals(")")||token.equals("&&")||token.equals("||")||token.equals(";")){
						parsers.add("B->id");
						States.pop();//出栈状态栈出栈一个
						/*Symbols.pop();**/   //符号栈出栈一个
						B boo = new B();
						boo.op1 = Symbols.pop();
						Symbols.push("B");
						push_States_B();
					}
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 2:
					if (token.matches(m_id)||token.matches(m_int)){
						States.push(3);
						Symbols.push(token);
						token = tokens.get(cur++);
					}
					else if (token.contentEquals(")")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else if(token.contentEquals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 3:
					if (token.equals(")")||token.equals("&&")||token.equals("||")||token.equals(";")){
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
					else if(token.contentEquals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 4:
				case 5:
				case 7:
				case 8:
					if (token.matches(m_id)||token.matches(m_int)){
						States.push(1);
						Symbols.push(token);
						token = tokens.get(cur++);
					}
					else if (token.equals("(")){
						States.push(4);
						Symbols.push("(");
						token = tokens.get(cur++);
					}
					else if (token.equals("!")){
						States.push(5);
						Symbols.push("!");
						token = tokens.get(cur++);
					}
					else if(token.contentEquals(")")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 6:
					if (token.equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}
					else if (token.equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					}
					else if (token.equals(")")||token.equals(";")){
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
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 9:
					if (token.equals("(")||token.equals("!")||token.matches(m_id)||token.matches(m_int)){
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
					else if(token.equals(")")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 10:
					if (token.equals("(")||token.equals("!")||token.matches(m_id)||token.matches(m_int)){
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
					else if(token.equals(")")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 11:
					if (token.equals(")")){
						States.push(12);
						Symbols.push(")");
						token = tokens.get(cur++);
					}
					else if (token.equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}
					else if (token.equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					}
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) 或者 ; ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 12:
					if (token.equals(")")||token.equals("&&")||token.equals("||")||token.equals(";")){
						parsers.add("B->(B)");
						for (int i=0;i<3;i++){
							Symbols.pop();
							States.pop();
						}
						Symbols.push("B");
						push_States_B();
					}
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 13:
					if (token.equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}
					else if (token.equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur++);
					}
					else if (token.equals(";")||token.contentEquals(")")){
						b = false;/////识别表达式语句结束
						////退出识别表达式
					}
					else if(token.contentEquals(")")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ( ");
						break;
					}
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 14:
					if (token.equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur++);
					}
					else if (token.equals(")")||token.equals(";")||token.contentEquals("||")){
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
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
					}
					break;
				case 15:
					if (token.equals("&&")){
						States.push(9);
						Symbols.push("&&");
						token = tokens.get(cur);
						cur++;
					}
					else if (token.equals("||")){
						States.push(10);
						Symbols.push("||");
						token = tokens.get(cur);
					cur++;
					}
					else if (token.equals(")")||token.equals(";")){
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
					else if(token.equals("(")){
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：可能缺少 ) 或者 ;  ");
						break;
					}
					else {
						System.out.println("行：in"+line_token.getLine_no()+"	错误提示：这是无效输入！");
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

	private static Frame frame;
	public static void main(String[] args) {///////////////////TODO主函数
		// TODO Auto-generated method stub
		Parser parse = new Parser();///////分析实例
		FileDialog fileDialog = new FileDialog(frame,"test file",FileDialog.LOAD);
		fileDialog.setVisible(true);
		String str_file = fileDialog.getDirectory() + fileDialog.getFile();
		//建立一个LexicalParser对象，构造函数的参数为代码文件的地址
		LexicalParser lexicalParser = new LexicalParser(str_file);
		//使用getAllTokens()方法获取Tokens,返回一个包含了识别出的Tokens的ArrayList
		List<String> tokens = lexicalParser.getAllTokens();
		for (String token: tokens) {
			parse.tokens.add(token);
		}
		parse.token = parse.tokens.get(parse.cur++);////读入第一个单词
		parse.L();
		////测试
		System.out.println("算术表达式栈顶的存放位置："+parse.Es.peek().des);
		System.out.println("识别出算术表达式的数量："+parse.symbols.size());
		System.out.println("算术表达式状态栈栈顶：（正确时应该为1）"+parse.states.peek());
		System.out.println("栈顶逻辑表达式需要回填的真出口链:");
		for(int i=0;i<parse.Bs.peek().truelist.size();i++) {
		System.out.print(parse.Bs.peek().truelist.get(i)+"   ");
		}
		System.out.println("");
		System.out.println("需要回填的假出口链：");
		for(int i=0;i<parse.Bs.peek().falselist.size();i++) {
			System.out.print(parse.Bs.peek().falselist.get(i)+"   ");
		}
		System.out.println("");
		System.out.println(parse.Bs.size());
		System.out.println("token:"+parse.token);
		for(int i=0;i<parse.fours.size();i++) {
			System.out.print(i+":   ");
			parse.fours.get(i).print();
		}
		System.out.println("下一条指令地址："+FourYuan.no);
	}
}
