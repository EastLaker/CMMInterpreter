package ViewAndController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
            //todo 将需要输出的内容输出到output中
            output.setText("输出内容");
        }
    }

    public void onActionOpenFile(ActionEvent actionEvent){
        //todo 这是打开文件的按钮，可以不需要
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT file (*.txt)","*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());
        if(file!=null){
            //todo print the
            try{
                final int FILESIZE = 1024;
                char[] buf = new char[FILESIZE];
                FileReader fr = new FileReader(file);
                int len = fr.read(buf);
                output.setText(new String(buf));
            }catch (IOException e){e.printStackTrace();}
        }
    }
}
