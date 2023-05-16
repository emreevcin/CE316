package com.example.iae.ce316;

import java.io.File;

public class DirectoryHandler {
    private String configurationsDir = "configurations";
    private String projectsDir = "projects";


    private DirectoryHandler() {
        File file = new File(configurationsDir);
        boolean isConfDirExists = file.exists();
        boolean isCreated = false ;
        if(!isConfDirExists){
            isCreated = file.mkdir();
        }
        if(isCreated){
            System.out.println("Succesfully created directory :"+file.getName());
        }
        file = new File(projectsDir);
        boolean isProjectDirExists = file.exists();
        if(!isProjectDirExists){
            isCreated=file.mkdir();
        }
        if(isCreated){
            System.out.println("Succesfully created directory :"+file.getName());
        }
    }
    private static DirectoryHandler instance = null;

    public static DirectoryHandler getInstance(){
        if(instance == null){
            instance = new DirectoryHandler();
        }

        return instance ;
    }




}
