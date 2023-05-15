package com.example.iae.ce316;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class Project {
    private final String title;
    private final Configuration configuration;
    private final ArrayList<Submission> submissions ;
    private final HashMap<Submission, ImageView> statusList ;
    private final HashMap<Submission,String> outputList;

    public Project(String title, Configuration configuration) {
        this.title = title;
        this.configuration = configuration;
        this.submissions = new ArrayList<>();
        this.statusList = new HashMap<>();
        this.outputList = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public String getConfigOutput() {
        return configOutput;
    }

    public void setConfigOutput(String configOutput) {
        this.configOutput = configOutput;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ArrayList<Submission> getSubmissions() {
        return submissions;
    }

    public HashMap<Submission, ImageView> getStatusList() {
        return statusList;
    }

    public HashMap<Submission, String> getOutputList() {
        return outputList;
    }
}
