package Utils;

/**
 * @author knight
 * @date 2019/11/9 15:30
 * created
 */
public class Regex {

    //for int|float
    public static final String constant = "(([+])?[0-9]\\d*\\.?\\d*)|((-)?[0-9]\\d*\\.?\\d*)";

    //for register field
    public static final String regPat = "reg";

    //for word recognition
    public static final String variPat = "^[A-Za-z_][A-Za-z0-9_]*$";

    //for array index
    public static final String positiveInt = "^(0|[1-9][0-9]*)$";

    //for integer.
    public static final String _int = "^[+/-]?[0-9]*$";

    //for float  实数
    public static final String _float = "(([+])?[0-9]\\d*\\.?\\d*)|((-)?[0-9]\\d*\\.?\\d*)";

    //for 字符串
    public static final String _string = "^\\^.*";

}
