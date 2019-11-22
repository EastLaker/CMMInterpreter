package ElementType;

import Parser.ClassFactory;

import java.util.LinkedList;

/**
 * @author knight
 */
public class Word<T> {
//todo word 的type的识别  可以用int/float.class去识别
    //数据段地址的起始位置，每次声明新的变量往上增长
    private static int des_start = 0x0;

    public int length;
    private int des;
    private String name;
    public ClassFactory.TYPE type;
    private T value;
    private T type1;

    public Word(T type1){this.type1 = type1;}

    public Word(){}

    public Word(ClassFactory.TYPE type) {
        this.type = type;
    }

    public Word(ClassFactory.TYPE type, T value) {
        this.type = type;
        this.value = value;
        LinkedList<Integer[]> linkedList = new LinkedList<>();
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

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
