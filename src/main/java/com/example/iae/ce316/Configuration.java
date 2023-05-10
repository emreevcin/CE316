package com.example.iae.ce316;

import java.util.ArrayList;

public class Configuration {
    private final String title ;
    private String directory;
    private String output;
    private final String[] commands ;
    private final String args ;
    private final ArrayList<String> files ;
    private final String lang;
    private final String lib;

    public Configuration(String title, String lang, ArrayList<String> files, String lib, String args) {
        this.title = title;
        this.files = files;
        this.args = args;
        this.lang = lang;
        this.lib = lib;
        directory = "src\\main\\java\\com\\example\\iae\\ce316\\files\\" + title + ".json";
        this.commands = makeCommand(this.lang,this.files,this.lib,this.args);
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

    public ArrayList<String> getFiles() {
        return files;
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

    public static String[] makeCommand(String lang, ArrayList<String> files , String lib, String args) {
        String command1 = "";
        String command2 = "";
        String[] commands = new String[2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <files.size() ; i++) {
            sb.append(files.get(i));
            sb.append(" ");
        }
        String filesString = sb.toString();
        if(lang.equals("C")){
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
        System.out.println("Command1: "+command1);
        System.out.println("Command2: "+command2);
        return commands;
    }
}
