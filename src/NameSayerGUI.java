import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class NameSayerGUI extends Application {
    private Creations _creationBrain= new Creations();
    private ListView<String> _listView;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("NameSayerGUI-Harman Lamba");

        //Creating the Layout
        GridPane mainGrid= new GridPane();
        //mainGrid.setGridLinesVisible(true);
        mainGrid.setPadding(new Insets(10,10,10,10));
        mainGrid.setVgap(8);
        mainGrid.setHgap(10);

        //Setting up the ListView for the GUI
        _listView= new ListView<>();
        _listView.autosize();
        ArrayList<String> creationFiles=_creationBrain.listCreations();
        for (String counter: creationFiles){
            _listView.getItems().add(counter);
        }

        Label creationLabel= new Label("  Creations");
        GridPane.setConstraints(_listView,0,2);
        GridPane.setConstraints(creationLabel,0,1);




        //Components for the layout
        Button button= new Button("Play");
        GridPane.setConstraints(button,20,20);


        //Adding children to the layout
        mainGrid.getChildren().addAll(button,_listView,creationLabel);

        //Creating the scene and assigning it to the stage
        Scene mainScene= new Scene(mainGrid,500,500);
        primaryStage.setScene(mainScene);
        primaryStage.show();




    }
}

