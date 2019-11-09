package Utils;

import Parser.FunctionType;
import Parser.Word;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author knight
 * @date 2019/11/9 15:52
 * created
 */
public class DataSturcture {
    private static final int Capacity = 10;
    public static final HashMap<String, Word> Data = new HashMap<>(Capacity);
    public static final HashMap<String, FunctionType> Function = new HashMap<>(Capacity);

    //总调用过程
    public static final Stack<Stack<Word>> Env = new Stack<>();

    //调用过程的指针
    public static Stack<Word> Top;

    //todo 调用前设置形参  调用后置null.
    public static LinkedList<Word> FormalParam;
}
