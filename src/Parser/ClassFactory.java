package Parser;

import java.util.HashMap;

/**
 * @author knight
 * @date 2019/10/25 20:17
 * created
 */
public class ClassFactory {
    public static HashMap<String,Word> Wordlist = new HashMap<>();
    public static HashMap<String,Register> Registers = new HashMap<>();

    private String _int = "^[+/-]?[0-9]*$";
    private String _float = "[0-9]+\\.?[0-9]+";

    public enum TYPE{
        INT,  //识别的声明类型 int
        FLOAT, //识别float
    }

    public  Word newWordFromValue(String str){
        switch (getTypeFromNum(str)){
            case INT:
                int i = Integer.parseInt(str);
                return new Word<>(TYPE.INT, i);
            case FLOAT:
                float f = Float.parseFloat(str);
                return new Word<>(TYPE.FLOAT, f);
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

    public  Word newWordFromType(String str){
        switch (getTypeFromType(str)){
            case INT:
                return new Word<Integer>(TYPE.INT);
            case FLOAT:
                return new Word<Float>(TYPE.FLOAT);
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

        public void setWordValue(Word word,String str) {
            switch (getTypeFromNum(str)) {
                case INT:
                    word.setValue(Integer.parseInt(str));
                    break;
                case FLOAT:
                    word.setValue(Float.parseFloat(str));
                    break;
                default:
                    throw new IllegalArgumentException("can not match type");
            }
        }
    public Register newRegister(String str) {
        switch (getTypeFromNum(str)) {
            case INT:
                Register<Integer> r_int = new Register<Integer>(TYPE.INT);
                r_int.setValue(Integer.parseInt(str));
                return r_int;
            case FLOAT:
                Register<Float> r_float =  new Register<Float>(TYPE.FLOAT);
                r_float.setValue(Float.parseFloat(str));
                return r_float;
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

    private TYPE getTypeFromNum(String str) {
        if (str.matches(_int)) {
            return ClassFactory.TYPE.INT;
        } else if (str.matches(_float)) {
            return ClassFactory.TYPE.FLOAT;
        }
        throw new IllegalArgumentException("can not match type");
    }

    //可以做成不区分大小写
    public static TYPE getTypeFromType(String str){
        if("int".equals(str)){
            return TYPE.INT;
        }else if("float".equals(str)){
            return TYPE.FLOAT;
        }
        throw new IllegalArgumentException("can not match type");
    }
}