import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

import java.util.ArrayList;
import java.util.HashSet;

public class Evaluator_Test {
    
    // helper method to build evalautor from regex string
    private Evaluator buildEvaluator(String regex) {
        ArrayList<Token> tokens = RegexEngine.tokenise(regex);
        Parser parser = new Parser (tokens);
        NFA nfa = parser.parse();
        return new Evaluator(nfa);
    }

    @Test
    // test literal regex against inputs
    public void testLiteral() {
        Evaluator evaluator = buildEvaluator("a"); 
        assertTrue(evaluator.evaluate("a")); // a matches "a"
        assertFalse(evaluator.evaluate("aa")); // aa has no match within "a", doesnt account for multiple occurences 
        assertFalse(evaluator.evaluate("b")); // b has no match within "a"
        assertFalse(evaluator.evaluate("")); // no match within "a"
    }

    @Test
    // test regex containing kleene star against inputs 
    public void testStar() {
        Evaluator evaluator = buildEvaluator("a*"); 
        assertTrue(evaluator.evaluate("a")); // a matches "a*"
        assertTrue(evaluator.evaluate("aa")); // aa matches "a*", accepts multiple occurences 
        assertFalse(evaluator.evaluate("b")); // b has no match within "a*"
        assertTrue(evaluator.evaluate("")); // match within "a*", accepts empty state        
    }

    @Test
    // test regex containing kleene plus against inputs 
    public void testPlus() {
        Evaluator evaluator = buildEvaluator("a+"); 
        assertTrue(evaluator.evaluate("a")); // a matches "a+"
        assertTrue(evaluator.evaluate("aa")); // aa matches "a+", accepts multiple occurences 
        assertFalse(evaluator.evaluate("b")); // b has no match within "a+"
        assertFalse(evaluator.evaluate("")); // no match within "a+", unlike kleene star, does not accept empty state    
    }

    @Test 
    // test regex containing alternation against inputs
    public void testAlternation() {
        Evaluator evaluator = buildEvaluator("a|b");
        assertTrue(evaluator.evaluate("a")); // a matches "a|b"
        assertTrue(evaluator.evaluate("b")); // b matches "a|b"
        assertFalse(evaluator.evaluate("c")); // c has no match within "a|b", not accepting random letters
    }

    @Test 
    // test regex containing concatenation against inputs 
    public void testConcatenation() {
        Evaluator evaluator = buildEvaluator("ab");
        assertTrue(evaluator.evaluate("ab")); // ab matches "ab"
        assertFalse(evaluator.evaluate("a")); // a does not match "ab", not accepting partial 
        assertFalse(evaluator.evaluate("b")); // b does not match "ab", not accepting partial 
    }


    @Test 
    // testing regex containing brackets using kleene star
    public void testBracketsKleeneStar() {
        Evaluator evaluator = buildEvaluator("(ab)*");
        assertTrue(evaluator.evaluate("ab")); // ab matches "ab*"
        assertTrue(evaluator.evaluate("abab")); // ab matches "(ab)*", accepts multiple occurences 
        assertTrue(evaluator.evaluate("")); // match within "(ab)*", accepts empty 
    }

    @Test   
    // testing complex regex contains brackets, kleene star, alternation and concatenation from canvas
    public void testComplex() {
        Evaluator evaluator = buildEvaluator("(ab)*|c+");
        assertFalse(evaluator.evaluate("abc")); // abc does not match "(ab)*|c+", consistent with example
        assertTrue(evaluator.evaluate("ccc")); // ccc does match "(ab)*|c+", consistent with example
    }
}
