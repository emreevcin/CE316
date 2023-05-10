package com.example.iae.ce316;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Executor {
    public static ArrayList<Configuration> configurations = new ArrayList<>();
    public static ArrayList<Project> projects = new ArrayList<>();

    private Executor() {}

    public static HashMap<String,String> executeSubmission(Submission submission) {
        // TODO: set configuration directory
        // TODO: change pb.directory() to configuration.getDirectory()
        // TODO: Emre ÖZOĞUL - I set path as "C:\\Users\\emred\\OneDrive\\Masaüstü" for testing purposes , you can change it to your wish.
        // Emre Evcin - I also have changed directory to my absolute path (Be careful!)


        HashMap<String,String> executionInfo = new HashMap<>();
        String exitCode = "";

        Configuration configuration = submission.getProject().getConfiguration();
        String lang = configuration.getLang();
        String[] commands = submission.getCommands();
        String directory = submission.getDirectory();

        StringBuilder stringBuilder = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File("C:\\Users\\EMRE\\Desktop\\Projects\\Hello World"));

        if(lang.equals("C")){
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                exitCode = String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (lang.equals("C++")) {
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                exitCode = String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else if (lang.equals("Java")) {
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String command2 = commands[1];
            pb.command("cmd.exe", "/c", command2);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                exitCode= String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (lang.equals("Python")) {
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                exitCode = String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String output = stringBuilder.toString();

        executionInfo.put("output",output);
        executionInfo.put("exitCode",exitCode);

        return executionInfo;
    }
    public static HashMap<String,String> executeConfiguration(Configuration configuration) {
        // TODO: set configuration directory
        // TODO: change pb.directory() to configuration.getDirectory()


        HashMap<String,String> executionInfo = new HashMap<>();
        String exitCode = "";

        String lang = configuration.getLang();
        String[] commands = configuration.getCommands();
        String directory = configuration.getDirectory();

        StringBuilder stringBuilder = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File("C:\\Users\\EMRE\\Desktop\\Projects\\Hello World"));

        if(lang.equals("C")){
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                exitCode = String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (lang.equals("C++")) {
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                exitCode = String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else if (lang.equals("Java")) {
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String command2 = commands[1];
            pb.command("cmd.exe", "/c", command2);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }
                exitCode= String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (lang.equals("Python")) {
            String command = commands[0];
            pb.command("cmd.exe", "/c", command);
            try {
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read the output of the process
                String line ;
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                exitCode = String.valueOf(p.waitFor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String output = stringBuilder.toString();

        executionInfo.put("output",output);
        executionInfo.put("exitCode",exitCode);

        return executionInfo;
    }

    public static void executeAll() {
        // TODO: implement this method

    }

    public static boolean compare(Submission submission){
        Configuration configuration = submission.getProject().getConfiguration();
        String configOutput = configuration.getOutput();
        if(configOutput.equals(submission.getOutput())){
            return true;
        }
        return false;
    }










}
