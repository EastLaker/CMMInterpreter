package lexical;

public class Token {

    public enum TokenType {

        //算术运算符
        PLUS,//0
        MINUS,//1
        MULTIPLY,//2
        DIVIDE,//3
        ASSIGN,//4

        //关系运算符
        LESS,//5
        MORE,//6
        EQUAL,//7
        NOT_EQUAL,//8

        //保留字
        IF,//9
        ELSE,//10
        WHILE,//11
        READ,//12
        WRITE,//13
        INT,//14
        REAL,//15

        //分隔符
        SEMICOLON,//16
        DOT,//17
        L_BRACKET,//18
        R_BRACKET,//19
        L_ANGLE_BRACKET,//20
        R_ANGLE_BRACKET,//21
        L_SQUARE_BRACKET,//22
        R_SQUARE_BRACKET,//23

        //字面量
        INT_LITERAL,//24
        REAL_LITERAL,//25
        IDENTIFIER,//26

        //注释
        SINGLE_LINE_COMMENT,//27
        MULTIPLE_LINE_COMMENT,//28

        NULL//29    空的token，说明已经到文件结尾
    }

    private TokenType type;//token类型
    private String stringValue;//字符串值
    private int intValue;//整型值
    private double realValue;//实数值

    public TokenType getType () {
        return type;
    }

    public void setType (TokenType type) {
        this.type = type;
    }

    public String getStringValue () {
        return stringValue;
    }

    public void setStringValue (String stringValue) {
        this.stringValue = stringValue;
    }

    public int getIntValue () {
        return intValue;
    }

    public void setIntValue (int intValue) {
        this.intValue = intValue;
    }

    public double getRealValue () {
        return realValue;
    }

    public void setRealValue (double realValue) {
        this.realValue = realValue;
    }

    public void printValue() {
        switch (type) {
            case INT_LITERAL:
                System.out.println(type+" "+getIntValue());break;
            case REAL_LITERAL:
                System.out.println(type+" "+getRealValue());break;
            case IDENTIFIER:
                System.out.println(type+" "+getStringValue());break;
            default:
                System.out.println(type);break;
        }
    }
}
