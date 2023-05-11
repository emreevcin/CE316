package com.example.iae.ce316;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // database
    Database d = Database.getInstance();
    // variables
    // TODO: Types of the ArrayLists should be converted to corresponding objects
    ArrayList<String> cfgTitles = new ArrayList<>();
    ArrayList<String> projectTitles = new ArrayList<>();
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
    private ListView<String> fileListConfiguration;
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

    private HashMap<String, String> configFileMap = new HashMap<>();
    private HashMap<String, String> subFileMap = new HashMap<>();



    // initializing ui elements
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Database:"+d);

        try {
            cfgTitles.addAll(d.getCfgTitles());
            configBox.getItems().addAll(cfgTitles);
            projectTitles.addAll(d.getProjectTitles());
            projectBoxSubmission.getItems().addAll(projectTitles);
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
        String[] fileDetails = addFile();
        //fileDetails[0] = file name , fileDetails[1] = file path
        if(fileDetails != null){
            fileListConfiguration.getItems().add(fileDetails[0]);
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

    public void submitSubmission() throws IOException {
        String projectTitle = projectBoxSubmission.getValue();
        Project p = findProject(projectTitle);
        if(p == null){
            System.out.println("Project not found");
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
                } else {
                    System.out.println("Error: Failed to load image file: " + imageUrl);
                }
            } else {
                String deniedURL = getClass().getResource("/icons/denied.png").toExternalForm();
                if (deniedURL != null) {
                    Image deniedImage = new Image(deniedURL);
                    s.setStatus("Error");
                    s.setStatusImage(new ImageView(deniedImage));
                } else {
                    System.out.println("Error: Failed to load image file: " + deniedURL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        p.getSubmissions().add(s);
        fileListSubmission.getItems().clear();
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
        if(fileListConfiguration.getItems().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No files selected");
            alert.setContentText("Please select files to submit");
            alert.showAndWait();
            return;
        }
        String fileName = fileListConfiguration.getItems().get(0);
        String filePath = configFileMap.get(fileName);

        ZipHandler.unzip(new File(filePath),0);

        String directory = "src/main/configurations/"+fileName.substring(0,fileName.lastIndexOf("."));

        Configuration configuration = new Configuration(title,langBox.getValue(),commandLib,commandArgs,directory);


        // Creates com/example/iae/ce316/files/<title>.json
        JsonFileHandler.createJSONFile(configuration); // ??
        // Creates an entity in configurations table
        d.addCfg(configuration);
        HashMap<String,String> info = Executor.executeConfiguration(configuration);
        cfgTitles.add(configuration.getTitle());
        configBox.getItems().add(configuration.getTitle());
        Executor.configurations.add(configuration);
        configuration.setOutput(info.get("output"));
    }
    public void submitProject() throws SQLException {
        String title = projectTitle.getText();
        Configuration config = findConfiguration(configBox.getValue());
        Project p = new Project(title,config);
        d.addProject(p);
        projectBoxResults.getItems().add(p.getTitle());
        projectTitles.add(p.getTitle());
        projectBoxSubmission.getItems().add(p.getTitle());
        Executor.projects.add(p);


    }
    private Configuration findConfiguration(String title){
        for(Configuration c : Executor.configurations){
            if(c.getTitle().equals(title)){
                return c;
            }
        }
        return null;
    }
    private Project findProject(String title){
        for(Project p : Executor.projects){
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
    public void openConfigurationListPage(){
        configPageList.setVisible(true);
        configPageAdd.setVisible(false);

        for (int i = 0; i < Executor.configurations.size(); i++) {
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(10, 10, 10, 10));
            Label title = new Label(Executor.configurations.get(i).getTitle());
            title.setPrefWidth(200);
            ImageView delete = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/delete.png")))); // path wrong I will change it : Emre Ö.
            ImageView edit = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/edit.png"))));
            delete.setFitHeight(20);
            delete.setFitWidth(20);
            edit.setFitHeight(20);
            edit.setFitWidth(20);
            hBox.getChildren().addAll(title, delete, edit);
        }


    }
    public void openConfigurationAddPage(){
        configPageList.setVisible(false);
        configPageAdd.setVisible(true);
    }

}