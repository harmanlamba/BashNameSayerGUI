package NameSayer;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PopUps {

    public String creationBox(String title, String message, String button1, String button2) {
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label messageToDisplay = new Label(message);
        TextField creationNameField= new TextField();
        Button okButton = new Button(button1);
        Button cancelButton = new Button(button2);
        grid.maxHeight(700);
        grid.maxWidth(300);
        GridPane.setConstraints(messageToDisplay, 5, 5);
        GridPane.setConstraints(creationNameField,5,6);
        GridPane.setConstraints(okButton, 6, 6);
        GridPane.setConstraints(cancelButton, 7, 6);
        grid.getChildren().setAll(messageToDisplay, okButton, cancelButton,creationNameField);

        //Setting up event handlers
        cancelButton.setOnAction(e ->{
            window.close();
        });
        okButton.setOnAction(e-> {
            if(creationNameField.getText()==null || creationNameField.getText().equals("")){
                AlertBox("Invalid Input", "Please enter a valid creation name!",580,50);
            }else{
                System.out.println(creationNameField.getText());
                window.close();
            }
        });

        Scene scene = new Scene(grid, 580, 130);
        window.setScene(scene);
        window.showAndWait();
        return creationNameField.getText();

    }

    public void deletionBox(){

    }

    public void AlertBox(String title, String message, int width, int height){
        Stage window= new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        VBox layout= new VBox();
        layout.setAlignment(Pos.CENTER);
        Label messageToDisplay= new Label(message);
        Button okButton= new Button("Ok");
        okButton.setOnAction(e -> window.close());
        layout.getChildren().addAll(messageToDisplay,okButton);


        Scene scene= new Scene(layout,width,height);
        window.setScene(scene);
        window.showAndWait();

    }

    public void recordingBox(String title, String message, String creationName, int width, int height){
        Stage window= new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        VBox layout= new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        HBox hLayout= new HBox();
        hLayout.setAlignment(Pos.CENTER);
        hLayout.setSpacing(10);

        Label messageToDisplay= new Label(message);
        Button okButton= new Button("Ok");
        Button cancelButton= new Button("Cancel");

        //Event Handling
        cancelButton.setOnAction(e-> window.close());
        okButton.setOnAction(e -> {
            Creations.recordingAudio(creationName);
            PauseTransition delay= new PauseTransition(Duration.seconds(5));
            delay.play();
            delay.setOnFinished(event  -> window.close());
        });
        hLayout.getChildren().addAll(okButton,cancelButton);
        layout.getChildren().addAll(messageToDisplay,hLayout);


        Scene scene= new Scene(layout,width,height);
        window.setScene(scene);
        window.showAndWait();
    }




}
