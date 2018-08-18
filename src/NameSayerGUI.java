import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class NameSayerGUI extends Application {
    private boolean gridLines=false;
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
        GridPane.setConstraints(_listView,0,2);
        ArrayList<String> creationFiles=_creationBrain.listCreations();
        for (String counter: creationFiles){
            _listView.getItems().add(counter);
        }


        //Creation Label
        Label creationLabel= new Label("  Creations");
        GridPane.setConstraints(creationLabel,0,1);


        //Secondary GridPane below ListView
        HBox hboxBelowListCreations= new HBox();
        GridPane.setConstraints(hboxBelowListCreations,0,3);
        Button createCreationButton = new Button("Create Creation");
        Button playCreation= new Button("Play Creation");
        hboxBelowListCreations.setSpacing(10);
        hboxBelowListCreations.getChildren().addAll(createCreationButton,playCreation);



        //Components for the layout
        Button button= new Button("Display Grid Line");
        GridPane.setConstraints(button,20,20);
        button.setOnAction(e -> {
            if(!gridLines){
                mainGrid.setGridLinesVisible(true);
                gridLines=true;
            }else{
                mainGrid.setGridLinesVisible(false);
                gridLines=false;
            }
        });


        //Adding children to the layout
        mainGrid.getChildren().addAll(button,_listView,hboxBelowListCreations,creationLabel);

        //Creating the scene and assigning it to the stage
        Scene mainScene= new Scene(mainGrid,500,500);
        primaryStage.setScene(mainScene);
        primaryStage.show();




    }
}

