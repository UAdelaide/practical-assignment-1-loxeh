// importing scanner for for higher level reading methods (as System.in only provides raw bytes)
// buffering input, detecting newlines, splitting input into tokens (similar to scanf in C)
import java.util.Scanner;

public class RegexEngine {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in); // Scanner wrapping System.in

        String regex = scanner.nextLine(); // reads regex from first line of input and returns it as a string (similar to fgets)

        System.out.println("ready"); // print that the scanner is ready

        // read and evaluate input strings for any subsequent inputs 
        while (scanner.hasNextLine()){
            String input = scanner.nextLine();
            System.out.println(input); 
        }
    }
    
}