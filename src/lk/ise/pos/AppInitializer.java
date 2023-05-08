package lk.ise.pos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ise.pos.bo.BoFactory;
import lk.ise.pos.bo.custom.UserBO;
import lk.ise.pos.enums.BoType;

import java.io.IOException;

public class AppInitializer extends Application {
    private UserBO userBO = BoFactory.getInstance().getBo(BoType.USER);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try{
            userBO.initializeUsers();
        }catch (Exception e){
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("view/LoginForm.fxml"))));
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
