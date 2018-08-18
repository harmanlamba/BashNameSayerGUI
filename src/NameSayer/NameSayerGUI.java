package NameSayer;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.util.ArrayList;

public class NameSayerGUI extends Application {
    private boolean gridLines=false;
    private Creations _creationBrain= new Creations();
    private PopUps _popUp= new PopUps();
    private static ListView<String> _listView;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("NameSayer-Harman Lamba");

        //Creating the Layout
        GridPane mainGrid= new GridPane();
        mainGrid.setPadding(new Insets(10,10,10,10));
        mainGrid.setVgap(8);
        mainGrid.setHgap(10);

        //Components
        Label creationLabel= new Label("  Creations:");
        Button button= new Button("Display Grid Line");
        Button createCreationButton = new Button("Create Creation");
        Button playCreationButton= new Button("Play Creation");
        Button quitButton= new Button ("Quit NameSayer");


        //GridPane Constrains
        GridPane.setConstraints(creationLabel,0,1);
        GridPane.setConstraints(button,20,20);
        GridPane.setConstraints(quitButton,21,20);



        //Setting up the ListView for the GUI
        _listView= new ListView<>();
        _listView.autosize();
        GridPane.setConstraints(_listView,0,2);
        GridPane.setVgrow(_listView,Priority.ALWAYS);
        ArrayList<String> creationFiles=_creationBrain.listCreations();
        for (String counter: creationFiles){
            _listView.getItems().add(counter);
        }


        //HBox below ListView
        HBox hboxBelowListCreations= new HBox();
        hboxBelowListCreations.setSpacing(10);
        hboxBelowListCreations.getChildren().addAll(createCreationButton,playCreationButton);
        GridPane.setConstraints(hboxBelowListCreations,0,3);


        //Adding children to the layout
        mainGrid.getChildren().addAll(button,_listView,hboxBelowListCreations,creationLabel,quitButton);

        //Creating the scene and assigning it to the stage
        Scene mainScene= new Scene(mainGrid,800,500);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        //Event Handling
        playCreationButton.setOnAction(e->_creationBrain.playSelectedCreation());
        createCreationButton.setOnAction(e -> _popUp.creationBox("Create Creation","Type in the name of the creation without any extensions:","Ok","Cancel"));
        button.setOnAction(e -> {
            if(!gridLines){
                mainGrid.setGridLinesVisible(true);
                gridLines=true;
            }else{
                mainGrid.setGridLinesVisible(false);
                gridLines=false;
            }
        });
        quitButton.setOnAction(e -> primaryStage.close());
    }

    public ObservableList<String> getSelectedCreation() {
        return _listView.getSelectionModel().getSelectedItems();
    }

}

