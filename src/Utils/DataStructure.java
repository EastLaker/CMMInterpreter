package Utils;

import ElementType.FuncSignature;
import ElementType.FunctionType;
import ElementType.Register;
import ElementType.Word;

import java.util.*;

/**
 * @author knight
 * @date 2019/11/9 15:52
 * created
 */
public class DataStructure {
    public static Boolean inMain = false;

    public static HashMap<String, Register> Registers = new HashMap<>();

    private static final int Capacity = 10;
    public static final HashMap<String, Word> Datas = new HashMap<>(Capacity);
    public static final HashMap<String, FuncSignature> Functions = new HashMap<>(Capacity);

    //总调用过程
    public static final Stack<FunctionType> Env = new Stack<>();
    public static final FuncSignature MAIN = new FuncSignature();

    //调用过程的指针
    public static FunctionType Top;


    //todo 调用前设置形参  调用后置null.
    public static int Ret;

    public static Register rax;

}
