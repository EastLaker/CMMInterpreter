package Parser;

public class Word {
public String name;//变量名
public String des;//地址0x.....
public String type;//类型   int float ......
public static int des_start=0x0;//数据段地址的起始位置，每次声明新的变量往上增长
/////转换问题：string与int，float等等转换
public String value;
public int get_int_val() {
	return Integer.parseInt(this.value);
}
}
