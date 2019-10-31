package Parser;


/**
 * @author knight
 * @date 2019/10/30 20:23
 * created
 */
public class ArrayType<T> extends Word<T> {
    private T[] value;

    public ArrayType(T[] array, ClassFactory.TYPE type){
        value = array;
        this.length = array.length;
        this.type = type;
    }


    public T getValue(int index){
        checkIndexOutOfBound(index);
        return value[index];
    }

    public void setValue(int index, T v){
        checkIndexOutOfBound(index);
        value[index] = v;
    }

    private void checkIndexOutOfBound(int index) {
        if (index < 0 && index > length - 1)
            throw new IndexOutOfBoundsException();/////todo   报错处理
    }
}
