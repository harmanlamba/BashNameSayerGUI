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

import java.io.File;
import java.io.IOException;


public class PopUps {

    public String[] creationBox(String title, String message, String button1, String button2) {
        final int[] isCancel = new int[1];
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

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

        Creations creation= new Creations();


        //Setting up event handlers
        cancelButton.setOnAction(e -> {
            isCancel[0] = 1;
            window.close();

        });
        okButton.setOnAction(e -> {
            isCancel[0] = 0;
            boolean validChacters= creation.reggexCheker(creationNameField.getText());
            if (creationNameField.getText() == null || creationNameField.getText().equals("") || validChacters) {
                AlertBox("Invalid Input", "Please enter a valid creation name!", 580, 50);
            } else {
                System.out.println(creationNameField.getText());
                window.close();
            }

        });

        Scene scene = new Scene(grid, 580, 130);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
        String[] combinedOutput = new String[2];
        combinedOutput[0] = creationNameField.getText();
        combinedOutput[1] = String.valueOf(isCancel[0]);
        return combinedOutput;

    }

    public void AlertBox(String title, String message, int width, int height) {
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        Label messageToDisplay = new Label(message);
        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> window.close());
        layout.getChildren().addAll(messageToDisplay, okButton);


        Scene scene = new Scene(layout, width, height);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

    }

    public boolean[] recordingBox(String title, String message, String creationName, int width, int height) {
        boolean[] isCancel = new boolean[1];
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        HBox hLayout = new HBox();
        hLayout.setAlignment(Pos.CENTER);
        hLayout.setSpacing(10);

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
            cancelButton.setDisable(true);
            okButton.setDisable(true);
            messageToDisplay.setText("RECORDING");
            messageToDisplay.setTextFill(Color.RED);
            Creations creation = new Creations();
            creation.recordingAudio(creationName);
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.play();
            delay.setOnFinished(event -> window.close());
        });
        hLayout.getChildren().addAll(okButton, cancelButton);
        layout.getChildren().addAll(messageToDisplay, hLayout);

        Scene scene = new Scene(layout, width, height);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();
        return isCancel;
    }

    public void confirmRecordingBox(String creationName, ListView listView) {
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle("Confirm Recording");
        window.setX(320);
        window.setY(150);

        //Layouts
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(10);
        buttonContainer.setAlignment(Pos.CENTER);

        //Components
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
            Platform.runLater(() -> {
                Creations creation = new Creations();
                creation.combineVideoAndAudio(creationName, listView);
                window.close();
            });
        });

        redoButton.setOnAction(e -> {
            boolean[] isCancel = recordingBox("Recording", "Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n", creationName + "temp", 580, 100);
            if (!isCancel[0]) {
                String cmd = "rm -r ./tempCreations/" + "\"" + creationName + "\"" + ".wav";
                String cmd2 = "mv ./tempCreations/" + "\"" + creationName + "temp" + "\"" + ".wav" + " " + "./tempCreations/" + "\"" + creationName + "\"" + ".wav";
                ProcessBuilder remover = new ProcessBuilder("/bin/bash", "-c", cmd);
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
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    PauseTransition delay = new PauseTransition(Duration.seconds(5));
                    String file = "./tempCreations/" + creationName + ".wav";
                    Media media = new Media(new File(file).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    Platform.runLater(() -> {
                        mv.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                    });
                    delay.play();
                    listenButton.setDisable(true);
                    redoButton.setDisable(true);
                    keepButton.setDisable(true);
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

    public void overrideCreationBox(String title, String message, String creationName, int width, int height, ListView listView) {
        Stage window = new Stage();
        window.initModality((Modality.APPLICATION_MODAL));
        window.setTitle(title);
        window.setX(320);
        window.setY(150);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        HBox hLayout = new HBox();
        hLayout.setAlignment(Pos.CENTER);
        hLayout.setSpacing(10);

        Label messageToDisplay = new Label(message);
        Button okButton = new Button("Ok");
        Button cancelButton = new Button("Cancel");


        hLayout.getChildren().addAll(okButton, cancelButton);
        layout.getChildren().addAll(messageToDisplay, hLayout);


        //Event Handling
        okButton.setOnAction(e -> {
            String cmd = "rm -r ./creations/" + creationName + ".mp4";
            System.out.println(cmd);
            String mkDir = "mkdir ./tempCreations";
            String mkMp4 = "ffmpeg -f lavfi -i color=c=blue:s=600x600:d=5 -vf \"drawtext=fontfile=/usr/share/fonts/truetype/ubuntu/Ubuntu-RI.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + creationName + "\"" + " ./tempCreations/" + creationName + ".mp4";
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
            listView.getItems().remove(count);
            recordingBox("Recording", "Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n", creationName, 580, 100);
            confirmRecordingBox(creationName, listView);
            window.close();
        });

        cancelButton.setOnAction(e -> window.close());

        Scene scene = new Scene(layout, width, height);
        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

    }


}
