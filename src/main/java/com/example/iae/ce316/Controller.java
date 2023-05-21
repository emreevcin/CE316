package com.example.iae.ce316;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;


public class Controller implements Initializable {
    Database d = Database.getInstance();
    DirectoryHandler directoryHandler= DirectoryHandler.getInstance();
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
    @FXML
    private VBox helpModal;
    @FXML
    private StackPane helpStack;
    @FXML
    private ImageView backButtonHelp;
    @FXML
    private ImageView nextButtonHelp;


    private int helpStackIndex = 0;
    private Configuration editConfig;
    private Label editLabel;
    private HashMap<String, String> configFileMap = new HashMap<>(); // can be changeable due to it will hold just one path of one file -> String configAbsPath
    private HashMap<String, String> subFileMap = new HashMap<>();// can be changeable due to it will hold just one path of one file -> String subAbsPath

    public Controller() throws IOException {
    }


    // initializing ui elements
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Database:"+d);
        System.out.println("Directory handler:"+directoryHandler);

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
                projectBoxResults.getItems().add(p.getTitle());
                if(p.getConfiguration()!= null){
                    projectBoxSubmission.getItems().add(p.getTitle());
                }
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
        projectBoxResults.setValue("Select a project");
    }
    // methods

    public void switchToLandingPage() {
        landingPage.setVisible(true);
        submissionPage.setVisible(false);
        resultsPage.setVisible(false);
        configurationPage.setVisible(false);
        projectPage.setVisible(false);
    }

    public void switchToSubmissionPage() throws SQLException {
        projectBoxSubmission.getItems().clear();
        for (Project p : projectList) {
            Configuration c = p.getConfiguration();
            int ID = d.getConfigurationID(c);
            if(ID != 0){
                projectBoxSubmission.getItems().add(p.getTitle());
            }
        }
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
    public void addFileSubmission() {
        HashMap<String,String> fileMap = addFiles();
        if(fileMap != null){
            // add file names to list view using substring
            for (String key : fileMap.keySet()) {
                fileListSubmission.getItems().add(key.substring(key.lastIndexOf("\\")+1));
                subFileMap.put(key,fileMap.get(key));
            }
        }
    }
    public void addFileConfiguration()  {
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
    private HashMap<String,String> addFiles(){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Upload Files");
        File file = fileChooser.showDialog(null);
        if(file == null){
            return null;
        }
        // check every file inside of this file is .zip  , then add these files to map
        HashMap<String,String> fileMap = new HashMap<>();
        File[] files = file.listFiles();
        for(File f : files){
            String suffix = f.getName().substring(f.getName().lastIndexOf("."));
            if(!suffix.equals(".zip")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File type not supported");
                alert.setContentText("Please select a .zip file");
                alert.showAndWait();
                return null;
            }
            fileMap.put(f.getName(),f.getAbsolutePath());
        }
        return fileMap;
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private Image loadImage(String imageUrl) {
        String imageURL = getClass().getResource(imageUrl).toExternalForm();

        if (imageURL != null) {
            return new Image(imageURL);
        }

        return null;
    }


    public void submitSubmission() throws IOException, SQLException {
        String projectTitle = projectBoxSubmission.getValue();
        Project p = findProject(projectTitle);

        if (p == null) {
            return;
        }

        if (fileListSubmission.getItems().isEmpty()) {
            showAlert("Error", "No files selected", "Please select files to submit");
            return;
        }

        for (int i = 0; i <fileListSubmission.getItems().size() ; i++) {
            String fileName = fileListSubmission.getItems().get(i);
            String filePath = subFileMap.get(fileName);
            ZipHandler.unzip(new File(filePath), 1,p.getTitle()+File.separator+fileName.substring(0,fileName.lastIndexOf(".")));

            String directory = "projects"+File.separator+p.getTitle()+File.separator+ fileName.substring(0, fileName.lastIndexOf("."));

            Submission s = new Submission(p, directory);
            s.setCommands();
            HashMap<String, String> info = Executor.executeSubmission(s);

            if (info.get("output") != null) {
                s.setOutput(info.get("output"));
            }

            boolean isOk = Executor.compare(s);

            try {
                String imageUrl;
                String status;
                String error;

                if (isOk) {
                    imageUrl = "/icons/ok.png";
                    status = "OK";
                    error = "No Error";
                } else {
                    imageUrl = "/icons/denied.png";
                    status = "Error";
                    if (!s.getOutput().equals("")) {
                        error = "No Output Match";
                    } else {
                        error = "Compiling/Interpreting Error";
                    }
                }

                Image statusImage = loadImage(imageUrl);

                s.setStatus(status);
                s.setStatusImage(new ImageView(statusImage));
                s.setError(error);
            } catch (Exception e) {
                e.printStackTrace();
            }

            d.addSubmission(s);
            submissionList.add(s);
            p.getSubmissions().add(s);

            if (projectBoxResults.getValue() != null && projectBoxResults.getValue().equals(projectTitle)) {
                resultTable.getItems().add(s);
            }
        }

        fileListSubmission.getItems().clear();
        subFileMap.clear();
        projectBoxSubmission.setValue("Select a project");
    }

    public void submitConfiguration() throws SQLException, IOException {
        String title = configTitle.getText();
        String commandLib = configCommandLib.getText();
        String commandArgs = configCommandArgs.getText();
        if(title.equals("")){
            showAlert("Error","No title","Please enter a title");
            return;
        }
        if(langBox.getValue().equals("Select a language")){
            showAlert("Error","No language selected","Please select a language");
            return;
        }
        if(configFile.getText().equals("")){
            showAlert("Error","No configuration file selected","Please select a configuration file");
            return;
        }
        String fileName = configFile.getText();
        String filePath = configFileMap.get(fileName);

        ZipHandler.unzip(new File(filePath),0,title);

        String directory = "configurations"+ File.separator +title;


        Configuration configuration = new Configuration(title,langBox.getValue(),commandLib,commandArgs,directory);


        HashMap<String,String> info = Executor.executeConfiguration(configuration);
        configurationList.add(configuration);
        configBox.getItems().add(configuration.getTitle());

        configuration.setOutput(info.get("output"));

        d.addConfiguration(configuration);

        JsonFileHandler.createJSONFile(configuration);

        configTitle.clear();
        configCommandLib.clear();
        configCommandArgs.clear();
        configFile.clear();
        langBox.setValue("Select a language");


    }
    public void submitProject() throws SQLException {
        String title = projectTitle.getText();
        String configName = configBox.getValue();
        if(title.equals("")){
            showAlert("Error","No title","Please enter a project title");
            return;
        }
        if(configName.equals("Select a configuration")){
            showAlert("Error","No configuration selection","Please select a configuration");
            return;
        }
        Configuration config = findConfiguration(configBox.getValue());
        Project p = new Project(title,config, config.getOutput());
        p.setConfigOutput(config.getOutput());
        d.addProject(p);
        projectBoxResults.getItems().add(p.getTitle());
        projectList.add(p);
        projectBoxSubmission.getItems().add(p.getTitle());

        projectTitle.clear();
        configBox.setValue("Select a configuration");

        File file = new File("projects"+File.separator);
        File[] files = file.listFiles();
        for(File f : files){
            if(f.getName().equals(title)){
                showAlert("Error","Project already exists","Please enter a different title");
                return;
            }
        }
        file = new File("projects"+File.separator+title);
        file.mkdir();

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
        if(projectTitle.equals("Select a project")){
            return ;
        }
        Project p = findProject(projectTitle);
        resultTable.getItems().clear();
        resultTable.getItems().addAll(p.getSubmissions());
        configOutput.setText(p.getConfiguration().getOutput());
        submissionOutput.clear();
    }
    public void configPageButtonHandler() {
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
                ImageView export = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/export.png"))));
                delete.setFitHeight(20);
                delete.setFitWidth(20);
                edit.setFitHeight(20);
                edit.setFitWidth(20);
                export.setFitHeight(20);
                export.setFitWidth(20);
                Button deleteButton = new Button();
                Button editButton = new Button();
                Button exportButton = new Button();
                deleteButton.setGraphic(delete);
                editButton.setGraphic(edit);
                exportButton.setGraphic(export);
                deleteButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; ");
                editButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; ");
                exportButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; ");
                // add hover effect
                deleteButton.setOnMouseEntered(e -> {
                    deleteButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3E54AC; ");
                });
                deleteButton.setOnMouseExited(e -> {
                    deleteButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; ");
                });
                editButton.setOnMouseEntered(e -> {
                    editButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3E54AC; ");
                });
                editButton.setOnMouseExited(e -> {
                    editButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; ");
                });
                exportButton.setOnMouseEntered(e -> {
                    exportButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #3E54AC; ");
                });
                exportButton.setOnMouseExited(e -> {
                    exportButton.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFFFFF; ");
                });

                buttons.getChildren().addAll(deleteButton, editButton,exportButton);
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

                    try {
                        d.removeConfiguration(c);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                });
                editButton.setOnAction(e -> {
                    Configuration config = findConfiguration(title.getText());
                    if(config == null){
                        return;
                    }
                    editConfig = config;
                    editLabel = title;
                    openEditConfiguration(config);
                });
                exportButton.setOnAction(e -> {
                    Configuration config = findConfiguration(title.getText());
                    if(config == null){
                        return;
                    }

                    JsonFileHandler.exportConfigurationToJson(config);
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

    public void importConfiguration() throws SQLException {
        Configuration config = JsonFileHandler.importConfigurationFromJson();
        if (config == null) {
            return;
        }
        d.addConfiguration(config);
        configurationList.add(config);
        configBox.getItems().add(config.getTitle());
    }

    public void openEditConfiguration(Configuration config){
        editConfigLang.getItems().clear();
        configFileMap.clear();
        editModal.setVisible(true);
        configPageList.setDisable(true);
        configPageList.setEffect(new BoxBlur(3,3,3));
        editConfigTitle.setText(config.getTitle());
        editConfigLang.setValue(config.getLang());
        editConfigLang.getItems().addAll("C", "C++", "Java", "Python");
        editConfigLib.setText(config.getLib());
        editConfigArgs.setText(config.getArgs());
    }
    public void submitConfigurationEdit() throws IOException, SQLException {

        String title = editConfigTitle.getText();
        String lang = editConfigLang.getValue();
        String lib = editConfigLib.getText();
        String args = editConfigArgs.getText();
        Configuration configuration = editConfig;
        if(configuration == null){
            return;
        }
        String oldTitle = configuration.getTitle();
        int configurationID = d.getConfigurationID(configuration);

        String fileName = editConfigFile.getText();
        String filePath = configFileMap.get(fileName);
        String directory;

        boolean isTitleChanged = !oldTitle.equals(title);

        File dir = new File("configurations"+File.separator);
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for(File child : directoryListing){

                if(isTitleChanged && child.getName().equals(oldTitle)){
                    child.renameTo(new File("configurations"+File.separator+title));
                    directory = "configurations"+File.separator+title;
                    configuration.setDirectory(directory);
                    break;
                }

                if(child.getName().equals(title)){
                       showAlert("Error","Configuration with the same name already exists!","Please choose a different name.");
                       configFileMap.remove(fileName);
                       return;
                }

            }
        }

        if(filePath != null){
            ZipHandler.unzip(new File(filePath),0,title);
        }

        configuration.setTitle(title);
        configuration.setLang(lang);
        configuration.setLib(lib);
        configuration.setArgs(args);



        configuration.setCommands(Configuration.makeCommand(lang,lib,args,configuration.getDirectory()));



        HashMap<String,String> info = Executor.executeConfiguration(configuration);

        configuration.setOutput(info.get("output"));

        JsonFileHandler.createJSONFile(configuration);

        d.updateConfiguration(configuration, configurationID);
        editLabel.setText(title);
        closeEditConfiguration();

        resultTable.refresh();
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
    public void openHelpModal(){
        helpModal.setVisible(true);
        backButtonHelp.setVisible(false);
        configPageList.setEffect(new BoxBlur(3,3,3));
        configPageList.setDisable(true);
        configPageAdd.setEffect(new BoxBlur(3,3,3));
        configPageAdd.setDisable(true);
        editModal.setEffect(new BoxBlur(3,3,3));
        editModal.setDisable(true);
        landingPage.setEffect(new BoxBlur(3,3,3));
        landingPage.setDisable(true);
        configurationPage.setEffect(new BoxBlur(3,3,3));
        configurationPage.setDisable(true);
        resultsPage.setEffect(new BoxBlur(3,3,3));
        resultsPage.setDisable(true);
        submissionPage.setEffect(new BoxBlur(3,3,3));
        submissionPage.setDisable(true);
        projectPage.setEffect(new BoxBlur(3,3,3));
        projectPage.setDisable(true);
    }
    public void closeHelpModal(){
        helpModal.setVisible(false);
        configPageList.setEffect(null);
        configPageList.setDisable(false);
        configPageAdd.setEffect(null);
        configPageAdd.setDisable(false);
        editModal.setEffect(null);
        editModal.setDisable(false);
        landingPage.setEffect(null);
        landingPage.setDisable(false);
        configurationPage.setEffect(null);
        configurationPage.setDisable(false);
        resultsPage.setEffect(null);
        resultsPage.setDisable(false);
        submissionPage.setEffect(null);
        submissionPage.setDisable(false);
        projectPage.setEffect(null);
        projectPage.setDisable(false);
    }
    public void navigateForwardHelp(){
        helpStack.getChildren().get(helpStackIndex).setVisible(false);
        helpStack.getChildren().get(helpStackIndex+1).setVisible(true);
        helpStackIndex++;
        if(helpStackIndex == helpStack.getChildren().size()-1){
            nextButtonHelp.setVisible(false);
        }
        backButtonHelp.setVisible(true);

    }
    public void navigateBackwardHelp(){
        helpStack.getChildren().get(helpStackIndex).setVisible(false);
        helpStack.getChildren().get(helpStackIndex-1).setVisible(true);
        helpStackIndex--;
        if(helpStackIndex == 0){
            backButtonHelp.setVisible(false);
        }
        nextButtonHelp.setVisible(true);


    }


}