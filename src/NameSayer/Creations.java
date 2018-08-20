package NameSayer;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;


public class Creations {
    PopUps _popUp = new PopUps();


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

    public void checkCreationsDirectory() {
        //Checking to see if creations exist
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

    public String getSelectedCreation() {
        NameSayerGUI temp = new NameSayerGUI();
        String selection;
        ObservableList<String> tempSelection = temp.getSelectedCreation();
        selection = tempSelection.get(0);
        return selection;
    }

    public String getSelectedCreationPath(boolean withQuotations) {
        String selection = getSelectedCreation();
        if (withQuotations) {
            return "./creations/" + "\"" + selection + "\"" + ".mp4";
        } else {
            return "./creations/" + selection + ".mp4";
        }
    }

    public void createCreation(ListView listView) {
        String[] combinedOutput = _popUp.creationBox("Create new Creation", "Please enter the name of the creation without \nthe extension", "Ok", "Cancel");
        String creationName = combinedOutput[0];
        boolean[] isCancelRecordingBox;
        int isCancelCreationBox = Integer.parseInt(combinedOutput[1]);
        checkCreationsDirectory();
        if (!creationNameExist(creationName) && isCancelCreationBox == 0) {
            ProcessBuilder makeTempCreations = new ProcessBuilder("/bin/bash", "-c", "mkdir ./tempCreations");
            String makeVideoCmd = "ffmpeg -f lavfi -i color=c=blue:s=600x600:d=5 -vf \"drawtext=fontfile=/usr/share/fonts/truetype/ubuntu/Ubuntu-RI.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + creationName + "\"" + " ./tempCreations/" + "\"" + creationName + "\"" + ".mp4";
            System.out.println("Make video command is:" + makeVideoCmd);
            ProcessBuilder makeVideo = new ProcessBuilder("/bin/bash", "-c", makeVideoCmd);
            try {
                Process tempCreationsFolder = makeTempCreations.start();
                Process makeVideoProcess = makeVideo.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isCancelCreationBox == 0) {
                isCancelRecordingBox = _popUp.recordingBox("Recording", "Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n", creationName, 580, 100);
                if (!isCancelRecordingBox[0]) {
                    _popUp.confirmRecordingBox(creationName, listView);
                }
            }
        } else if (isCancelCreationBox == 1) {

        } else {
            //Implementation for override or cancel the creation
            Platform.runLater(() -> {
                _popUp.overrideCreationBox("Override Existing Creation", "Would you like to override the existing creation?", creationName, 580, 100, listView);
            });
        }
    }

    public void recordingAudio(String creationName) {
        Task task= new Task<Void>() {
            @Override
            protected Void call() {
                String recordingCmd = "ffmpeg -f alsa -ac 2 -i default -t 5  ./tempCreations/" + "\"" + creationName + "\"" + ".m4a";
                ProcessBuilder recordingAudio = new ProcessBuilder("/bin/bash", "-c", recordingCmd);
                try {
                    Process process = recordingAudio.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread= new Thread(task);
        thread.start();
    }

    public void combineVideoAndAudio(String creationName, ListView listView) {
        String cmd = "ffmpeg -i ./tempCreations/" + "\"" + creationName + "\"" + ".mp4 -i ./tempCreations/" + "\"" + creationName + "\"" + ".m4a -c:v copy -c:a aac -strict experimental ./creations/" + "\"" + creationName + "\"" + ".mp4 ";
        System.out.println(cmd);
        ProcessBuilder combiningMediaProcess = new ProcessBuilder("/bin/bash", "-c", cmd);
        ProcessBuilder removeTempCreations = new ProcessBuilder("/bin/bash", "-c", "rm -r ./tempCreations");
        try {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.play();
            Process process = combiningMediaProcess.start();
            delay.setOnFinished(event -> {
                try {
                    Process process1 = removeTempCreations.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView.getItems().add(creationName);
    }


    public void playCreation(MediaView mediaView) {
        String selection = getSelectedCreation();
        String selectionPath = getSelectedCreationPath(false);
        if (selection == null || selection.equals("")) {
            _popUp.AlertBox("Invalid Selection", "Please choose a valid selection from the side bar", 580, 50);
        } else {
            System.out.println(new File(selectionPath).toURI().toString());
            Media media = new Media(new File(selectionPath).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            Platform.runLater(() -> {
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
            });
            System.out.println("Currently PLaying: " + selectionPath);
        }
    }

    public void deleteCreation(ListView listView) {
        String selection = getSelectedCreation();
        String selectionPath = getSelectedCreationPath(true);
        String cmd = "rm -r " + selectionPath;

        if (selection != null) {
            String[] combinedArray = _popUp.creationBox("Deleting Confirmation", "Are you sure you want to delete the creation? \nPlease enter the name of the creation to continue", "Ok", "Cancel");
            String textFieldText = combinedArray[0];
            int isCancel = Integer.parseInt(combinedArray[1]);
            if (selection.equals(textFieldText)) {
                System.out.println("Deleting the file: " + cmd);
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                try {
                    Process process = builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int selectedCell = listView.getSelectionModel().getSelectedIndex();
                listView.getItems().remove(selectedCell);
            } else {
                if (isCancel == 0) {
                    _popUp.AlertBox("Invalid Input!", "The name did not match the creation, please check again", 580, 50);
                    deleteCreation(listView);
                }
            }
        } else {
            _popUp.AlertBox("Invalid Selection!", "Please choose a selection from the sidebar", 580, 50);
        }

    }
}
