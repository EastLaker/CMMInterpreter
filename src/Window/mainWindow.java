/**
 * @author Ulysses Chen
 * @date 2019/10/25 16:13
 * @version 1.0
 */
package Window;


import Parser.E;
import Parser.FourYuan;
import Parser.Parser;
import Parser.LexicalParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class mainWindow {

    @FXML
    private TreeView<String> folderView;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private TabPane codeTabs;

    @FXML
    private TextFlow output;

    private CodeArea codeArea;
    private final Node rootIcon = new ImageView(
            new Image(getClass().getResourceAsStream("folder.png"))
    );

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        codeTabs.getTabs().add(codeTabBuilder("new file", ""));

    }

    private static Stage frame;
    public void onActionOpenFolder(ActionEvent actionEvent){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Source Folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        try {
            File file = directoryChooser.showDialog(frame);
            String path = file.getPath();//选择的文件夹路径
            TreeItem<String> treeRootItem = new TreeItem<>(path, rootIcon);
            folderView.setRoot(treeRootItem);
            treeRootItem.setExpanded(true);
            File[] file_list = file.listFiles();
            for (int i = 0; i < file_list.length; i++) {
                System.out.println(file_list[i].getName());
                TreeItem<String> item = new TreeItem<> (file_list[i].getName());
                treeRootItem.getChildren().add(item);
            }

        } finally {

        }
    }

    public void onActionOpenFile(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        try {
            File file = fileChooser.showOpenDialog(frame);
            String path = file.getPath();//选择的文件夹路径

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                while (reader.ready()) {
                    builder.append(reader.readLine());
                    builder.append('\n');
                }
                String sourceCode = builder.toString();
                codeArea.replaceText(0, 0, sourceCode);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

        } finally {

        }
    }

    public Tab codeTabBuilder(String title, String content){
        Tab tab = new Tab();
        tab.setText(title);
        tab.setClosable(true);
        codeArea = new CodeArea();
        codeArea.replaceText(0, 0, content);
        codeArea.getText();
        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        tab.setContent(new VirtualizedScrollPane<>(codeArea));

        return tab;
    }

    //todo 执行按钮
    public void onActionExecute(ActionEvent actionEvent){
        String text = codeArea.getText();
        if(text==null||"".equals(text.trim())){

        }else{
            FourYuan.no = 0;
            E.reg = 0;
            Parser parse = new Parser();///////分析实例
            LexicalParser lexicalParser = new LexicalParser();
            lexicalParser.setSourceCode(text);
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
            String output_text = "";
            for(int i=0;i<parse.fours.size();i++) {
                output_text += i + " " + parse.fours.get(i).get_four_str() + "\n";
            }
            System.out.println("下一条指令地址："+ FourYuan.no);
            //todo 将需要输出的内容输出到output中
            Text t = new Text();
            t.setText(output_text);
            output.getChildren().addAll(t);
        }
    }
}
