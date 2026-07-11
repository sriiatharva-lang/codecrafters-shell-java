import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("$ ");
            String input = scanner.nextLine();
            System.out.println(input + " is not a valid command");
        }
    }
}
