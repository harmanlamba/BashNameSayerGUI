package NameSayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Creations {
    PopUps _popUp= new PopUps();


    public ObservableList<String> listCreations(){
        ObservableList<String> creationList = FXCollections.observableArrayList();
        checkCreationsDirectory();
        File currentDirectory= new File("./creations");
        File[] fileList= currentDirectory.listFiles();
        for(File counter:fileList){
            String temp=counter.getName();
            temp=temp.substring(0,temp.lastIndexOf('.'));
            creationList.add(temp);
        }
        return creationList;
    }

    public void checkCreationsDirectory(){
        //Checking to see if creations exist
        File tempDir= new File("./creations");
        boolean exists= tempDir.exists();
        if(!exists){
            ProcessBuilder builder= new ProcessBuilder("/bin/bash","-c","mkdir creations");
            try {
                Process process= builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean creationNameExist(String creationName){
        //Checking to see if creations exist
        File currentDirectory= new File("./creations");
        File[] fileList= currentDirectory.listFiles();
        boolean creationNameExist=false;
        creationName=creationName+".mp4";
        if(fileList!= null){
            for(File counter:fileList){
                if(counter.getName().equals(creationName)){
                    creationNameExist=true;
                    return creationNameExist;
                }
            }
        }
        return creationNameExist;

    }

    public String getSelectedCreation(){
        NameSayerGUI temp= new NameSayerGUI();
        String selection;
        ObservableList<String> tempSelection=temp.getSelectedCreation();
        selection=tempSelection.get(0);
        return selection;
    }

    public String getSelectedCreationPath(boolean withQuotations){
        String selection=getSelectedCreation();
        if(withQuotations){
            return "./creations/"+"\""+selection + "\"" + ".mp4";
        } else {
            return "./creations/"+selection + ".mp4";
        }
    }

    public void createCreation(){
        String creationName=_popUp.creationBox("Create new Creation", "Please enter the name of the creation without \nthe extension", "Ok", "Cancel");
        checkCreationsDirectory();
        if(!creationNameExist(creationName)){
            ProcessBuilder makeTempCreations= new ProcessBuilder("/bin/bash","-c","mkdir ./tempCreations");
            String makeVideoCmd="ffmpeg -f lavfi -i color=c=blue:s=600x600:d=5 -vf \"drawtext=fontfile=/usr/share/fonts/truetype/ubuntu/Ubuntu-RI.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + creationName+"\"" +" ./tempCreations/" +creationName+".mp4";
            System.out.println(makeVideoCmd);
            ProcessBuilder makeVideo= new ProcessBuilder("/bin/bash","-c",makeVideoCmd);
            try {
                Process tempCreationsFolder= makeTempCreations.start();
                Process makeVideoProcess= makeVideo.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            _popUp.recordingBox("Recording","Clicking ok will start the recording \nafter the recording is finished\nthe window will close by its self\n",creationName,580,100);
            //confirm recording
        }else{
            //Implementation for override or cancel the creation
        }



    }

    public static void recordingAudio(String creationName){
        String recordingCmd="ffmpeg -f alsa -ac 2 -i default -t 5  ./tempCreations/" +creationName+".mkv";
        ProcessBuilder recordingAudio= new ProcessBuilder("/bin/bash","-c",recordingCmd);
        try {
            Process process= recordingAudio.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void confirmRecording(){

    }

    public void combineVideoAndAudio(String creationName){
        String cmd="ffmpeg -i ./tempCreations/"+creationName+".mp4 -i ./tempCreations/"+creationName+".mkv -c:v copy -c:a aac -strict experimental ./creations/"+ creationName+".mp4 ";
        ProcessBuilder combiningMediaProcess= new ProcessBuilder("/bin/bash","-c",cmd);
        try {
            Process process= combiningMediaProcess.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void playCreation(MediaView mediaView){
        String selection =getSelectedCreation();
        String selectionPath=getSelectedCreationPath(false);
        if (selection == null ||selection.equals("") ){
            _popUp.AlertBox("Invalid Selection","Please choose a valid selection from the side bar",580,50);
        }else{
            System.out.println(new File (selectionPath).toURI().toString());
            Media media= new Media(new File (selectionPath).toURI().toString());
            MediaPlayer mediaPlayer= new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.play();
            System.out.println("Currently PLaying: "+selectionPath);
        }
    }

    public void deleteCreation(ListView listView){
        String selection=getSelectedCreation();
        String selectionPath=getSelectedCreationPath(true);
        String cmd= "rm -r "+ selectionPath;

        /*
        if (selection != null) {
            String[] combinedArray= _popUp.creationBox("Deleting Confirmation", "Are you sure you want to delete the creation? \nPlease enter the name of the creation to continue","Ok","Cancel");
            String textFieldText= combinedArray[0];
            boolean isCancel= Boolean.parseBoolean(combinedArray[1]);
            if(selection.equals(textFieldText)){
                System.out.println("Deleting the file: " + cmd);
                ProcessBuilder builder= new ProcessBuilder("/bin/bash","-c",cmd);
                try {
                    Process process= builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int selectedCell= listView.getSelectionModel().getSelectedIndex();
                listView.getItems().remove(selectedCell);
            }else{
                if(!isCancel){
                    _popUp.AlertBox("Invalid Input!", "The name did not match the creation, please check again",580,50);
                    deleteCreation(listView);
                }
            }
        }else{
            _popUp.AlertBox("Invalid Selection!", "Please choose a selection from the sidebar",580,50);
        }
        */
    }
}
