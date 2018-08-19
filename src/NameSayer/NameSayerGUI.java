package NameSayer;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class NameSayerGUI extends Application {
    private boolean gridLines=false;
    private Creations _creationBrain= new Creations();
    private ObservableList<String> _creationFiles=_creationBrain.listCreations();
    private static ListView<String> _listView;
    private PopUps _popUp= new PopUps();

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
        Button deleteCreationButton= new Button("Delete Creation");
        GridPane.setHalignment(deleteCreationButton, HPos.CENTER);


        //GridPane Constrains
        GridPane.setConstraints(creationLabel,0,1);
        GridPane.setConstraints(button,20,20);
        GridPane.setConstraints(quitButton,21,20);
        GridPane.setConstraints(deleteCreationButton,20,3);

        //HBox below ListView
        HBox hboxBelowListCreations= new HBox();
        hboxBelowListCreations.setSpacing(10);
        hboxBelowListCreations.getChildren().addAll(createCreationButton,playCreationButton);
        ColumnConstraints hBoxColConstrains= new ColumnConstraints(240,240,240);
        mainGrid.getColumnConstraints().add(0,hBoxColConstrains);
        GridPane.setConstraints(hboxBelowListCreations,0,3);
        GridPane.setHgrow(hboxBelowListCreations,Priority.ALWAYS);

        //Adding MediaPlayer to the GUI
        MediaView mediaView= new MediaView();
        mediaView.setFitHeight(300);
        mediaView.setFitWidth(400);
        mediaView.setX(100);
        GridPane.setConstraints(mediaView,20,2);

        //Setting up the ListView for the GUI
        _listView= new ListView<>(_creationFiles);
        _listView.autosize();
        _listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectionPath= _creationBrain.getSelectedCreationPath(false);
                String selection= _creationBrain.getSelectedCreation();
                if(selection != null){
                    Media media= new Media(new File(selectionPath).toURI().toString());
                    MediaPlayer mediaPlayer= new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    System.out.println(selectionPath);
                }
            }
        });
        GridPane.setConstraints(_listView,0,2);
        GridPane.setVgrow(_listView,Priority.ALWAYS);


        //Adding children to the layout
        mainGrid.getChildren().addAll(button,_listView,hboxBelowListCreations,creationLabel,quitButton,mediaView,deleteCreationButton);

        //Creating the scene and assigning it to the stage
        Scene mainScene= new Scene(mainGrid,880,500);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        //Event Handling
        playCreationButton.setOnAction(e->{
            _creationBrain.playCreation(mediaView);
        });
        createCreationButton.setOnAction(e -> {
            _creationBrain.createCreation(_listView);
        });
        button.setOnAction(e -> {
            if(!gridLines){
                mainGrid.setGridLinesVisible(true);
                gridLines=true;
            }else{
                mainGrid.setGridLinesVisible(false);
                gridLines=false;
            }
        });
        //quitButton.setOnAction(e -> _popUp.overrideCreationBox("title","asdfasdfasdfasdf","baboons",580,100));
        deleteCreationButton.setOnAction(e -> {
            _creationBrain.deleteCreation(_listView);
        });
    }

    public ObservableList<String> getSelectedCreation() {
        return _listView.getSelectionModel().getSelectedItems();
    }


}

