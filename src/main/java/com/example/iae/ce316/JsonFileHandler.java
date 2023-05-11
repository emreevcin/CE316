package com.example.iae.ce316;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
    public static <T> T readJsonFile(File file, Class<T> clazz) throws FileNotFoundException, JsonIOException, JsonSyntaxException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new JsonIOException("Error reading JSON file: " + file.getAbsolutePath(), e);
        }
    }

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

    // TODO: First the program terminates then corresponding json file is added into directory. Handle it.
    public static void createJSONFile(Configuration configuration) {
        try {
            String currentDir = System.getProperty("user.dir");
            String filePath = currentDir + "\\" +configuration.getDirectory()+"\\"+ configuration.getTitle() + ".json";
            System.out.println(filePath);
            writeJsonFile(new File(filePath), configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {

         /*** Example usage for reading the JSON file into a Java object

         Configuration read = readJsonFile(new File("src\\main\\java\\com\\example\\iae\\ce316\\files\\config.json"), Configuration.class);
         System.out.println(read.getFiles());


         ***/


        String directory = "src\\main\\java\\com\\example\\iae\\ce316\\files\\config.json";
        String title = "Config";
        String lang = "C";
        String output = "Hello World";
        String lib = "";
        String Args = "";
        Configuration configuration = new Configuration(title,lang,lib,Args,directory);
        configuration.setOutput(output);
        configuration.setDirectory(directory);
        System.out.println(configuration);

        // Write the Java object to a JSON file
        writeJsonFile(new File("src\\main\\java\\com\\example\\iae\\ce316\\files\\config.json"), configuration);

    }

}