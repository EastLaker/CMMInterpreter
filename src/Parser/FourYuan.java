package Parser;

public class FourYuan {
//四元式的类
	public String oprator;//运算符
	public String op1;//第一个源操作数
	public String op2;//第二个源操作数
	public String des;//1⃣️存放中间变量（算术指令）2⃣️地址出口（跳转指令）
	public void Exec() {
		if(this.oprator.contentEquals("+")) {
			///TODO   加法运算
			///TODO   在单词表中找到第一个操作数（按照op1的名）
			///TODO    在单词表中找到第二个操作数（按照op2的名）
			///TODO    运算
			///TODO    保存到寄存器
		}
		else if(this.oprator.contentEquals("*")) {
			/////TODO 乘法运算
		}
		else if(this.oprator.contentEquals("JMP")) {
			/////TODO 直接跳转
		}
		else if(this.oprator.contentEquals("=")) {
			/////TODO 赋值指令
			if(this.op1.matches("^[+/-]?[0-9]*$")) {///赋值成常数
				int j = 0;
				for(;j<Parser.Wordlist.size();j++) {
					if(Parser.Wordlist.get(j).name.contentEquals(this.des))
						break;
				}
				if(j==Parser.Wordlist.size())////TODO  一段错误处理
					Parser.errors.add("未声明标志符"+this.des+"\n");
				else
				Parser.Wordlist.get(j).value = this.op1;
			}
		}
		else if(this.oprator.contentEquals("J<")) {
			/////TODO 选择跳转
		}
		/////执行四元式
	}
	public static int no = 0;/////指令序列
	public void print() {
		System.out.println("("+this.oprator+","+this.op1+","+this.op2+","+this.des+")");
	}

	public String get_four_str() {
		return "("+this.oprator+","+this.op1+","+this.op2+","+this.des+")";
	}
}
