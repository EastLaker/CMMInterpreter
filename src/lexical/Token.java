package lexical;

import java.lang.String;
import java.util.HashMap;

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
        LESS_EQUAL,//8
        MORE_EQUAL,//9
        NOT_EQUAL,//10
        AND, //11
        OR, //12

        //保留字
        IF,//13
        ELSE,//14
        WHILE,//15
        READ,//16
        WRITE,//17
        INT,//18
        REAL,//19

        //分隔符
        SEMICOLON,//20
        DOT,//21
        L_BRACKET,//22
        R_BRACKET,//23
        L_ANGLE_BRACKET,//24
        R_ANGLE_BRACKET,//25
        L_SQUARE_BRACKET,//26
        R_SQUARE_BRACKET,//27

        //字面量
        INT_LITERAL,//28
        REAL_LITERAL,//29
        IDENTIFIER,//30

        //注释
        SINGLE_LINE_COMMENT,//31
        MULTIPLE_LINE_COMMENT,//32

        NULL//33    空的token，说明已经到文件结尾
    }
    private HashMap<TokenType, String> dict = new HashMap<TokenType, String>(){{
        put(TokenType.PLUS, "+");
        put(TokenType.MINUS, "-");
        put(TokenType.MULTIPLY, "*");
        put(TokenType.DIVIDE, "/");
        put(TokenType.ASSIGN, "=");
        put(TokenType.LESS, "<");
        put(TokenType.MORE, ">");
        put(TokenType.EQUAL, "==");
        put(TokenType.LESS_EQUAL, "<=");
        put(TokenType.MORE_EQUAL, ">=");
        put(TokenType.NOT_EQUAL, "!=");
        put(TokenType.AND, "&&");
        put(TokenType.OR, "||");
        put(TokenType.IF, "if");
        put(TokenType.ELSE, "else");
        put(TokenType.WHILE, "while");
        put(TokenType.READ, "read");
        put(TokenType.WRITE, "write");
        put(TokenType.INT, "int");
        put(TokenType.REAL,  "real");
        put(TokenType.SEMICOLON, ";");
        put(TokenType.DOT, ",");
        put(TokenType.L_BRACKET, "(");
        put(TokenType.R_BRACKET, ")");
        put(TokenType.L_ANGLE_BRACKET, "{");
        put(TokenType.R_ANGLE_BRACKET, "}");
        put(TokenType.L_SQUARE_BRACKET, "[");
        put(TokenType.R_SQUARE_BRACKET, "]");
        put(TokenType.SINGLE_LINE_COMMENT, "");
        put(TokenType.MULTIPLE_LINE_COMMENT, "");
        put(TokenType.NULL, "");
    }};

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

    public String getString() {
        switch (type) {
            case INT_LITERAL:
                return String.valueOf(getIntValue());
            case REAL_LITERAL:
                return String.valueOf(getRealValue());
            case IDENTIFIER:
                return getStringValue();
            default:
                return dict.get(type);
        }
    }
}
