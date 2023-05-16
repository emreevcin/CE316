package com.example.iae.ce316;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.stage.FileChooser;


public class JsonFileHandler {

    // Gson instance with pretty printing enabled
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Reads a JSON file and deserializes its contents to a Java object of the specified class.
     * @param file the JSON file to read
     * @param clazz the class of the Java object to deserialize to
     * @return the deserialized Java object
     * @throws FileNotFoundException if the file does not exist or is not readable
     * @throws JsonIOException if there is an error reading the JSON file
     * @throws JsonSyntaxException if the JSON file has invalid syntax
     */

    /**
     * Serializes a Java object to a JSON string and writes it to a file.
     * @param file the file to write the JSON to
     * @param object the Java object to serialize
     * @throws IOException if there is an error writing to the file
     */
    private static void writeJsonFile(File file, Object object) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(object, writer);
        }
    }

    public static void createJSONFile(Configuration configuration) {
        try {
            String currentDir = System.getProperty("user.dir");
            String directoryPath = currentDir + File.separator + configuration.getDirectory();
            System.out.println(directoryPath);

            // Delete existing .json files in the directory
            File[] files = new File(directoryPath).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        file.delete();
                    }
                }
            }

            // Create new .json file
            String filePath = directoryPath + File.separator + configuration.getTitle() + ".json";
            writeJsonFile(new File(filePath), configuration);
            System.out.println("JSON file created successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration importConfigurationFromJson() {
        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration JSON File");

        // Set extension filter if needed (optional)
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(jsonFilter);

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Read the JSON file and map it to the Configuration object
                Configuration configuration = objectMapper.readValue(selectedFile, Configuration.class);

                System.out.println("Configuration imported from JSON successfully!");
                return configuration;
            } catch (IOException e) {
                System.out.println("Error importing configuration from JSON: " + e.getMessage());
            }
        } else {
            System.out.println("Import canceled by the user.");
        }

        return null;
    }

    public static void exportConfigurationToJson(Configuration configuration) {
        // Create an instance of ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Enable pretty printing of JSON
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Configuration JSON File");
        fileChooser.setInitialFileName(configuration.getTitle());

        // Set extension filter if needed (optional)
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(jsonFilter);

        // Show the file chooser dialog
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            try {
                // Write the configuration object to the selected file as JSON
                objectMapper.writeValue(selectedFile, configuration);

                System.out.println("Configuration exported to JSON successfully!");
            } catch (IOException e) {
                System.out.println("Error exporting configuration to JSON: " + e.getMessage());
            }
        } else {
            System.out.println("Configuration export canceled by the user.");
        }
    }

}