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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

public class NameSayerGUI extends Application {
    private Creations _creationBrain = new Creations();
    private ObservableList<String> _creationFiles = _creationBrain.listCreations();
    private static ListView<String> _listView;

    public static void main(String[] args) {
        //Launching the GUI it self
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("NameSayer-Harman Lamba");

        //Creating the Layout
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(10, 10, 10, 10));
        mainGrid.setVgap(8);
        mainGrid.setHgap(10);


        //Creating the base components for the GUI
        Label creationLabel = new Label("  Creations:");
        Button createCreationButton = new Button("Create Creation");
        Button playCreationButton = new Button("Play Creation");
        Button quitButton = new Button("Quit NameSayer");
        Button deleteCreationButton = new Button("Delete Creation");
        GridPane.setHalignment(deleteCreationButton, HPos.CENTER);
        GridPane.setHalignment(quitButton, HPos.RIGHT);
        Rectangle rect = new Rectangle(400, 300);
        rect.setStroke(Color.BLACK);


        //GridPane Constrains
        GridPane.setConstraints(creationLabel, 0, 1);
        GridPane.setConstraints(quitButton, 20, 10);
        GridPane.setConstraints(deleteCreationButton, 20, 3);


        //Setting up the HBox below ListView
        HBox hboxBelowListCreations = new HBox();
        hboxBelowListCreations.setSpacing(10);
        hboxBelowListCreations.getChildren().addAll(createCreationButton, playCreationButton);
        ColumnConstraints hBoxColConstrains = new ColumnConstraints(240, 240, 240);
        mainGrid.getColumnConstraints().add(0, hBoxColConstrains);
        GridPane.setConstraints(hboxBelowListCreations, 0, 3);
        GridPane.setHgrow(hboxBelowListCreations, Priority.ALWAYS);

        //Adding MediaPlayer to the GUI
        MediaView mediaView = new MediaView();
        mediaView.setFitHeight(300);
        mediaView.setFitWidth(400);
        mediaView.setX(100);
        mediaView.setPreserveRatio(false);
        GridPane.setConstraints(mediaView, 20, 2);
        GridPane.setConstraints(rect, 20, 2);

        //Setting up the ListView for the GUI
        _listView = new ListView<>(_creationFiles);
        _listView.autosize();
        _listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectionPath = _creationBrain.getSelectedCreationPath(false);
                String selection = _creationBrain.getSelectedCreation();
                ObservableList<String> listOfCreations = _listView.getItems();
                if (selection != null) {
                    mainGrid.getChildren().removeAll(rect);
                    Media media = new Media(new File(selectionPath).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                } else if (listOfCreations.size() == 0) {
                    mainGrid.getChildren().removeAll(rect);
                    mainGrid.getChildren().addAll(rect);
                }

            }
        });
        GridPane.setConstraints(_listView, 0, 2);
        GridPane.setVgrow(_listView, Priority.ALWAYS);


        //Adding children to the layout
        mainGrid.getChildren().addAll(_listView, hboxBelowListCreations, creationLabel, quitButton, mediaView, deleteCreationButton, rect);

        //Creating the scene and assigning it to the stage
        Scene mainScene = new Scene(mainGrid, 870, 470);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();


        //Event Handling
        playCreationButton.setOnAction(e -> {
            _creationBrain.playCreation(mediaView);
        });
        createCreationButton.setOnAction(e -> {
            _creationBrain.checkTempCreationsDir();
            _creationBrain.createCreation(_listView);
        });
        quitButton.setOnAction(e -> primaryStage.close());
        deleteCreationButton.setOnAction(e -> {
            _creationBrain.deleteCreation(_listView, mainGrid,rect);
        });
    }

    //A getter to know the selected item. The return type is an ObservableList
    public ObservableList<String> getSelectedCreation() {
        return _listView.getSelectionModel().getSelectedItems();
    }


}

