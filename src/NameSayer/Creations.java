package NameSayer;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;


public class Creations {
    PopUps _popUp = new PopUps();


    /*
     * listCreations() returns an observable list of all the creations in the "creations" folder.
     */
    public ObservableList<String> listCreations() {
        ObservableList<String> creationList = FXCollections.observableArrayList();
        checkCreationsDirectory();
        File currentDirectory = new File("./creations");
        File[] fileList = currentDirectory.listFiles();
        if (fileList != null) {
            for (File counter : fileList) {
                String temp = counter.getName();
                temp = temp.substring(0, temp.lastIndexOf('.'));
                creationList.add(temp);
            }
        }
        return creationList;
    }

    /*
     * checkCreationsDirectory() checks if the "creation" folder exist, and in the case that it doesn't it creates a new folder
     * using process builder.
     */
    public void checkCreationsDirectory() {
        File tempDir = new File("./creations");
        boolean exists = tempDir.exists();
        if (!exists) {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "mkdir creations");
            try {
                Process process = builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * checkTempCreationsDir checks if the tempCreations directory exist, and in the case that it does due to a user error it will
     * delete the directory.
     */
    public void checkTempCreationsDir() {
        File tempDir = new File("./tempCreations");
        boolean exists = tempDir.exists();
        if (exists) {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "rm -r ./tempCreations");
            try {
                Process process = builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * creationNameExist() checks to see if a creation with the current creationName already exist. This is to verify if there
     * is a need to overwrite a creation. The inputs to the method are just the creationName.
     */
    public boolean creationNameExist(String creationName) {
        //Checking to see if creations exist
        File currentDirectory = new File("./creations");
        File[] fileList = currentDirectory.listFiles();
        boolean creationNameExist = false;
        creationName = creationName + ".mp4";
        if (fileList != null) {
            for (File counter : fileList) {
                if (counter.getName().equals(creationName)) {
                    creationNameExist = true;
                    return creationNameExist;
                }
            }
        }
        return creationNameExist;

    }

    /*
     * getSelectedCreation gets the selected creation from the User in the ListView.
     */
    public String getSelectedCreation() {
        NameSayerGUI temp = new NameSayerGUI();
        String selection;
        ObservableList<String> tempSelection = temp.getSelectedCreation();
        selection = tempSelection.get(0);
        return selection;
    }

    /*
     * getSelectedCreationPath() when given a boolean returns the path of the selected creation by the user in the ListView. The
     * two types of Strings that can be returned are the path with quotes and the path without quotes.
     */
    public String getSelectedCreationPath(boolean withQuotations) {
        String selection = getSelectedCreation();
        if (withQuotations) {
            return "./creations/" + "\"" + selection + "\"" + ".mp4";
        } else {
            return "./creations/" + selection + ".mp4";
        }
    }

    /*
     * createCreation() given the ListView, creates the new creation by the user.
     */
    public void createCreation(ListView listView) {
        //Call to the pop-up to check for existing creation, and to input the name
        String[] combinedOutput = _popUp.creationBox("Create new Creation", "Please enter the name of the creation without \nthe extension", "Ok", "Cancel");
        //Assigning the combinedOutput to individual variables
        String creationName = combinedOutput[0];
        boolean[] isCancelRecordingBox;
        int isCancelCreationBox = Integer.parseInt(combinedOutput[1]);
        //Check if creations folder exist
        checkCreationsDirectory();
        if (!creationNameExist(creationName) && isCancelCreationBox == 0) {
            //Setting up the commands for ProcessBuilder
            ProcessBuilder makeTempCreations = new ProcessBuilder("/bin/bash", "-c", "mkdir ./tempCreations");
            String makeVideoCmd = "ffmpeg -f lavfi -i color=c=blue:s=600x600:d=5 -vf \"drawtext=fontfile=/usr/share/fonts/truetype/ubuntu/Ubuntu-RI.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + creationName + "\"" + " ./tempCreations/" + "\"" + creationName + "\"" + ".mp4";
            ProcessBuilder makeVideo = new ProcessBuilder("/bin/bash", "-c", makeVideoCmd);
            try {
                Process tempCreationsFolder = makeTempCreations.start();
                Process makeVideoProcess = makeVideo.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //If cancel was not pressed, start the recording for the creation
            if (isCancelCreationBox == 0) {
                isCancelRecordingBox = _popUp.recordingBox("Recording", "Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n", creationName, 580, 100);
                if (!isCancelRecordingBox[0]) {
                    //Prompt the recording box
                    _popUp.confirmRecordingBox(creationName, listView);
                }
            }
        } else if (isCancelCreationBox == 1) {
            //Don't need to anything if cancel was pressed, thus there is no implementation needed
        } else {
            //In the case that the User wants to override the existing creation
            //Done via threads.
            Platform.runLater(() -> {
                _popUp.overrideCreationBox("Override Existing Creation", "Would you like to override the existing creation?", creationName, 580, 100, listView);
            });
        }
    }

    /*
     * recordingAudio() takes in the name of the creation and is used to start the recording of the audio. NOTE: this method,
     * does not present a pop-up but does the recording in the background. This method has been implemented with threading
     * in mind to ensure concurrency.
     */
    public void recordingAudio(String creationName) {
        //Creation of a new task for multi-threading
        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                String recordingCmd = "ffmpeg -f alsa -ac 2 -i default -t 5  ./tempCreations/" + "\"" + creationName + "\"" + ".wav";
                ProcessBuilder recordingAudio = new ProcessBuilder("/bin/bash", "-c", recordingCmd);
                try {
                    Process process = recordingAudio.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        //Recording in the background thread, to ensure concurrency of the GUI.
        Thread thread = new Thread(task);
        thread.start();
    }

    /*
     * combineVideoAndAudio() given the creationName, and the ListView component is in charge of combining the video and the audio
     * in the tempCreations folder and create the final creation which gets placed in the creations folder.
     */
    public void combineVideoAndAudio(String creationName, ListView listView) {
        //Seting up the commands for process builder
        String cmd = "ffmpeg -i ./tempCreations/" + "\"" + creationName + "\"" + ".mp4 -i ./tempCreations/" + "\"" + creationName + "\"" + ".wav -c:v copy -c:a aac -strict experimental ./creations/" + "\"" + creationName + "\"" + ".mp4 ";
        ProcessBuilder combiningMediaProcess = new ProcessBuilder("/bin/bash", "-c", cmd);
        ProcessBuilder removeTempCreations = new ProcessBuilder("/bin/bash", "-c", "rm -r ./tempCreations");
        try {
            //Adding a pause to ensure that the files are combined before they are deleted
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.play();
            Process process = combiningMediaProcess.start();
            delay.setOnFinished(event -> {
                try {
                    //Removing the tempCreations only after the combining is finished
                    Process process1 = removeTempCreations.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Adding the new creation to the ListView, for the user to see
        listView.getItems().add(creationName);
    }


    /*
     * playCreation() given the mediaView component, plays the selected creation in the background thread.
     */
    public void playCreation(MediaView mediaView) {
        //Get the selection from the user
        String selection = getSelectedCreation();
        String selectionPath = getSelectedCreationPath(false);

        if (selection == null || selection.equals("")) {
            _popUp.AlertBox("Invalid Selection", "Please choose a valid selection from the side bar", 580, 50);
        } else {
            //Creating a seperate thread to ensure concurrency
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Media media = new Media(new File(selectionPath).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    //Ensuring that the actual playing of the creation happens on the GUI
                    Platform.runLater(() -> {
                        mediaView.getMediaPlayer().stop();
                        mediaView.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();

                    });
                }
            });
            thread.start();

        }
    }

    /*
     * deleteCreation() deletes the creation given the listView component. This method is in charge of the physical deletion
     * in the creations folder and the removing of the creation from the listView component.
     */
    public void deleteCreation(ListView listView, GridPane mainGrid, Rectangle rect) {
        //Setting up the commands for the process builder
        String selection = getSelectedCreation();
        String selectionPath = getSelectedCreationPath(true);
        String cmd = "rm -r " + selectionPath;

        if (selection != null) {
            //Confirming the deletion, by using the box made in PopUps
            String[] combinedArray = _popUp.creationBox("Deleting Confirmation", "Are you sure you want to delete the creation? \nPlease enter the name of the creation to continue", "Ok", "Cancel");
            String textFieldText = combinedArray[0];
            int isCancel = Integer.parseInt(combinedArray[1]);
            //Ensuring that the creationName matches the name inputted in the textfield.
            if (selection.equals(textFieldText) && isCancel == 0) {
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                try {
                    //Deleting the creation
                    Process process = builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Updating the ListView component with the deleted creation
                int selectedCell = listView.getSelectionModel().getSelectedIndex();
                listView.getItems().remove(selectedCell);
                mainGrid.getChildren().addAll(rect);

            } else {
                if (isCancel == 0) {
                    //In the case the creationName does not match the inputted name in textfield.
                    _popUp.AlertBox("Invalid Input!", "The name did not match the creation, please check again", 580, 50);
                    deleteCreation(listView, mainGrid,rect);
                }
            }
        } else {
            _popUp.AlertBox("Invalid Selection!", "Please choose a selection from the sidebar", 580, 50);
        }

    }

    /**
     * reggexChecker() uses reggex to check for any invalid characters. NOTE: It is important to know that the method outputs
     * true if the character was found.
     */
    public boolean reggexChecker(String creationName) {
        String pattern = "[\\.\\\\!@#$%\\^&\\*\\(\\)\\{\\}\\+\\[\\]\\|]";
        return Pattern.compile(pattern).matcher(creationName).find();
    }

}
