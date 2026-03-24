// importing scanner for for higher level reading methods (as System.in only provides raw bytes)
// buffering input, detecting newlines, splitting input into tokens (similar to scanf in C)
import java.util.Scanner;
import java.util.ArrayList;

public class RegexEngine {

    static ArrayList<Token> tokenise (String regex){ // takes string and returns ArrayList (similar to vector)
        
        ArrayList<Token> tokens = new ArrayList<Token>(); // create an empty ArrayList 

        for (int i = 0; i < regex.length(); i++){
            char c = regex.charAt(i);

            if (Character.isLetterOrDigit(c) || c == ' '){
                tokens.add(new Token(Token.Type.LITERAL, c));
                continue;
            }

            switch(c){
                case '*': tokens.add(new Token(Token.Type.STAR));
                break;
                case '+': tokens.add(new Token(Token.Type.PLUS));
                break;
                case '|': tokens.add(new Token(Token.Type.ALTERNATION));
                break;
                case '(': tokens.add(new Token(Token.Type.LEFTBRACKET));
                break;                
                case ')': tokens.add(new Token(Token.Type.RIGHTBRACKET));
                break;
                default:
                    System.err.println("Error: invalid character '" + c + " ' in regular expression");
                    System.exit(1);
            }
        }
        return tokens;
    }


    public static void main(String[] args){

        // check for verbose flag in running
        boolean verbose = false;

        for (String arg : args) {
            if (arg.equals("-v")) {
                verbose = true; 
            }
        }


        Scanner scanner = new Scanner(System.in); // Scanner wrapping System.in

        String regex = scanner.nextLine(); // reads regex from first line of input and returns it as a string (similar to fgets)
        
        ArrayList<Token> tokens = tokenise(regex);
        Parser parser = new Parser(tokens);
        NFA nfa = parser.parse();

        Evaluator evaluator = new Evaluator(nfa);

        if (verbose) {
            VerboseEvaluator verboseEvaluator = new VerboseEvaluator(nfa);
            verboseEvaluator.printTable();
            System.out.println("ready"); // print that the scanner is ready

            // read and evaluate input strings for any subsequent inputs 
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                System.out.println(evaluator.evaluate(input)); // true or false 
            }
            
        } else {
            System.out.println("ready"); // print that the scanner is ready

            // read and evaluate input strings for any subsequent inputs 
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                System.out.println(evaluator.evaluate(input)); // true or false 
            }  
        }

    }
    
}