import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static List<String> parseInput(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideSingleQuotes = false;
        boolean insideDoubleQuotes = false;

        for (int i =0; i < input.length(); i++) {
            char c = input.charAt(i);
             
            if(c == '\\' && !insideSingleQuotes && !insideDoubleQuotes){
                if (i + 1 < input.length()) {
                    current.append(input.charAt(i + 1));
                    i++;
                }
            } else if(c == '\\' && insideDoubleQuotes){
                if(i + 1 < input.length()){
                    char next = input.charAt(i + 1);
                    if(next == '\'' || next == '"' || next == '\\' || next == ' ') {
                        current.append(next);
                        i++;
                    } else {
                        current.append(c);
                    }
            }
        } else if (c == '\'' && !insideDoubleQuotes) {
            insideSingleQuotes = !insideSingleQuotes;
            } else if (c == '"' && !insideSingleQuotes) {
                insideDoubleQuotes = !insideDoubleQuotes;
            } else if (c == ' ' && !insideSingleQuotes && !insideDoubleQuotes) {
                if (current.length() > 0) {
                    parts.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString());
        }
        return parts; 
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String[] builtins = {"exit", "echo", "type", "pwd", "cd"};
        String currentDir = System.getProperty("user.dir");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                break;
            } else if (input.startsWith("echo")) {
                List<String> parts = parseInput(input);
                StringBuilder result = new StringBuilder();
                for (int i = 1; i < parts.size(); i++) {
                    if (i > 1) result.append(" ");
                    result.append(parts.get(i));
                }
                System.out.println(result.toString());
            } else if (input.equals("pwd")) {
                System.out.println(currentDir);
            } else if (input.startsWith("cd ")) {
                String targetPath = input.substring(3).trim();

                if (targetPath.equals("~")) {
                    String home = System.getenv("HOME");
                    if (home != null) {
                        targetPath = home;
                    }
                }
                File dir = new File(targetPath);
                if (!dir.isAbsolute()) {
                    dir = new File(currentDir, targetPath);
                }

                if (dir.exists() && dir.isDirectory()) {
                    currentDir = dir.getCanonicalPath();
                } else {
                    System.out.println("cd: " + targetPath + ": No such file or directory");
                }
            } else if (input.startsWith("type ")) {
                String word = input.substring(4).trim();

                if (Arrays.asList(builtins).contains(word)) {
                    System.out.println(word + " is a shell builtin");
                } else {
                    String pathEnv = System.getenv("PATH");
                    String[] dirs = pathEnv.split(":");
                    boolean found = false;

                    for (String dir : dirs) {
                        File file = new File(dir, word);
                        if (file.exists() && file.canExecute()) {
                            System.out.println(word + " is " + file.getPath());
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println(word + ": not found");
                    }
                }
            } else {
               List <String> tokens = parseInput(input);
               String outputFile = null;
               
               for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).equals(">") || tokens.get(i).equals("1>")) {
                    outputFile = tokens.get(i +1);
                    tokens = tokens.subList(0, i);
                    break;
               }
            }

            String[] parts = tokens.toArray(new String[0]);
            String command = parts[0];

            String pathEnv = System.getenv("PATH");
            String[] dirs = pathEnv.split(":");
            boolean found = false;

            for(String dir : dirs){
                File file = new File(dir, command);
                if (file.exists() && file.canExecute()){
                    found = true;
                    break;
                }
            }
            if(found){
                try {
                    ProcessBuilder pb = new ProcessBuilder(parts);
                    if(outputFile != null){
                        pb.redirectOutput(new File(outputFile));
                        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
                    } else {
                        pb.inheritIO();
                    }
                    Process process = pb.start();
                    process.waitFor();
                    
                } catch (IOException e) {
                    System.out.println(command + ": error running program");
                }

            } else {
                System.out.println(command + ": command not found");
            }

            }
        }
    }
}