package Parser;


/**
 * @author knight
 */
public class Word<T> {
    //地址0x.....
    private int des;
    //类型   int float ......
    public ClassFactory.TYPE type;

    //数据段地址的起始位置，每次声明新的变量往上增长
    private static int des_start = 0x0;
    private T value;

    public Word(){}

    public Word(ClassFactory.TYPE type) {
        this.type = type;
    }

    public Word(ClassFactory.TYPE type, T value) {
        this.type = type;
        this.value = value;
    }


    public static void setDes_start(int des_start) {
        Word.des_start = des_start;
    }

    public static int getDes_start() {
        int des = des_start;
        des_start += 4;
        return des;
    }

    public static int getDes_start(int size) {
        int des = des_start;
        des_start += size*4;
        return des;
    }

    public int getDes() {
        return des;
    }

    public void setDes(int des){
        this.des = des;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /////转换问题：string与int，float等等转换

    public T getValue() {
        return value;
    }
}
