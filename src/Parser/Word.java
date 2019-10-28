package Parser;

import java.lang.reflect.Type;

/**
 * @author knight
 */
public class Word<T> {
    //地址0x.....
	public String des;
    //类型   int float ......
	public ClassFactory.TYPE type;
    //数据段地址的起始位置，每次声明新的变量往上增长
	public static int des_start = 0x0;
    private T value;

    public void setValue(T value) {
        this.value = value;
    }


    public Word(ClassFactory.TYPE type){
        this.type = type;
    }

	/////转换问题：string与int，float等等转换

	public T getValue() {
		return value;
	}
}
