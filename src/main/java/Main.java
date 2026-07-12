import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String[] builtins = {"exit", "echo", "type","pwd"};
        String currentDir = System.getProperty("user.dir");

        while(true){
            System.out.print("$ ");
            String input = scanner.nextLine();
            if(input.equals("exit")){
                break;
            } else if(input.startsWith("echo")){
                System.out.println(input.substring(5));
            } else if (input.equals("pwd")){
                System.out.println(currentDir);
            } else if (input.startsWith("type ")){
                String word = input.substring(4).trim();
                if (Arrays.asList(builtins).contains(word)) {
                    System.out.println(word + " is a shell builtin");
                } else {
                    //new 
                String pathEnv = System.getenv("PATH");
                String[] dirs = pathEnv.split(":");
                boolean found = false;
                for (String dir : dirs){
                    File file = new File ( dir , word);
                    if (file.exists() && file.canExecute()){
                        System.out.println(word + " is " + file.getPath());
                        found = true;
                        break;
                    }
                }
            if (!found){
                System.out.println(word + ": not found");
            }
        }
    } else {
        //new : running real one
        String[] parts = input.split(" ");
        String command = parts[0];
        String pathEnv = System.getenv("PATH");
        String[] dirs = pathEnv.split(":");
        boolean found = false;

        for (String dir : dirs){
            File file = new File(dir, command);
            if (file.exists() && file.canExecute()){
                found = true;
                break;
            }
        }
        if (found){
            try{
            ProcessBuilder pb = new ProcessBuilder(parts);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            } catch (IOException e){
                System.out.println(command + ": error running program");
        }
        } else {
        System.out.println(input + ": command not found");
    }
}
    }
}
}