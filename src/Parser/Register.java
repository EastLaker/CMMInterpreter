package Parser;

/**
 * @author knight
 */
public class Register<T> {
    public static int reg;
    private T value;
    private ClassFactory.TYPE type;

    public Register(){}

    public Register(ClassFactory.TYPE type){
        this.type = type;
    }

    public Register(ClassFactory.TYPE type, T value){
        this.type = type;
        this.value = value;
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


    public ClassFactory.TYPE getType() {
        return type;
    }

    public void setType(ClassFactory.TYPE type) {
        this.type = type;
    }

    public static String getReg() {
        return "reg" + reg++;
    }
}