package com.example.iae.ce316;

import java.io.File;
import java.util.ArrayList;

public class Configuration {
    private String title ;
    private String directory;
    private String output;
    private String[] commands ;
    private String args ;
    private String lang;
    private String lib;

    public Configuration(String title, String lang, String lib, String args,String directory) {
        this.title = title;
        this.args = args;
        this.lang = lang;
        this.lib = lib;
        this.directory = directory;
        this.commands = makeCommand(this.lang,this.lib,this.args, this.directory);
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
            command1 ="gcc "+filesString +" " +lib +" " +args;
            commands[0] = command1;
            commands[1] = command2;
        } else if (lang.equals("C++")) {
            command1 ="g++ "+filesString +" " +lib +" " +args;
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
