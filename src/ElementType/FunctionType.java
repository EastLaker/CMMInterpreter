package ElementType;

import Parser.ClassFactory;

import javax.print.attribute.standard.OrientationRequested;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Parser.ClassFactory.*;

/**
 * @author knight
 * @date 2019/11/9 15:55
 * created
 */
public class FunctionType<T> {
    //todo function
    //函数四元式地址
    private int enterDes;
    //形参个数
    private int numOfParam;
    //形参列表
    private List<Word<T>> FormalParam;
    //返回类型
    private TYPE returnValue;
    //辅助变量  读取形参时用
    private int index;
    //返回地址
    private int ret;

    private Register rax;

    public FunctionType(){
        index = -1;
        FormalParam = new LinkedList<>();
    }

    public FunctionType(TYPE returnValue){
        this();
        this.returnValue = returnValue;
    }

    public void addParam(TYPE paramType){
        FormalParam.add(new Word<>(paramType));
        ++numOfParam;
    }

    public Word<T> getParamInIndex(){
        return FormalParam.get(++index);
    }

    public Word<T> popFormalParam(){
        if(index>=0){
            return FormalParam.get(index--);
        }
        return null;
    }

    public boolean checkNumOfParam(){
        return index==numOfParam;
    }

    public int getEnterDes() {
        return enterDes;
    }

    public void setEnterDes(int enterDes) {
        this.enterDes = enterDes;
    }

    public List<Word<T>> getFormalParam() {
        return FormalParam;
    }

    public void setFormalParam(List<Word<T>> formalParam) {
        FormalParam = formalParam;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public Register getRax() {
        return rax;
    }

    public void setRax(Register rax) {
        this.rax = rax;
    }

    public void initRax(){
        rax = new Register(returnValue);
    }
}
