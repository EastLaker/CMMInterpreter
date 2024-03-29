
package lexical;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by UlyssesChen on 2019/6/1
 */


public class LexicalParser {
    private BufferedReader reader;
    private StringBuilder builder = new StringBuilder();
    private char current;
    private int currentLine;
    private Map<Integer, Integer> lines = new HashMap<Integer, Integer>();

    private static final Map<Character,Integer> directRecognized = new HashMap<Character,Integer>();

    private static final HashMap<String,Integer> reserveWords = new HashMap<>();

    private static Token.TokenType[] values = Token.TokenType.values();

    private String path;

    private String sourceCode;
    private List<Token> tokens;

    private int pointer = 0;

    public LexicalParser (String path) {
        this.path = path;
        this.getSourceCode();
        currentLine = 0;
        parse();
    }

    public LexicalParser() {}

    static {
        directRecognized.put('+',0);
        directRecognized.put('-',1);
        directRecognized.put('*',2);
        directRecognized.put(';',21);
        directRecognized.put(',',22);
        directRecognized.put('(',23);
        directRecognized.put(')',24);
        directRecognized.put('{',25);
        directRecognized.put('}',26);
        directRecognized.put('[',27);
        directRecognized.put(']',28);

        reserveWords.put("if",13);
        reserveWords.put("else",14);
        reserveWords.put("while",15);
        reserveWords.put("read",16);
        reserveWords.put("write",17);
        reserveWords.put("int",18);
        reserveWords.put("real",19);
        reserveWords.put("string",20);
    }

    public void getSourceCode() {
        File file = new File(path);
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            lines = new HashMap<Integer, Integer>();
            int counter = 1;
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append('\n');
                lines.put(counter, builder.length());
                counter += 1;
            }
            sourceCode = builder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String SourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode){
        this.sourceCode = sourceCode;
        try {
            reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sourceCode.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
            StringBuilder builder = new StringBuilder();
            lines = new HashMap<Integer, Integer>();
            int counter = 1;
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append('\n');
                lines.put(counter, builder.length());
                counter += 1;
            }
            //sourceCode = builder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        parse();
    }

    public List<Token> parse() {
        tokens = new ArrayList<Token>();
        Token currentToken;
        currentLine = 1;
        do {
            currentToken = this.getNextToken();
            tokens.add(currentToken) ;
        }while (currentToken.getType() != Token.TokenType.NULL);
        return tokens;
    }

    public List<Token> getAllTokens() {
        List<Token> tokenStrings = new ArrayList<Token>();
        for (Token token: tokens) {
            if(token.getType() != Token.TokenType.MULTIPLE_LINE_COMMENT && token.getType() != Token.TokenType.SINGLE_LINE_COMMENT){
                tokenStrings.add(token);
            }
        }
        return tokenStrings;
    }

    public Token getNextToken() {
        while (pointer >= lines.get(currentLine)){
            currentLine +=1;
        }

        Token token = new Token();
        token.setLine_no(currentLine);
        token.setType(Token.TokenType.NULL);

        if(current == '\u0003') {
            return token;
        }
        readCharSkip();
        if(directRecognized.containsKey(current)) {//先判断出可以直接识别的token
            token.setType(values[directRecognized.get(current)]);
            //token.printValue();
            return token;
        }
        else if(current == '/') {//判断是除号还是多行注释还是单行注释
            readCharSkip();
            if(current == '*') {    //多行注释
                while (true) {
                    readCharSkip();
                    if(current =='*') {
                        readCharSkip();
                        if(current == '/') {
                            token.setType(Token.TokenType.MULTIPLE_LINE_COMMENT);
                            break;
                        }
                    }
                }
            }
            else if(current == '/') {   //单行注释
                readLineEnd();
                token.setType(Token.TokenType.SINGLE_LINE_COMMENT);
            }
            else {      //除号
                token.setType(Token.TokenType.DIVIDE);
            }
        }
        else if(current == '=') {   //判断赋值还是相等
            readChar();
            if(current == '=') {
                token.setType(Token.TokenType.EQUAL);
            }
            else {
                token.setType(Token.TokenType.ASSIGN);
                pointer--;
            }
        }
        else if(current == '<') {//判断小于还是小于等于
            readChar();
            if(current =='=') {
                token.setType(Token.TokenType.LESS_EQUAL);
            }
            else {
                token.setType(Token.TokenType.LESS);
                pointer--;
            }
        }
        else if(current == '>') {//判断小于还是不等于
            readChar();
            if(current =='=') {
                token.setType(Token.TokenType.MORE_EQUAL);
            }
            else {
                token.setType(Token.TokenType.MORE);
                pointer--;
            }
        }
        else if(current == '!') {//判断是否是!=
            readChar();
            if(current =='=') {
                token.setType(Token.TokenType.NOT_EQUAL);
            }
            else {
                pointer--;
                current = sourceCode.charAt(pointer);
            }
        }
        else if(current == '|') {//判断是否是 ||
            readChar();
            if(current =='|') {
                token.setType(Token.TokenType.OR);
            }
            else {
                pointer--;
                current = sourceCode.charAt(pointer);
            }
        }
        else if(current == '&') {//判断是否是 &&
            readChar();
            if(current =='&') {
                token.setType(Token.TokenType.AND);
            }
            else {
                pointer--;
                current = sourceCode.charAt(pointer);
            }
        }
        else {
            if(current>='0'&&current<='9') {//说明接下来是一个数字字面量,判断它是整数还是实数
                boolean isReal = false;
                while (true) {
                    if((current>='0'&&current<='9')||current=='.') {
                        builder.append(current);
                        if (current == '.') {
                            isReal = true;
                        }
                        readChar();
                    }
                    else {
                        pointer--;
                        break;
                    }
                }
                String value = builder.toString();
                builder.delete(0,builder.length());
                if(isReal) {
                    token.setType(Token.TokenType.REAL_LITERAL);
                    token.setRealValue(Double.parseDouble(value));
                }
                else {
                    token.setType(Token.TokenType.INT_LITERAL);
                    token.setIntValue(Integer.parseInt(value));
                }
            }
            else if((current>='A'&&current<='Z')||(current>='a'&&current<='z')){//说明接下来是一个标识符或者关键字
                while (true) {  //将单词存入builder
                    if((current>='A'&&current<='Z')||(current>='a'&&current<='z')||(current>='0'&&current<='9')||current == '_') {
                        builder.append(current);
                        readChar();
                    }
                    else {
                        pointer--;
                        break;
                    }
                }
                String value = builder.toString();
                builder.delete(0,builder.length());
                if(reserveWords.containsKey(value)) {   //关键字
                    token.setType(values[reserveWords.get(value)]);
                }
                else {  //标识符
                    token.setType(Token.TokenType.IDENTIFIER);
                    token.setStringValue(value);
                }
            }
            else if(current=='"'){//说明接下来是一个字符串变量
                readChar();
                while (true) {  //将单词存入builder
                    if(current !='"') {
                        builder.append(current);
                        readChar();
                    }
                    else {
                        //readChar();
                        break;
                    }
                }
                String value = builder.toString();
                builder.delete(0,builder.length());
                token.setType(Token.TokenType.STRING_LITERAL);
                token.setStringValue(value);
            }
        }

        //token.printValue();
        return token;
    }

    private void readCharSkip () {
        do {
            if (pointer < sourceCode.length()) {
                current = sourceCode.charAt(pointer);
                pointer++;
            } else {
                current = '\u0003';
                break;
            }
        }while (current == '\n'||current == '\r'||current == '\t'||current ==' ');
    }

    private void readLineEnd() {
        while (sourceCode.charAt(pointer)!='\n') {
            pointer++;
        }
    }

    private void readChar() {
        if (pointer < sourceCode.length()) {
            current = sourceCode.charAt(pointer);
            pointer++;
        } else {
            current = '\u0003';
        }
    }

}
