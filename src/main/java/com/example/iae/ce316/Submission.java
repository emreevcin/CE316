package com.example.iae.ce316;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Submission {
    private String studentID;
    private String error;
    private String output;
    private String status;
    private final Project project;
    private ImageView statusImage;
    private String directory;
    private String[] commands;

    public Submission(Project project,String directory) {
        this.studentID = directory;
        this.directory = directory;
        this.project = project;
    }

    public String getStudentID() {
        return studentID;
    }

    public Project getProject() {
        return project;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ImageView getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(ImageView statusImage) {
        this.statusImage = statusImage;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setCommands(){
        Configuration configuration = this.project.getConfiguration();
        String[] commands = Configuration.makeCommand(configuration.getLang(),configuration.getLib(),configuration.getArgs());
        this.commands = commands;

    }
    public String[] getCommands(){
        return this.commands;
    }
}
