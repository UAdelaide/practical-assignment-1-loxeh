import org.junit.Test; // JUnit test methods 
import static org.junit.Assert.assertEquals; // Assert equals for comparisons

import java.util.ArrayList; 

public class TestingTokens_Test {
    

    @Test // Tells JUnit this is a test method
    public void testBasicLiterals() {
        ArrayList<Token> tokens = RegexEngine.tokenise("abc"); // tokenise basic string
        assertEquals(3, tokens.size()); // compare size
        assertEquals(Token.Type.LITERAL, tokens.get(0).type); // first token should be a LITERAL
        assertEquals('a', tokens.get(0).value); // compare first token
        assertEquals('b', tokens.get(1).value); // compare first token
        assertEquals('c', tokens.get(2).value); // compare first token
    };

    @Test
    public void testSpecialCharacters(){
        ArrayList<Token> tokens = RegexEngine.tokenise("a*b+c|d"); // string including special characters
        assertEquals(Token.Type.STAR, tokens.get(1).type);
        assertEquals(Token.Type.PLUS, tokens.get(3).type);
        assertEquals(Token.Type.ALTERNATION, tokens.get(5).type);
    }

    @Test
    public void testingBrackets(){
        ArrayList<Token> tokens = RegexEngine.tokenise("(ab)+(cd)");  // string including brackets 
        assertEquals(Token.Type.LEFTBRACKET, tokens.get(0).type);
        assertEquals(Token.Type.RIGHTBRACKET, tokens.get(3).type);
        assertEquals(Token.Type.LEFTBRACKET, tokens.get(5).type);
        assertEquals(Token.Type.RIGHTBRACKET, tokens.get(8).type);
    }

    @Test
    public void testingEmpty(){
        ArrayList<Token> tokens = RegexEngine.tokenise(""); // empty string 
        assertEquals(0, tokens.size());
    }
}
