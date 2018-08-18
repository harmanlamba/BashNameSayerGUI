

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Creations {

    public ArrayList<String> listCreations(){
        ArrayList<String> creationList = new ArrayList<String>();
        //Checking to see if creations exsist
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
        File currentDirectory= new File("./creations");
        File[] fileList= currentDirectory.listFiles();
        for(File counter:fileList){
            int counter1=0;
            String temp=counter.getName();
            temp=temp.substring(0,temp.lastIndexOf('.'));
            creationList.add(temp);
        }
        return creationList;
    }






}
