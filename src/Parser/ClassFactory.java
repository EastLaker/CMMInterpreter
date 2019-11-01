package Parser;

import java.util.Arrays;
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
    private String _float = "(([+])?[0-9]\\d*\\.?\\d*)|((-)?[0-9]\\d*\\.?\\d*)";

    public enum TYPE{
        INT,  //识别的声明类型 int
        FLOAT, //识别float
        INT_ARRAY,
        FLOAT_ARRAY,
    }

    public Word newWordFromType(String str) {
        switch (getTypeFromType(str)){
            case INT:
                return new Word<>(TYPE.INT, null);
            case FLOAT:
                return new Word<>(TYPE.FLOAT, null);
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

    public ArrayType newArrayFromArray(Object[] arr, String type) throws DynamicException.defaultException {
        switch (getTypeFromType(type)){
            case INT:
                return new ArrayType(Arrays.copyOf(arr,arr.length,Integer[].class),TYPE.INT_ARRAY);
            case FLOAT:
                return new ArrayType(Arrays.copyOf(arr,arr.length,Float[].class),TYPE.FLOAT_ARRAY);
            default:
                //todo 添加错误处理
                return null;
        }
    }


    public ArrayType newArrayFromType(String str, int length) throws DynamicException.defaultException {
        switch (getTypeFromType(str)){
            case INT:
                return new ArrayType<>(new Integer[length],TYPE.INT_ARRAY);
            case FLOAT:
                return new ArrayType<>(new Float[length],TYPE.FLOAT_ARRAY);
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

    public void setWordValue(Word word, Object value, TYPE type) throws DynamicException.defaultException
            , DynamicException.numberFormatException {
        //todo 当word中为int时， str为float时可能有异常发生.
        switch (type) {
            case INT:
                word.setValue(value);
                break;
            case FLOAT:
                if (word.type == TYPE.INT) {
                    throw new DynamicException().new numberFormatException();
                }
                word.setValue(value);
                break;
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

    public void setWordValue(Word word,String str) throws DynamicException.defaultException
            , DynamicException.numberFormatException {
        //todo 当word中为int时， str为float时可能有异常发生.
        switch (getTypeFromNum(str)) {
            case INT:
                word.setValue(Integer.parseInt(str));
                break;
            case FLOAT:
                if (word.type == TYPE.INT) {
                    throw new DynamicException().new numberFormatException();
                }
                word.setValue(Float.parseFloat(str));
                break;
            default:
                throw new IllegalArgumentException("can not match type");
        }
    }

    public void setArrayElementValue(ArrayType array, int index, String str)
            throws DynamicException.outOfArrayBoundException, DynamicException.numberFormatException {
        switch (getTypeFromNum(str)){
            case INT:
                array.setValue(index, Integer.parseInt(str));
                break;
            case FLOAT:
                if(array.type==TYPE.INT_ARRAY){
                    throw new DynamicException().new numberFormatException();
                }
                array.setValue(index,Float.parseFloat(str));
                break;
        }
    }

    public void setArrayElementValue(ArrayType array, int index, Object value, TYPE type)
            throws DynamicException.outOfArrayBoundException, DynamicException.numberFormatException {
        switch (type){
            case INT:
                array.setValue(index, value);
                break;
            case FLOAT:
                if(array.type==TYPE.INT_ARRAY){
                    throw new DynamicException().new numberFormatException();
                }
                array.setValue(index,value);
                break;
        }
    }

    public Register newRegister(String str) throws DynamicException.defaultException {
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
                throw new DynamicException().new defaultException("无法解析的数据类型");
        }
    }

    public TYPE getTypeFromNum(String str) throws DynamicException.defaultException {
        if (str.matches(_int)) {
            return ClassFactory.TYPE.INT;
        } else if (str.matches(_float)) {
            return ClassFactory.TYPE.FLOAT;
        }
        throw new DynamicException().new defaultException("无法解析的数据类型");

    }

    //可以做成不区分大小写
    public static TYPE getTypeFromType(String str) throws DynamicException.defaultException {
        if("int".equals(str)){
            return TYPE.INT;
        }else if("float".equals(str)){
            return TYPE.FLOAT;
        }
        throw new DynamicException().new defaultException("无法解析的数据类型");

    }
}
