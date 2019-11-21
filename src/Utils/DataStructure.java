package Utils;

import ElementType.FuncSignature;
import ElementType.FunctionType;
import ElementType.Register;
import ElementType.Word;
import Parser.ClassFactory;

import java.util.*;

/**
 * @author knight
 * @date 2019/11/9 15:52
 * created
 */
public class DataStructure {
    //是否已经进入main函数
    public static Boolean inMain;
    //寄存器表
    public static HashMap<String, Register> Registers;
    //初始化大小
    private static final int Capacity;
    //全局变量
    public static final HashMap<String, Word> Datas;
    //函数表
    public static final HashMap<String, FuncSignature> Functions;
    //总调用过程
    public static final Stack<FunctionType> Env;
    //创建运行线程的main函数
    public static final FuncSignature MAIN;
    //调用过程的指针
    public static FunctionType Top;
    public static int Ret;
    //返回寄存器
    public static Register rax;
    //初始化数据结构
    static {
        Capacity = 10;
        inMain = false;

        MAIN = new FuncSignature();
        MAIN.setReturnType(ClassFactory.TYPE.INT);

        Env = new Stack<>();
        Datas = new HashMap<>(Capacity);
        Functions = new HashMap<>(Capacity);
        Registers = new HashMap<>();

        Registers.put("reg_rax",new Register());
    }

}
