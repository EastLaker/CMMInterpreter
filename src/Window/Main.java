/**
 * @author Ulysses Chen
 * @date 2019/10/25 14:42
 * @version 1.0
 */
package Window;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage mainStage;
    private AnchorPane root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));

        primaryStage.setTitle("CMMInterpreter");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

