package ViewAndController;

import Parser.Parser;
import Parser.FourYuan;
import Parser.LexicalParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.List;

public class Controller {
    @FXML
    private Button open;
    @FXML
    private Button execute;
    @FXML
    private TextArea input;
    @FXML
    private TextArea output;

    private Main main;

    public Controller(){

    }

    public void setMain(Main main){
        this.main = main;
    }

    //todo 执行按钮
    public void onActionExecute(ActionEvent actionEvent){
        String text = input.getText();
        if(text==null||"".equals(text.trim())){

        }else{
            Parser parse = new Parser();///////分析实例
            LexicalParser lexicalParser = new LexicalParser();
            lexicalParser.setSourceCode(input.getText());
            //使用getAllTokens()方法获取Tokens,返回一个包含了识别出的Tokens的ArrayList
            List<String> tokens1 = lexicalParser.getAllTokens();
            for (String token: tokens1) {
                parse.tokens.add(token);
            }
            parse.token = parse.tokens.get(parse.cur++);////读入第一个单词
            parse.L();
            ////测试
            System.out.println("算术表达式栈顶的存放位置："+parse.Es.peek().des);
            System.out.println("识别出算术表达式的数量："+parse.symbols.size());
            System.out.println("算术表达式状态栈栈顶：（正确时应该为1）"+parse.states.peek());
            System.out.println("栈顶逻辑表达式需要回填的真出口链:");
            for(int i=0;i<parse.Bs.peek().truelist.size();i++) {
                System.out.print(parse.Bs.peek().truelist.get(i)+"   ");
            }
            System.out.println("");
            System.out.println("需要回填的假出口链：");
            for(int i=0;i<parse.Bs.peek().falselist.size();i++) {
                System.out.print(parse.Bs.peek().falselist.get(i)+"   ");
            }
            System.out.println("");
            System.out.println(parse.Bs.size());
            System.out.println("token:"+parse.token);
            for(int i=0;i<parse.fours.size();i++) {
                System.out.print(i+":   ");
                parse.fours.get(i).print();
            }
            System.out.println("下一条指令地址："+ FourYuan.no);
            //todo 将需要输出的内容输出到output中
            output.setText("输出内容");
        }
    }

    private static Frame frame;
    public void onActionOpenFile(ActionEvent actionEvent){
        FileDialog fileDialog = new FileDialog(frame,"test file",FileDialog.LOAD);
        fileDialog.setVisible(true);
        String str_file = fileDialog.getDirectory() + fileDialog.getFile();
        File file = new File(str_file);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append('\n');
            }
            String sourceCode = builder.toString();
            input.setText(sourceCode);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
