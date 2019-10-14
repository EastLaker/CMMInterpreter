/**
 * @author  Ulysses Chen
 * @date  2019/9/25
 * @version 1.0
 */
import lexical.*;

import java.awt.*;
import java.util.List;

public class Main {
    private static Frame frame;
    public static void main(String[] args) {
        FileDialog fileDialog = new FileDialog(frame,"test file",FileDialog.LOAD);
        fileDialog.setVisible(true);
        String str_file = fileDialog.getDirectory() + fileDialog.getFile();
        //建立一个LexicalParser对象，构造函数的参数为代码文件的地址
        LexicalParser lexicalParser = new LexicalParser(str_file);
        //使用getAllTokens()方法获取Tokens,返回一个包含了识别出的Tokens的ArrayList
        List<String> tokens = lexicalParser.getAllTokens();
        for (String token: tokens) {
            System.out.println(token);
        }
    }

}
