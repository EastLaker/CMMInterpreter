package ElementType;

import Parser.ClassFactory;

import java.util.*;

/**
 * @author knight
 * @date 2019/11/20 8:52
 * created
 */
public class FuncSignature<T> {
    //todo function
    //函数四元式地址
    private int enterDes;
    //形参个数
    private int numOfParam;
    //形参列表
    private List<ClassFactory.TYPE> FormalParamType;
    //返回类型
    private ClassFactory.TYPE returnType;
    //des of out of scope
    private int exitDes;

    public FuncSignature(){
        FormalParamType = new LinkedList<>();
    }

    public FuncSignature(ClassFactory.TYPE returnType, int enterDes){
        this();
        this.returnType = returnType;
        this.enterDes = enterDes;
    }

    public void addParam(ClassFactory.TYPE paramType){
        FormalParamType.add(paramType);
        ++numOfParam;
    }

    public ClassFactory.TYPE getParamTypeOfIndex(int index){
        return FormalParamType.get(index);
    }

    public int getEnterDes() {
        return enterDes;
    }

    public int getNumOfParam() {
        return numOfParam;
    }

    public void setNumOfParam(int numOfParam) {
        this.numOfParam = numOfParam;
    }

    public List<ClassFactory.TYPE> getFormalParamType() {
        return FormalParamType;
    }

    public void setFormalParamType(List<ClassFactory.TYPE> formalParamType) {
        FormalParamType = formalParamType;
    }

    public ClassFactory.TYPE getReturnType() {
        return returnType;
    }

    public void setReturnType(ClassFactory.TYPE returnType) {
        this.returnType = returnType;
    }

    public int getExitDes() {
        return exitDes;
    }

    public void setExitDes(int exitDes) {
        this.exitDes = exitDes;
    }
}
