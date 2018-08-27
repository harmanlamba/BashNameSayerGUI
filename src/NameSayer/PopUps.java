package NameSayer;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class PopUps {

    /*
     * creationBox() creates an alertbox for when a new creation has to be made. The alertbox was designed with the idea of reuse,
     * thus the creationBox() requires 4 parameters.
     */
    public String[] creationBox(String title, String message, String button1, String button2) {
        //isCancel is used to know if the cancel button was clicked on
        final int[] isCancel = new int[1];
        //Creating the GUI for the box
        Stage window = new Stage();
        //Ensuring that other windows canno't but used while this one is still up
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        //window.setX(320);
        //window.setY(150);

        //Creating and modifying the layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        //components and adding them to the layout
        Label messageToDisplay = new Label(message);
        TextField creationNameField = new TextField();
        creationNameField.setPromptText("Characters Not allowed:!@#$%^&*()+{}[]\\.");
        Button okButton = new Button(button1);
        Button cancelButton = new Button(button2);
        grid.maxHeight(700);
        grid.maxWidth(300);
        GridPane.setConstraints(messageToDisplay, 5, 5);
        GridPane.setConstraints(creationNameField, 5, 6);
        GridPane.setConstraints(okButton, 6, 6);
        GridPane.setConstraints(cancelButton, 7, 6);
        grid.getChildren().setAll(messageToDisplay, okButton, cancelButton, creationNameField);

        Creations creation = new Creations();

        //Setting up event handlers
        cancelButton.setOnAction(e -> {
            isCancel[0] = 1;
            window.close();

        });
        okButton.setOnAction(e -> {
            isCancel[0] = 0;
            boolean isInvalidCharacters = creation.reggexChecker(creationNameField.getText());
            if (creationNameField.getText() == null || creationNameField.getText().equals("") || isInvalidCharacters || creationNameField.getText().length() > 20 || creationNameField.getText().trim().isEmpty()) {
                AlertBox("Invalid Input", "Please enter a valid creation name:\nWithout special characters, non-empty,not just a space,\nand less than 20 characters long\n", 580, 100);
            } else {
                window.close();
            }

        });

        Scene scene = new Scene(grid, 580, 130);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
        //Setting up the return array, which returns the boolean if the cancel button was placed and the input to the
        //textfield.
        String[] combinedOutput = new String[2];
        combinedOutput[0] = creationNameField.getText();
        combinedOutput[1] = String.valueOf(isCancel[0]);
        return combinedOutput;

    }

    /*
     * AlertBox()  creates an alertBox, with a custom title, message, width and height. This method was made
     * for the multiple times that an alertbox may be needed, thus there are 4 parameters to the method.
     */
    public void AlertBox(String title, String message, int width, int height) {
        //Creating the stage and modifying its settings for the alertBox
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.centerOnScreen();

        //Creating the layout for the box
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(5);
        Label messageToDisplay = new Label(message);
        //Event handling
        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> window.close());
        layout.getChildren().addAll(messageToDisplay, okButton);


        Scene scene = new Scene(layout, width, height);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

    }

    /*
     * recordingBox() creates the stage that records the sound, and calls upon the recordingAudio method to record the audio
     * in the background. It returns a boolean array which indicates if the cancel was fired during the execution of the method.
     */
    public boolean[] recordingBox(String title, String message, String creationName, int width, int height) {
        //Creating the boolean array to know if the cancel button was clicked on.
        boolean[] isCancel = new boolean[1];
        //Creating the window for the GUI
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.centerOnScreen();

        //Setting up the layouts and their properties
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        HBox hLayout = new HBox();
        hLayout.setAlignment(Pos.CENTER);
        hLayout.setSpacing(10);

        //Creating the components
        Label messageToDisplay = new Label(message);
        Button okButton = new Button("Ok");
        Button cancelButton = new Button("Cancel");

        //Event Handling
        cancelButton.setOnAction(e -> {
            isCancel[0] = true;
            window.close();
        });
        okButton.setOnAction(e -> {
            isCancel[0] = false;
            //Disabling the other buttons due to the recording taking place
            cancelButton.setDisable(true);
            okButton.setDisable(true);
            //Changing the message to show the user that the recording is taking place
            messageToDisplay.setText("RECORDING");
            messageToDisplay.setTextFill(Color.RED);
            Creations creation = new Creations();
            creation.recordingAudio(creationName);
            //Closing the window after 5 seconds, which is the recording time
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.play();
            delay.setOnFinished(event -> window.close());
        });
        //Adding the components to the layouts
        hLayout.getChildren().addAll(okButton, cancelButton);
        layout.getChildren().addAll(messageToDisplay, hLayout);

        //Adding the layout to the scene, and the scene to the window.
        Scene scene = new Scene(layout, width, height);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
        return isCancel;
    }

    /*
     * confirmRecordingBox() creates an alert type box which is used to confirm the recording of the user given the creationName
     * and the ListView component. It allows the user to keep, the recording, redo the recording, or listen to the
     * recording.
     */
    public void confirmRecordingBox(String creationName, ListView listView) {
        //creating the stage for the alertbox, and modifying its properties
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle("Confirm Recording");
        window.centerOnScreen();

        //Creating the layout and making adjustments
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        buttonContainer.setAlignment(Pos.CENTER);

        //Creating the components
        Button redoButton = new Button("Redo");
        Button listenButton = new Button("Listen");
        Button keepButton = new Button("Keep");
        Label messageLabel = new Label("Please Confirm you recording");
        MediaView mv = new MediaView();

        //Adding components to Layout
        buttonContainer.getChildren().addAll(redoButton, listenButton, keepButton);
        GridPane.setConstraints(buttonContainer, 12, 6);
        GridPane.setConstraints(messageLabel, 12, 4);
        grid.getChildren().addAll(buttonContainer, messageLabel);


        //Event Handling
        keepButton.setOnAction(e -> {
            //Combines the audio and video into a single creation by calling combineVideoAndAudio method
            Creations creation = new Creations();
            creation.combineVideoAndAudio(creationName, listView);
            window.close();
        });

        redoButton.setOnAction(e -> {
            //Calling the recording box again to record new audio, recording automatically happens in the background thread
            //due to recordingBox calling recordingAudio
            boolean[] isCancel = recordingBox("Recording", "Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n", creationName + "temp", 580, 100);
            //In the case that the redo was done and not canceled
            if (!isCancel[0]) {
                //Seting up the process builder commands to delete and rename the creations
                String cmd = "rm -r ./tempCreations/" + "\"" + creationName + "\"" + ".wav";
                String cmd2 = "mv ./tempCreations/" + "\"" + creationName + "temp" + "\"" + ".wav" + " " + "./tempCreations/" + "\"" + creationName + "\"" + ".wav";
                //Deleting old creations
                ProcessBuilder remover = new ProcessBuilder("/bin/bash", "-c", cmd);
                //Renaming new creations in order to play and delete later on
                ProcessBuilder renamer = new ProcessBuilder("/bin/bash", "-c", cmd2);
                try {
                    Process process = remover.start();
                    Process process2 = renamer.start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }


        });

        listenButton.setOnAction(e -> {
            //Playing the creation in the background thread to ensure concurrency
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    PauseTransition delay = new PauseTransition(Duration.seconds(5));
                    //Loading the media to the mediaPlayer to play the audio files
                    String file = "./tempCreations/" + creationName + ".wav";
                    Media media = new Media(new File(file).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    Platform.runLater(() -> {
                        mv.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                    });
                    //Disabling the buttons for 5 seconds while the audio file is being played back
                    delay.play();
                    listenButton.setDisable(true);
                    redoButton.setDisable(true);
                    keepButton.setDisable(true);
                    //Re-enabling the buttons after the audio file finishing.
                    delay.setOnFinished(event -> {
                        listenButton.setDisable(false);
                        redoButton.setDisable(false);
                        keepButton.setDisable(false);
                    });
                }
            });
            thread.start();

        });

        Scene scene = new Scene(grid, 480, 120);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
    }

    /*
     * overrideCreationBox() presents a stage that verifies if the user wants to override a creation given the title of the stage, the
     * message to display to the users, the creationName, width, height, and the ListView component.
     */
    public void overrideCreationBox(String title, String message, String creationName, int width, int height, ListView listView) {
        //Creating the new GUI stage for the box and setting up its properties
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.centerOnScreen();

        //Setting up the layout and their respective properties
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        HBox hLayout = new HBox();
        hLayout.setAlignment(Pos.CENTER);
        hLayout.setSpacing(10);

        //Creating the components
        Label messageToDisplay = new Label(message);
        Button okButton = new Button("Ok");
        Button cancelButton = new Button("Cancel");
        //Assigning the components to the layout
        hLayout.getChildren().addAll(okButton, cancelButton);
        layout.getChildren().addAll(messageToDisplay, hLayout);


        //Event Handling
        okButton.setOnAction(e -> {
            //Setting up the process builder commands
            String cmd = "rm -r ./creations/" + creationName + ".mp4";
            String mkDir = "mkdir ./tempCreations";
            String mkMp4 = "ffmpeg -f lavfi -i color=c=blue:s=600x600:d=5 -vf \"drawtext=fontfile=/usr/share/fonts/truetype/ubuntu/Ubuntu-RI.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + creationName + "\"" + " ./tempCreations/" + creationName + ".mp4";
            //Removing the existing creation, and making the tempDir again with the correct video file
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", mkDir);
            ProcessBuilder builder3 = new ProcessBuilder("/bin/bash", "-c", mkMp4);
            try {
                Process process = builder.start();
                Process process2 = builder2.start();
                Process process3 = builder3.start();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ObservableList<String> itemsInListView = listView.getItems();
            int count = 0;
            for (String counter : itemsInListView) {
                if (counter.equals(creationName)) {
                    break;
                } else {
                    count++;
                }
            }
            //Removing the creation from the ListView
            listView.getItems().remove(count);
            //Recoring the audio for the creation
            recordingBox("Recording", "Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n", creationName, 580, 100);
            //Confirming the recording
            confirmRecordingBox(creationName, listView);
            window.close();
        });

        cancelButton.setOnAction(e -> window.close());
        //Adding the layout to the scene, and the scene to the window.
        Scene scene = new Scene(layout, width, height);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

    }


}
