package Parser;


/**
 * @author knight
 */
public class Word<T> {
    //地址0x.....
    public String des;
    //类型   int float ......
    public ClassFactory.TYPE type;

    public static void setDes_start(int des_start) {
        Word.des_start = des_start;
    }

    //数据段地址的起始位置，每次声明新的变量往上增长
    private static int des_start = 0x0;
    private T value;

    public static String getDes() {
        int des = des_start;
        des_start += 4;
        return des+"";
    }

    public void setValue(Object value) {
        this.value = (T) value;
    }


    public Word(ClassFactory.TYPE type) {
        this.type = type;
    }


    public Word(ClassFactory.TYPE type, T value) {
        this.type = type;
        this.value = value;
    }

    /////转换问题：string与int，float等等转换


    public Object getValue() {
        if (value == null) {
            switch (type) {
                case FLOAT:
                    setValue(0.0f);
                    break;
                case INT:
                    setValue(0);
                    break;
            }
        }
        return value;
    }
}
