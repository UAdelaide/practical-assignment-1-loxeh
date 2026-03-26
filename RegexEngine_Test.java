import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

import java.util.ArrayList;
import java.util.HashSet;

public class RegexEngine_Test {

    @Test
    // test that valid characters are tokenised correctly
    public void testValidCharacters() {
        ArrayList<Token> tokens = RegexEngine.tokenise("abc");
        assertEquals(3, tokens.size()); // should produce 3 tokens
        assertEquals(Token.Type.LITERAL, tokens.get(0).type); // a should be literal
        assertEquals(Token.Type.LITERAL, tokens.get(1).type); // b should be literal
        assertEquals(Token.Type.LITERAL, tokens.get(2).type); // c should be literal
    }

    @Test
    // test that uppercase letters are tokenised correctly
    public void testUppercaseLetters() {
        ArrayList<Token> tokens = RegexEngine.tokenise("ABC");
        assertEquals(3, tokens.size());
        assertEquals(Token.Type.LITERAL, tokens.get(0).type); // A should be literal
        assertEquals('A', tokens.get(0).value);
    }

    @Test
    // test that numbers are tokenised correctly
    public void testNumbers() {
        ArrayList<Token> tokens = RegexEngine.tokenise("123");
        assertEquals(3, tokens.size());
        assertEquals(Token.Type.LITERAL, tokens.get(0).type); // 1 should be literal
        assertEquals('1', tokens.get(0).value);
    }

    @Test
    // test that spaces are tokenised correctly as literals
    public void testSpace() {
        ArrayList<Token> tokens = RegexEngine.tokenise("a b");
        assertEquals(3, tokens.size());
        assertEquals(Token.Type.LITERAL, tokens.get(1).type); // space should be literal
        assertEquals(' ', tokens.get(1).value);
    }

    @Test
    // test that special characters are tokenised correctly
    public void testSpecialCharacters() {
        ArrayList<Token> tokens = RegexEngine.tokenise("a*b+c|d");
        assertEquals(Token.Type.STAR, tokens.get(1).type); // * should be STAR
        assertEquals(Token.Type.PLUS, tokens.get(3).type); // + should be PLUS
        assertEquals(Token.Type.ALTERNATION, tokens.get(5).type); // | should be ALTERNATION
    }

    @Test
    // test that brackets are tokenised correctly
    public void testBrackets() {
        ArrayList<Token> tokens = RegexEngine.tokenise("(ab)");
        assertEquals(Token.Type.LEFTBRACKET, tokens.get(0).type); // ( should be LEFTBRACKET
        assertEquals(Token.Type.RIGHTBRACKET, tokens.get(3).type); // ) should be RIGHTBRACKET
    }

    @Test
    // test that empty regex produces empty token list
    public void testEmptyRegex() {
        ArrayList<Token> tokens = RegexEngine.tokenise("");
        assertEquals(0, tokens.size()); // no tokens should be produced
    }

    @Test
    // test that literal values are stored correctly in tokens
    public void testLiteralValues() {
        ArrayList<Token> tokens = RegexEngine.tokenise("abc");
        assertEquals('a', tokens.get(0).value); // first token should store 'a'
        assertEquals('b', tokens.get(1).value); // second token should store 'b'
        assertEquals('c', tokens.get(2).value); // third token should store 'c'
    }

    @Test
    // test that a complex regex is tokenised correctly
    public void testComplexRegex() {
        ArrayList<Token> tokens = RegexEngine.tokenise("(ab)*|c+");
        assertEquals(8, tokens.size()); // should produce 8 tokens
        assertEquals(Token.Type.LEFTBRACKET, tokens.get(0).type);   // (
        assertEquals(Token.Type.LITERAL, tokens.get(1).type);        // a
        assertEquals(Token.Type.LITERAL, tokens.get(2).type);        // b
        assertEquals(Token.Type.RIGHTBRACKET, tokens.get(3).type);   // )
        assertEquals(Token.Type.STAR, tokens.get(4).type);           // *
        assertEquals(Token.Type.ALTERNATION, tokens.get(5).type);    // |
        assertEquals(Token.Type.LITERAL, tokens.get(6).type);        // c
        assertEquals(Token.Type.PLUS, tokens.get(7).type);           // +
    }
}