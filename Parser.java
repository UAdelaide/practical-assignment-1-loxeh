import java.util.ArrayList;


// the parser needs to follow order of precedence 

// 1. Brackets and Literals 
// 2. Klenee Star and Klenee Plus
// 3. Concatenation
// 4. Alternation

// start at the lowest order of precedence and then call the next highest function from each


public class Parser {
    
    private ArrayList<Token> tokens; // tokens themselves
    private int pos; // position of tokens

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    // look at tokens without consuming it
    private Token currentToken() {

        if (pos < tokens.size()) {
            return tokens.get(pos);// return token if not at the end of list 
        }

        return null; // return null if at end of list
            
    }


    private Token consumeCurrentToken() {
        return tokens.get(pos++); // return current token and advance by one 
    }


    // start the descent parse
    public NFA parse() {

        // check if empty regex
        if (tokens.isEmpty()){
            throw new RuntimeException("Error: empty regular expression");
        }

        NFA result = parseAlternation(); // start with lowest precedence operator 

        // check that everything was consumed 
        if (pos < tokens.size()) {
            throw new RuntimeException("Error: unexpected trailing character at position " + pos);
        }

        return result; // return if checks pass
    }


    private NFA parseAlternation() {
        NFA left = parseConcatenation(); // immediately defer to next highest operator 

        while (currentToken() != null && currentToken().type == Token.Type.ALTERNATION) {
            consumeCurrentToken(); // consume | 

            // ends with | or is followed by ) throw error
            if (currentToken() == null || currentToken().type == Token.Type.RIGHTBRACKET) {
                throw new RuntimeException("Error: empty alternation side");
            }
            NFA right = parseConcatenation(); // get the right side from passConcatenation
            left = NFA.alternate(left, right);
        }

        return left;
    }


    public NFA parseConcatenation() {
        NFA left = parseStarAndPlus(); // next highest

        // loop while current token isnt null, | or )
        while (currentToken() != null && currentToken().type != Token.Type.ALTERNATION && currentToken().type != Token.Type.RIGHTBRACKET){
            NFA right = parseStarAndPlus(); // pass in the right side
            left = NFA.concatenate(left, right); // combine left side and right side 
        }

        return left; // return all cocatenated 
    }


    public NFA parseStarAndPlus() {
        NFA bracketsAndLiterals = parseBracketsAndLiterals(); /// next highest 

        if (currentToken() != null && currentToken().type == Token.Type.STAR) {

            consumeCurrentToken();
            return NFA.kleeneStar(bracketsAndLiterals); // create klenee star fragment 

        }else if (currentToken() != null && currentToken().type == Token.Type.PLUS){

            consumeCurrentToken();
            return NFA.kleenePlus(bracketsAndLiterals); // create klenee plus fragment 

        }

        return bracketsAndLiterals; // return all 
    }


    public NFA parseBracketsAndLiterals() {
        Token token = currentToken(); // lowest 

        if (token.type == Token.Type.LEFTBRACKET) {            
            consumeCurrentToken(); // consume (

            NFA insideBrackets = parseAlternation(); // parse everything inside and start process again

            // ensure that closing bracket exists 
            if (currentToken() == null || currentToken().type != Token.Type.RIGHTBRACKET) {
                throw new RuntimeException("Error: expected ')'");
            }
        
            consumeCurrentToken(); // now consume ) (as everything inside has been handled)
            return insideBrackets;
            
        // strictly check that token type is literal 
        }else if (token.type == Token.Type.LITERAL){

            consumeCurrentToken(); // must be a literal token 
            return NFA.literal(token.value); // take characeter at current token and build transition with it 

        }else {
            // now catch any unexpected tokens 
            throw new RuntimeException("Error: unexpected token '" + token.type + "'");
        }
    }


}
