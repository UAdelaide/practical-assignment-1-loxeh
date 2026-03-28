import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

import java.util.ArrayList;
import java.util.HashSet;

public class VerboseEvaluator_Test {
    
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

    @Test
    // testing that start state is correctly identified
    public void testStartState() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a");
        ArrayList<State> states = verboseEvaluator.getAllStates();
        // first state should be the start state
        assertEquals(states.get(0), verboseEvaluator.nfa.start);
    }

    @Test
    // testing that accepting state is correctly identified
    public void testAcceptingState() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a");
        ArrayList<State> states = verboseEvaluator.getAllStates();
        // last state should be accepting
        assertTrue(states.get(states.size() - 1).isAccepting);
    }

    @Test
    // testing verboseEvaluate returns correct result for matching input
    public void testVerboseEvaluateMatch() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("abc");
        assertTrue(verboseEvaluator.evaluate("abc"));
    }

    @Test
    // testing verboseEvaluate returns correct result for non matching input
    public void testVerboseEvaluateNoMatch() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("abc");
        assertFalse(verboseEvaluator.evaluate("ab"));
    }

    @Test
    // testing verboseEvaluate returns correct result for empty input against star regex
    public void testVerboseEvaluateEmptyStar() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a*");
        assertTrue(verboseEvaluator.evaluate(""));
    }

    @Test
    // testing verboseEvaluate returns correct result for empty input against plus regex
    public void testVerboseEvaluateEmptyPlus() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a+");
        assertFalse(verboseEvaluator.evaluate(""));
    }

    @Test
    // testing getAllStates contains start and end state
    public void testGetAllStatesContainsStartAndEnd() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a");
        ArrayList<State> states = verboseEvaluator.getAllStates();
        assertTrue(states.contains(verboseEvaluator.nfa.start));
        assertTrue(states.contains(verboseEvaluator.nfa.end));
    }

    @Test
    // testing getAlphabet returns empty for regex with no literals (just operators)
    public void testGetAlphabetStar() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("a*");
        ArrayList<Character> alphabet = verboseEvaluator.getAlphabet();
        assertTrue(alphabet.contains('a'));
        assertEquals(1, alphabet.size());
    }

    @Test
    // testing verbose evaluate against spec example
    public void testVerboseEvaluateSpecExample() {
        VerboseEvaluator verboseEvaluator = buildVerboseEvaluator("(ab)*|c+");
        // empty string should match (ab)* allows zero matches
        assertTrue(verboseEvaluator.evaluate(""));
        // c should match c+
        assertTrue(verboseEvaluator.evaluate("c"));
        // ccc should match c+
        assertTrue(verboseEvaluator.evaluate("ccc"));
        // abc should not match
        assertFalse(verboseEvaluator.evaluate("abc"));
    }

    @Test
    // test that all states are reachable from start state
    public void testAllStatesReachable() {
        VerboseEvaluator ve = buildVerboseEvaluator("a|b");
        ArrayList<State> states = ve.getAllStates();
        // every state should be reachable - getAllStates traverses from start
        assertTrue(states.contains(ve.nfa.start));
        assertTrue(states.contains(ve.nfa.end));
        // should have more than just start and end
        assertTrue(states.size() > 2);
    }

    @Test
    // test that getAlphabet works for alternation regex
    public void testGetAlphabetAlternation() {
        VerboseEvaluator ve = buildVerboseEvaluator("a|b");
        ArrayList<Character> alphabet = ve.getAlphabet();
        assertTrue(alphabet.contains('a'));
        assertTrue(alphabet.contains('b'));
        assertEquals(2, alphabet.size());
    }

    @Test
    // test that getAlphabet works for regex with repeated characters
    public void testGetAlphabetRepeated() {
        VerboseEvaluator ve = buildVerboseEvaluator("a+|a*");
        ArrayList<Character> alphabet = ve.getAlphabet();
        assertTrue(alphabet.contains('a'));
        assertEquals(1, alphabet.size()); // only one unique character
    }
    
}

