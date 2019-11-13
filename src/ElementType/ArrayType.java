package ElementType;


import Parser.ClassFactory;
import Utils.DynamicException;

/**
 * @author knight
 * @date 2019/10/30 20:23
 * created
 */
public class ArrayType<T> extends Word<T> {
    private T[] value;
    private int length;

    public ArrayType(T[] array, ClassFactory.TYPE type){
        value = array;
        this.length = array.length;
        this.type = type;
    }


    public T getValue(int index) throws DynamicException.outOfArrayBoundException {
        if (index < 0 || index > length - 1)
            throw new DynamicException().new outOfArrayBoundException();
        return value[index];
    }

    public void setValue(int index, T v) throws DynamicException.outOfArrayBoundException {
        if (index < 0 || index > length - 1)
            throw new DynamicException().new outOfArrayBoundException();
        value[index] = v;
    }

}
