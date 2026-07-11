import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        // Stage 1: Print a prompt
        System.out.print("$ ");

        // Stage 2: Handle invalid commands
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        System.out.println(input + ": command not found");
    }
}