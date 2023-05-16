package com.example.iae.ce316;

import java.io.File;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Configuration {
    private String title ;
    private String directory;
    private String output;
    private String[] commands ;
    private String args ;
    private String lang;
    private String lib;

    @JsonCreator
    public Configuration(@JsonProperty("title") String title,
                         @JsonProperty("lang") String lang,
                         @JsonProperty("lib") String lib,
                         @JsonProperty("args") String args,
                         @JsonProperty("directory") String directory) {
        this.title = title;
        this.args = args;
        this.lang = lang;
        this.lib = lib;
        this.directory = directory;
        this.commands = makeCommand(this.lang, this.lib, this.args, this.directory);
    }



    public String getTitle() {
        return title;
    }

    public String getDirectory() {
        return directory;
    }

    public String[] getCommands() {
        return commands;
    }

    public String getArgs() {
        return args;
    }

    public String getLang() {
        return lang;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getLib() {
        return lib;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public static String[] makeCommand(String lang, String lib, String args, String directory) {
        String command1 = "";
        String command2 = "";
        String[] commands = new String[2];
        StringBuilder sb = new StringBuilder();
        File dir = new File(directory);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(child.getName().endsWith(".c")){
                    sb.append(child.getName());
                    sb.append(" ");
                }
                else if(child.getName().endsWith(".java")){
                    sb.append(child.getName());
                    sb.append(" ");
                }
                else if(child.getName().endsWith(".cpp")){
                    sb.append(child.getName());
                    sb.append(" ");
                }
                else if(child.getName().endsWith(".py")){
                    sb.append(child.getName());
                    sb.append(" ");
                }
            }
        }
        String filesString = sb.toString();

        if(lang.equals("C")){
            // for every file in the directory with a suffix of .c, add it to the command
            String[] parts = lib.split("\\s+");
            String filename = parts[1];
            command1 ="gcc " + filesString + " " + lib;
            command2 = filename + " " + args;
            commands[0] = command1;
            commands[1] = command2;
        } else if (lang.equals("C++")) {

            String[] parts = lib.split("\\s+");
            String outputFileName = "";

            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].equals("-o")) {
                    outputFileName = parts[i + 1];
                    break;
                }
            }
            command1 ="g++ "+ lib + " " + filesString;
            command2 = outputFileName + " " + args;
            commands[0] = command1;
            commands[1] = command2;
        } else if (lang.equals("Python")) {
            command1 ="python "+filesString +" "+args;
            commands[0] = command1;
            commands[1] = command2;
        } else if (lang.equals("Java")) {
            command1= "javac "+filesString +" "+args;
            command2 = "java "+filesString +" "+args;
            commands[0] = command1;
            commands[1] = command2;
        }
        else{
            commands[0] = "error";
            commands[1] = "error";
        }
        return commands;
    }
}
