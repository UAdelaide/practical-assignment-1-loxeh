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
    // test that spaces are valid literals
    public void testSpaceLiteral() {
        Evaluator evaluator = buildEvaluator("a b");
        assertTrue(evaluator.evaluate("a b")); // "a b" matches "a b"
        assertFalse(evaluator.evaluate("ab")); // "ab" does not match "a b", space is required
    }

    @Test
    // test that uppercase letters are valid literals
    public void testUppercaseLiterals() {
        Evaluator evaluator = buildEvaluator("ABC");
        assertTrue(evaluator.evaluate("ABC")); // ABC matches "ABC"
        assertFalse(evaluator.evaluate("abc")); // abc does not match "ABC", case sensitive
    }    


    @Test
    // test regex containing kleene star against inputs 
    public void testStar() {
        Evaluator evaluator = buildEvaluator("a*"); 
        assertTrue(evaluator.evaluate("a")); // a matches "a*"
        assertTrue(evaluator.evaluate("aa")); // aa matches "a*", accepts multiple occurences 
        assertTrue(evaluator.evaluate("aaaaaaaaaaaaaaaaaaaaaaa")); // aaaaaaaaaaaaaaaaaaaaaaa matches "a*", accepts multiple occurences 
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
    // test alternation with more than two options
    public void testMultipleAlternation() {
        Evaluator evaluator = buildEvaluator("a|b|c");
        assertTrue(evaluator.evaluate("a")); // a matches "a|b|c"
        assertTrue(evaluator.evaluate("b")); // b matches "a|b|c"
        assertTrue(evaluator.evaluate("c")); // c matches "a|b|c"
        assertFalse(evaluator.evaluate("d")); // d does not match "a|b|c"
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

    @Test
    // test empty string against various regex
    public void testEmptyStringVariousRegex() {
        assertTrue(buildEvaluator("a*").evaluate("")); // star accepts empty
        assertTrue(buildEvaluator("(ab)*").evaluate("")); // group star accepts empty
        assertFalse(buildEvaluator("a+").evaluate("")); // plus rejects empty
        assertFalse(buildEvaluator("a").evaluate("")); // literal rejects empty
        assertFalse(buildEvaluator("ab").evaluate("")); // concatenation rejects empty
    }


    @Test
    // test that numbers are valid literals in evaluation
    public void testNumberLiterals() {
        Evaluator evaluator = buildEvaluator("123");
        assertTrue(evaluator.evaluate("123")); // 123 is accepted by "123"
        assertFalse(evaluator.evaluate("124")); // 124 is not accepted by "123"
        assertFalse(evaluator.evaluate("12")); // 12 is not accepted by "123", incomplete
    }

    @Test
    // test that a string longer than the regex is rejected
    public void testInputLongerThanRegex() {
        Evaluator evaluator = buildEvaluator("ab");
        assertFalse(evaluator.evaluate("abc")); // abc is not accepted by "ab", too long
        assertFalse(evaluator.evaluate("abb")); // abb is not accepted by "ab", too long
    }

    @Test
    // test alternation where both sides are groups
    public void testAlternationBothGroups() {
        Evaluator evaluator = buildEvaluator("(ab)|(cd)");
        assertTrue(evaluator.evaluate("ab")); // ab is accepted by "(ab)|(cd)"
        assertTrue(evaluator.evaluate("cd")); // cd is accepted by "(ab)|(cd)"
        assertFalse(evaluator.evaluate("ac")); // ac is not accepted by "(ab)|(cd)"
        assertFalse(evaluator.evaluate("abcd")); // abcd is not accepted by "(ab)|(cd)"
    }

    @Test
    // test that plus requires at least one match for grouped expressions
    public void testGroupPlus() {
        Evaluator evaluator = buildEvaluator("(ab)+");
        assertTrue(evaluator.evaluate("ab")); // ab is accepted by "(ab)+"
        assertTrue(evaluator.evaluate("abab")); // abab is accepted by "(ab)+"
        assertFalse(evaluator.evaluate("")); // empty is not accepted by "(ab)+"
        assertFalse(evaluator.evaluate("a")); // a is not accepted by "(ab)+", incomplete group
    }

    @Test
    // test complex regex with concatenation of star and plus
    public void testStarAndPlusConcatenation() {
        Evaluator evaluator = buildEvaluator("a*b+");
        assertTrue(evaluator.evaluate("b")); // b is accepted, a* allows zero matches
        assertTrue(evaluator.evaluate("ab")); // ab is accepted
        assertTrue(evaluator.evaluate("aaabbb")); // aaabbb is accepted
        assertFalse(evaluator.evaluate("")); // empty is not accepted, b+ requires at least one
        assertFalse(evaluator.evaluate("a")); // a is not accepted, b+ requires at least one
    }

    @Test
    // test complex regex with alternation and concatenation
    public void testAlternationAndConcatenation() {
        Evaluator evaluator = buildEvaluator("ab|cd");
        assertTrue(evaluator.evaluate("ab")); // ab is accepted by "ab|cd"
        assertTrue(evaluator.evaluate("cd")); // cd is accepted by "ab|cd"
        assertFalse(evaluator.evaluate("ac")); // ac is not accepted by "ab|cd"
        assertFalse(evaluator.evaluate("a")); // a is not accepted by "ab|cd", incomplete
    }

    @Test
    // test that a single digit is accepted by a digit regex
    public void testSingleDigit() {
        Evaluator evaluator = buildEvaluator("1");
        assertTrue(evaluator.evaluate("1")); // 1 is accepted by "1"
        assertFalse(evaluator.evaluate("2")); // 2 is not accepted by "1"
    }

    @Test
    // test that mixed alphanumeric regex works correctly
    public void testAlphanumeric() {
        Evaluator evaluator = buildEvaluator("a1b2");
        assertTrue(evaluator.evaluate("a1b2")); // exact match
        assertFalse(evaluator.evaluate("a1b")); // incomplete
        assertFalse(evaluator.evaluate("a1b3")); // wrong digit
    }

    @Test
    // test that alternation of star and plus works correctly
    public void testAlternationStarPlus() {
        Evaluator evaluator = buildEvaluator("a*|b+");
        assertTrue(evaluator.evaluate("")); // empty accepted by a*
        assertTrue(evaluator.evaluate("a")); // a accepted by a*
        assertTrue(evaluator.evaluate("b")); // b accepted by b+
        assertTrue(evaluator.evaluate("bbb")); // bbb accepted by b+
        assertFalse(evaluator.evaluate("ab")); // ab not accepted by either branch
    }

    @Test
    // test that nested star works correctly - (a*)* 
    public void testNestedStar() {
        Evaluator evaluator = buildEvaluator("(a*)*");
        assertTrue(evaluator.evaluate("")); // empty accepted
        assertTrue(evaluator.evaluate("a")); // a accepted
        assertTrue(evaluator.evaluate("aaa")); // aaa accepted
    }

    @Test
    // test that completely wrong input drives NFA to empty state set
    public void testNoMatchingTransitions() {
        Evaluator evaluator = buildEvaluator("a");
        assertFalse(evaluator.evaluate("b")); // no transition on 'b' from start
        assertFalse(evaluator.evaluate("ba")); // drives to empty set then tries to continue
    }

    @Test
    // test that input drives NFA to empty state then continues
    public void testEmptyStateSetContinuation() {
        Evaluator evaluator = buildEvaluator("ab");
        assertFalse(evaluator.evaluate("ba")); // b drives to empty set, a has nowhere to go
    }
}
