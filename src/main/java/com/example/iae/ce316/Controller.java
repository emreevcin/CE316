package com.example.iae.ce316;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // TODO: EDIT & DELETE DATABASE (Evcin)
    // TODO: File names (submissions -> projects) -> means : src/submission actually works but not for all submissions that has same configurations with different projects with same zip file
     // database
    Database d = Database.getInstance();
    // variables
    private ArrayList<Project> projectList = new ArrayList<>();
    private ArrayList<Configuration> configurationList = new ArrayList<>();
    private ArrayList<Submission> submissionList = new ArrayList<>();
    @FXML
    private VBox landingPage;
    @FXML
    private VBox submissionPage;
    @FXML
    private VBox resultsPage;
    @FXML
    private VBox configurationPage;
    @FXML
    private VBox projectPage;
    @FXML
    private ChoiceBox<String> langBox;
    @FXML
    private ChoiceBox<String> projectBoxResults;
    @FXML
    private ChoiceBox<String> projectBoxSubmission;
    @FXML
    private ChoiceBox<String> configBox;
    @FXML
    private ListView<String> fileListSubmission;
    @FXML
    private TableView<Submission> resultTable;
    @FXML
    private TableColumn<Submission, ImageView> statusCol;
    @FXML
    private TableColumn<Submission, String> studentCol;
    @FXML
    private TableColumn<Submission, String> errorCol;
    @FXML
    private TextField configTitle;
    @FXML
    private TextField configFile;
    @FXML
    private TextField configCommandLib;
    @FXML
    private TextField configCommandArgs;
    @FXML
    private TextField projectTitle;
    @FXML
    private TextArea configOutput;
    @FXML
    private TextArea submissionOutput;
    @FXML
    private VBox configPageList ;
    @FXML
    private VBox configPageAdd ;
    @FXML
    private VBox configList ;
    @FXML
    private Label configLabel;
    @FXML
    private VBox editModal ;
    @FXML
    private TextField editConfigTitle;
    @FXML
    private TextField editConfigFile;
    @FXML
    private TextField editConfigLib;
    @FXML
    private TextField editConfigArgs;
    @FXML
    private ChoiceBox<String> editConfigLang;

    private Configuration editConfig;
    private HBox editBox;
    private Label editLabel;
    private HashMap<String, String> configFileMap = new HashMap<>(); // can be changeable due to it will hold just one path of one file -> String configAbsPath
    private HashMap<String, String> subFileMap = new HashMap<>();// can be changeable due to it will hold just one path of one file -> String subAbsPath



    // initializing ui elements
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Database:"+d);

        try {
            projectList.addAll(d.getAllProjects());
            configurationList.addAll(d.getAllConfigurations());
            submissionList.addAll(d.getAllSubmissions());

            for (Configuration c : configurationList) {
                configBox.getItems().add(c.getTitle());
            }

            for (Submission s: submissionList) {
                for (Project p : projectList) {
                    if (s.getProject().getTitle().equals(p.getTitle())) {
                        p.getSubmissions().add(s);
                    }
                }
            }

            for (Project p : projectList) {
                projectBoxSubmission.getItems().add(p.getTitle());
                projectBoxResults.getItems().add(p.getTitle());
                // p.getSubmissions().addAll(submissionList);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // table view initializing

        statusCol.setCellValueFactory(new PropertyValueFactory<>("statusImage"));
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        errorCol.setCellValueFactory(new PropertyValueFactory<>("error"));
        resultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
               submissionOutput.setText(newSelection.getOutput());
            }
        });


        // choice box initializing
        langBox.setValue("Select a language");
        langBox.getItems().addAll("C","C++","Java","Python");
        projectBoxResults.setOnAction(e -> selectProject());
    }
    // methods

    public void switchToLandingPage() {
        landingPage.setVisible(true);
        submissionPage.setVisible(false);
        resultsPage.setVisible(false);
        configurationPage.setVisible(false);
        projectPage.setVisible(false);
    }

    public void switchToSubmissionPage() {
        landingPage.setVisible(false);
        submissionPage.setVisible(true);
        resultsPage.setVisible(false);
        configurationPage.setVisible(false);
        projectPage.setVisible(false);
    }

    public void switchToResultsPage() {
        landingPage.setVisible(false);
        submissionPage.setVisible(false);
        resultsPage.setVisible(true);
        configurationPage.setVisible(false);
        projectPage.setVisible(false);
    }

    public void switchToConfigurationPage() {
        landingPage.setVisible(false);
        submissionPage.setVisible(false);
        resultsPage.setVisible(false);
        configurationPage.setVisible(true);
        projectPage.setVisible(false);
    }
    public void switchToProjectPage(){
        landingPage.setVisible(false);
        submissionPage.setVisible(false);
        resultsPage.setVisible(false);
        configurationPage.setVisible(false);
        projectPage.setVisible(true);
    }
    public void addFileSubmission() throws IOException {
        String[] fileDetails = addFile();
        //fileDetails[0] = file name , fileDetails[1] = file path
        if(fileDetails != null){
            fileListSubmission.getItems().add(fileDetails[0]);
            subFileMap.put(fileDetails[0],fileDetails[1]);
        }
    }
    public void addFileConfiguration() throws IOException {
        configFileMap.clear();
        String[] fileDetails = addFile();
        //fileDetails[0] = file name , fileDetails[1] = file path
        if(fileDetails != null){
            configFile.setText(fileDetails[0]);
            configFileMap.put(fileDetails[0],fileDetails[1]);
        }
    }
    private String[] addFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload File");
        File file = fileChooser.showOpenDialog(null);
        if(file == null){
            return null;
        }
        // check suffix of file is .zip  , then if it is not zip file , show error
        String suffix = file.getName().substring(file.getName().lastIndexOf("."));
        if(!suffix.equals(".zip")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File type not supported");
            alert.setContentText("Please select a .zip file");
            alert.showAndWait();
            return null;
        }
        String[] fileDetails = new String[2];
        fileDetails[0] = file.getName();
        fileDetails[1] = file.getAbsolutePath();
        return fileDetails;

    }

    public void submitSubmission() throws IOException, SQLException {
        String projectTitle = projectBoxSubmission.getValue();
        Project p = findProject(projectTitle);
        if(p == null){
            return;
        }
        if(fileListSubmission.getItems().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No files selected");
            alert.setContentText("Please select files to submit");
            alert.showAndWait();
            return;
        }
        String fileName = fileListSubmission.getItems().get(0);
        String filePath = subFileMap.get(fileName);

        ZipHandler.unzip(new File(filePath),1);

        String directory = "src/main/submissions/"+fileName.substring(0,fileName.lastIndexOf("."));

        Submission s = new Submission(p, directory);
        s.setCommands();
        HashMap<String,String> info = Executor.executeSubmission(s);
        if(info.get("output") != null){
            s.setOutput(info.get("output"));
        }
        boolean isOk = Executor.compare(s);
        try {
            if (isOk) {
                String imageUrl = getClass().getResource("/icons/ok.png").toExternalForm();
                if (imageUrl != null) {
                    Image okImage = new Image(imageUrl);
                    s.setStatus("OK");
                    s.setStatusImage(new ImageView(okImage));
                    s.setError("No Error");
                }
            } else {
                String deniedURL = getClass().getResource("/icons/denied.png").toExternalForm();
                if (deniedURL != null) {
                    Image deniedImage = new Image(deniedURL);
                    s.setStatus("Error");
                    s.setStatusImage(new ImageView(deniedImage));
                    if(!s.getOutput().equals("")){
                        s.setError("No Output Match");
                    }
                    else {
                        s.setError("Compiling/Interpreting Error");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        d.addSubmission(s);
        submissionList.add(s);
        p.getSubmissions().add(s);
        fileListSubmission.getItems().clear();
        if(projectBoxResults.getValue().equals(projectTitle)){
            resultTable.getItems().add(s);
        }
    }
    public void submitConfiguration() throws SQLException, IOException {
        String title = configTitle.getText();
        String commandLib = configCommandLib.getText();
        String commandArgs = configCommandArgs.getText();
        if(title.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No title");
            alert.setContentText("Please enter a title");
            alert.showAndWait();
            return;
        }
        if(langBox.getValue().equals("Select a language")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No language selected");
            alert.setContentText("Please select a language");
            alert.showAndWait();
            return;
        }
        if(configFile.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No files selected");
            alert.setContentText("Please select files to submit");
            alert.showAndWait();
            return;
        }
        String fileName = configFile.getText();
        String filePath = configFileMap.get(fileName);

        ZipHandler.unzip(new File(filePath),0);

        String directory = "src/main/configurations/"+fileName.substring(0,fileName.lastIndexOf("."));

        Configuration configuration = new Configuration(title,langBox.getValue(),commandLib,commandArgs,directory);


        // Creates com/example/iae/ce316/files/<title>.json
        JsonFileHandler.createJSONFile(configuration); // ??
        // Creates an entity in configurations table
        HashMap<String,String> info = Executor.executeConfiguration(configuration);
        configurationList.add(configuration);
        configBox.getItems().add(configuration.getTitle());

        configuration.setOutput(info.get("output"));
        d.addConfiguration(configuration);
    }
    public void submitProject() throws SQLException {
        String title = projectTitle.getText();
        Configuration config = findConfiguration(configBox.getValue());
        Project p = new Project(title,config);
        d.addProject(p);
        projectBoxResults.getItems().add(p.getTitle());
        projectList.add(p);
        projectBoxSubmission.getItems().add(p.getTitle());

    }
    private Configuration findConfiguration(String title){
        for(Configuration c : configurationList){
            if(c.getTitle().equals(title)){
                return c;
            }
        }
        return null;
    }
    private Project findProject(String title){
        for(Project p : projectList){
            if(p.getTitle().equals(title)){
                return p;
            }
        }
        return null;
    }
    private void selectProject(){
        String projectTitle = projectBoxResults.getValue();
        Project p = findProject(projectTitle);
        resultTable.getItems().clear();
        resultTable.getItems().addAll(p.getSubmissions());
        configOutput.setText(p.getConfiguration().getOutput());

    }
    public void configPageButtonHandler(){
        if(configPageAdd.isVisible()){// if list is not visible , make it visible
            configPageList.setVisible(true);
            configPageAdd.setVisible(false);
            editModal.setVisible(false);
            configLabel.setText("Add Configuration");

            configList.getChildren().clear();

            for (int i = 0; i < configurationList.size(); i++) {
                HBox hBox = new HBox();
                HBox buttons = new HBox();
                HBox.setHgrow(buttons, Priority.ALWAYS);
                hBox.setMaxHeight(30);
                hBox.setSpacing(10);
                hBox.setAlignment(Pos.CENTER_LEFT);
                hBox.maxWidth(configList.getWidth());
                hBox.setPadding(new Insets(10, 10, 10, 10));
                HBox.setMargin(hBox, new Insets(0, 0, 20, 0));
                hBox.setStyle("-fx-background-color: #DCDCDC; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #FFFFFF; -fx-border-width: 2px;");
                Label title = new Label(configurationList.get(i).getTitle());
                title.setPrefWidth(400);
                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3E54AC; -fx-font-family: \"Segoe UI\";");
                ImageView delete = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/delete.png"))));
                ImageView edit = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/edit.png"))));
                delete.setFitHeight(20);
                delete.setFitWidth(20);
                edit.setFitHeight(20);
                edit.setFitWidth(20);
                Button deleteButton = new Button();
                Button editButton = new Button();
                deleteButton.setGraphic(delete);
                editButton.setGraphic(edit);
                deleteButton.setStyle("-fx-background-color: transparent;");
                editButton.setStyle("-fx-background-color: transparent;");

                buttons.getChildren().addAll(deleteButton, editButton);
                buttons.setAlignment(Pos.CENTER_RIGHT);
                buttons.setSpacing(10);
                buttons.maxHeight(30);

                deleteButton.setPrefWidth(30);
                editButton.setPrefWidth(30);
                deleteButton.setPrefHeight(30);
                editButton.setPrefHeight(30);
                deleteButton.setOnAction(e -> {
                    Configuration c = findConfiguration(title.getText());
                    if(c == null){
                        return;
                    }
                    configList.getChildren().remove(hBox);
                    configurationList.remove(c);
                    configBox.getItems().remove(c.getTitle());
                    // TODO : delete configuration from database

                });
                editButton.setOnAction(e -> {
                    Configuration config = findConfiguration(title.getText());
                    if(config == null){
                        return;
                    }
                    editConfig = config;
                    editLabel = title;
                    editBox = hBox;
                    openEditConfiguration(config);
                });

                hBox.getChildren().addAll(title, buttons);
                configList.getChildren().add(hBox);
            }

        }
        else if(configPageList.isVisible()){
            configPageList.setVisible(false);
            configPageAdd.setVisible(true);
            editModal.setVisible(false);
            configLabel.setText("Configurations");
        }
    }

    public void openEditConfiguration(Configuration config){
        configFileMap.clear();
        editModal.setVisible(true);
        configPageList.setDisable(true);
        configPageList.setEffect(new BoxBlur(3,3,3));
        editConfigTitle.setText(config.getTitle());
        editConfigLang.setValue(config.getLang());
        editConfigLib.setText(config.getLib());
        editConfigArgs.setText(config.getArgs());
        String zipName = config.getDirectory().substring(config.getDirectory().lastIndexOf("/")+1);
        editConfigFile.setText(zipName+".zip");
    }
    public void submitConfigurationEdit() throws IOException {
        String title = editConfigTitle.getText();
        String lang = editConfigLang.getValue();
        String lib = editConfigLib.getText();
        String args = editConfigArgs.getText();
        Configuration configuration = editConfig;
        if(configuration == null){
            return;
        }
        configuration.setTitle(title);
        configuration.setLang(lang);
        configuration.setLib(lib);
        configuration.setArgs(args);
        String fileName = configFile.getText();
        String filePath = configFileMap.get(fileName);
        String directory;
        if(filePath == null){
            directory = configuration.getDirectory();
        }
        else {
            directory = "src/main/configurations/"+fileName.substring(0,fileName.lastIndexOf("."));
            configuration.setDirectory(directory);
            ZipHandler.unzip(new File(filePath),0);
        }
        // if there is a directory with the same name, delete it and create a new one
        File file = new File(directory);
        if(file.exists()){
            file.delete();
        }
        file.mkdir();

        configuration.setCommands(Configuration.makeCommand(lang,lib,args,configuration.getDirectory()));


        JsonFileHandler.createJSONFile(configuration); // ??
        // Creates an entity in configurations table
        HashMap<String,String> info = Executor.executeConfiguration(configuration);

        configuration.setOutput(info.get("output"));

        // TODO : update configuration in database


        editLabel.setText(title);
        closeEditConfiguration();
    }
    public void closeEditConfiguration(){
        editModal.setVisible(false);
        configPageList.setEffect(null);
        configPageList.setDisable(false);
    }
    public void selectEditFileConfiguration(){
        String[] fileDetails = addFile();
        if(fileDetails == null){
            return;
        }
        editConfigFile.setText(fileDetails[0]);
        configFileMap.put(fileDetails[0],fileDetails[1]);
    }



}