/**
 * @author Ulysses Chen
 * @date 2019/10/25 16:13
 * @version 1.0
 */
package Window;


import Parser.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.EventHandler;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lexical.LexicalParser;
import lexical.Token;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.lang.*;

import Parser.ClassFactory;
import Parser.Word;
import Parser.ArrayType;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.AWTException;
import java.awt.Robot;

public class mainWindow {

    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "write", "read"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

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

    private static CodeArea codeArea;

    private String opened_folder;
    public static int j=0;
    private final Node rootIcon = new ImageView(
        new Image(getClass().getResourceAsStream("folder.png"))
    );

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        codeTabs.getTabs().add(codeTabBuilder("编辑区", ""));
    }

    private static Stage frame;
    public void onActionOpenFolder(ActionEvent actionEvent){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Source Folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        try {
            File file = directoryChooser.showDialog(frame);
            opened_folder = file.getPath().substring(0,file.getPath().length()-file.getName().length());//选择的文件夹路径
            FileTreeItem fileTreeItem = new FileTreeItem(file, f -> {
                File[] allFiles = f.listFiles();
                File[] directorFiles = f.listFiles(File::isDirectory);
                List<File> list = new ArrayList<>(Arrays.asList(allFiles));
                //list.removeAll(Arrays.asList(directorFiles));
                return list.toArray(new File[list.size()]);
            });
//            folderView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//                @Override
//                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                    TreeItem<String> currentSelectItem = (TreeItem<String>) newValue;
//                    if (currentSelectItem != null&& currentSelectItem.getValue().matches("(.*.txt)|(.*.cmm)")) {
//                        System.out.println("selection(" + ((TreeItem<String>) newValue).getValue() + ") change");
//                    }
//                }
//            });
            folderView.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent mouseEvent)
                {
                    if(mouseEvent.getClickCount() == 2)
                    {
                        TreeItem<String> item = folderView.getSelectionModel().getSelectedItem();
                        if (item.getValue().matches("(.*.txt)|(.*.cmm)|(.*.c)")) {
                            try {
                                String path1 = getTreeItemPath(item);
                                File file1 = new File(path1);
                                BufferedReader reader = new BufferedReader(new FileReader(file1));
                                StringBuilder builder = new StringBuilder();
                                while (reader.ready()) {
                                    builder.append(reader.readLine());
                                    builder.append('\n');
                                }
                                String sourceCode = builder.toString();
                                codeArea.clear();
                                codeArea.replaceText(0, 0, sourceCode);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                }
            });
            folderView.setRoot(fileTreeItem);
            fileTreeItem.setExpanded(true);
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
        codeArea.setStyle("-fx-font-family:'Consolas'");

        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = codeArea

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        // when no longer need syntax highlighting and wish to clean up memory leaks
        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
            }
        });

        tab.setContent(new VirtualizedScrollPane<>(codeArea));
        String css = getClass().getResource("light.css").toExternalForm();
        codeArea.getStylesheets().add(css);

        return tab;
    }

    //todo 执行按钮
    public void onActionExecute(ActionEvent actionEvent){
        String text = codeArea.getText();
        if(text==null||"".equals(text.trim())){

        }else{
            output.getChildren().clear();
            mainWindow.j=0;
            FourYuan.no = 0;
            Parser.errors.clear();
            E.reg = 0;
            ClassFactory.Wordlist.clear();
            Word.setDes_start(0x0);
            Parser parse = new Parser();///////分析实例
            LexicalParser lexicalParser = new LexicalParser();
            lexicalParser.setSourceCode(text);
            //使用getAllTokens()方法获取Tokens,返回一个包含了识别出的Tokens的ArrayList
            List<Token> tokens1 = lexicalParser.getAllTokens();
            for (Token token: tokens1) {
                parse.tokens.add(token);
            }
            parse.token = parse.tokens.get(parse.cur++);////读入第一个单词
            parse.L();
            ////测试
            StringBuilder output_text = new StringBuilder();
            for(int i=0;i<parse.fours.size();i++) {
                output_text.append(i + " " + parse.fours.get(i).get_four_str() + "\n");
            }
            try{
                for(;j<parse.fours.size();j++)
                    parse.fours.get(j).Exec();
            } catch (DynamicException.stopMachineException e) {
                //todo  已退出for循环  还需要添加的工作？
            }

            Set<String> words = ClassFactory.Wordlist.keySet();
            output_text.append("单词表结构：\n");
            output_text.append("变量名\t变量类型\t变量地址\t变量值\n");
            for(String word : words) {
                if(ClassFactory.Wordlist.get(word) instanceof ArrayType) {
                    for (int i = 0; i < ClassFactory.Wordlist.get(word).length; i++) {
                        try{
                            output_text.append(word + "[" + i + "]\t" + ClassFactory.Wordlist.get(word).type + "\t" + (ClassFactory.Wordlist.get(word).getDes() + i * 4) + "\t"
                                    + ((ArrayType) ClassFactory.Wordlist.get(word)).getValue(i) + "\n");
                        }catch (Exception e){}
                    }
                }
                else
                    output_text.append(word+"\t"+ ClassFactory.Wordlist.get(word).type+"\t"+ ClassFactory.Wordlist.get(word).getDes()+"\t"+ ClassFactory.Wordlist.get(word).getValue()+"\n");

            }

            for(int i=0;i<Parser.errors.size();i++)
                output_text.append(Parser.errors.get(i));
            System.out.println("下一条指令地址："+ FourYuan.no);
            //todo 将需要输出的内容输出到output中
            Text t = new Text();
            t.setText(output_text.toString());
            output.getChildren().addAll(t);
        }
    }

    public String getTreeItemPath(TreeItem<String> treeItem){
        if(treeItem.getParent()!=null) {
            return getTreeItemPath(treeItem.getParent()) + "/" +treeItem.getValue();
        }
        else {
            StringBuilder builder = new StringBuilder();
            builder.append(opened_folder);
            builder.append(treeItem.getValue());
            return builder.toString();
        }
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
	
    public void onUndo(){
    	Robot robot;
		try {
			robot = new Robot();
			keyPressWithCtrl(robot, java.awt.event.KeyEvent.VK_Z); 
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //创建一个robot对象
    }
    
    public void onRedo() {
    	Robot robot;
    	try {
    		robot = new Robot();
    		keyPressWithCtrl(robot, java.awt.event.KeyEvent.VK_Y);
    	} catch (AWTException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} //创建一个robot对象
    }
    
    public static void keyPressWithCtrl(Robot r, int key) {
    	codeArea.requestFocus();
    	r.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
    	r.keyPress(key);   
        r.keyRelease(key);  
        r.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
    }
}
