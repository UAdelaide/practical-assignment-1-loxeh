import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

import java.util.ArrayList;
import java.util.HashSet;

public class VerboseEvaluator_test {
    
    // helper method to build verbose evalautor from regex string
    private VerboseEvaluator buildVerboseEvaluator(String regex) {
        ArrayList<Token> tokens = RegexEngine.tokenise(regex);
        Parser parser = new Parser (tokens);
        NFA nfa = parser.parse();
        return new VerboseEvaluator(nfa);
    }    

    @Test 
    // testing that getALlStates returns the correct number of states for basic literal  regex
    public void testGetAllStatesSizeLiteral() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a");
        assertEquals(2, verboseEvaluator.getAllStates().size()); // should have 2 states (start and end)
    }

    @Test 
    // testing that getALlStates returns the correct number of states for more complex regex
    public void testGetAllStatesSizeComplex() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("(ab)*|c+");
        assertEquals(12, verboseEvaluator.getAllStates().size()); // according to construction technique, should have 12 states, differs from canvas example but works
    }


    @Test 
    // testing that getAlphabet returns correct character for a basic regex 
    public void testGetAlphabetBasic() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("ab");
        ArrayList<Character> alphabet = verboseEvaluator.getAlphabet();
        assertTrue(alphabet.contains('a'));
        assertTrue(alphabet.contains('b'));
        assertEquals(2, alphabet.size()); // only contains 2 (a,b)         
    }

    @Test 
    // testing that getAlphabet returns correct character for a complex regex 
    public void testGetAlphabetComplex() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("(ab)*|c+");
        ArrayList<Character> alphabet = verboseEvaluator.getAlphabet();
        assertTrue(alphabet.contains('a'));
        assertTrue(alphabet.contains('b'));
        assertTrue(alphabet.contains('c'));
        assertEquals(3, alphabet.size()); // only contains 2 (a,b,c)
    }    

    @Test 
    // testing that getAlphabet does not return duplicates of same character
    public void testGetAlphabetNoDuplicates() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("aaa");
        ArrayList<Character> alphabet = verboseEvaluator.getAlphabet();
        assertTrue(alphabet.contains('a'));
        assertEquals(1, alphabet.size()); // only contains 'a'     
    }    
}

