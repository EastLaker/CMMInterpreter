package Parser;

public class FourYuan {
//四元式的类
	public String oprator;//运算符
	public String op1;//第一个源操作数
	public String op2;//第二个源操作数
	public String des;//1⃣️存放中间变量（算术指令）2⃣️地址出口（跳转指令）
	public void Exec() {
		/////执行四元式
	}
	public static int no = 0;/////指令序列
	public void print() {
		System.out.println("("+this.oprator+","+this.op1+","+this.op2+","+this.des+")");
	}
}
