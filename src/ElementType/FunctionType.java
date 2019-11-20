package ElementType;

import Parser.ClassFactory;
import Utils.DynamicException;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
/**
 * @author knight
 * @date 2019/11/9 15:55
 * created
 */
public class FunctionType{
    //todo function
    //函数四元式地址

    private List<Word> FormalParam;

    //辅助变量  读取形参时用
    private int index;
    //返回地址
    private int ret;

    private int paramIndex;

    private Stack<Stack<Word>> Scope;

    private Stack<Word> Top;

    private Register rax;

    public FuncSignature signature;

    public FunctionType(){
        index = -1;
        FormalParam = new LinkedList<>();
    }

    public FunctionType(FuncSignature func){
        this.signature = func;
    }

    public void addParam(Word word) throws DynamicException.unequalFunctionParameters,
            DynamicException.dismatchFunctionParameter {
        if(paramIndex==signature.getNumOfParam()) {
            throw new DynamicException().new unequalFunctionParameters();
        }

        if(word.type!= signature.getParamTypeOfIndex(paramIndex)){
            throw new DynamicException().new dismatchFunctionParameter();
        }

        FormalParam.add(word);
        ++paramIndex;
    }

    public void checkExecute() throws DynamicException.unequalFunctionParameters {
        if(signature.getReturnType()!= ClassFactory.TYPE.Void){
            rax = new Register(signature.getReturnType());
        }
        if(paramIndex!=signature.getNumOfParam()){
            throw new DynamicException().new unequalFunctionParameters();
        }

        Scope = new Stack<>();
        Top = new Stack<>();
        Scope.push(Top);
    }

    public Iterable<Word> getParamList(){
        return FormalParam;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getEnterDes(){
        return signature.getEnterDes();
    }

    public Stack<Stack<Word>> getScope() {
        return Scope;
    }

    public Stack<Word> getTop() {
        return Top;
    }

    public void setTop(Stack<Word> stack){
        this.Top = stack;
    }

    public void outOfScope(){
        Top = this.Scope.pop();
    }

    public void GenerateScope(){
        Top = new Stack<>();
        Scope.push(Top);
    }

    public Register getRax() {
        return rax;
    }

    public void setRax(Register rax) {
        this.rax = rax;
    }
}
