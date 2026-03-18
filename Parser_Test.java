import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

import java.util.ArrayList;

public class Parser_Test {
    

    // helper for parsing regex string and returning NFA      
    // tokenises automatically and detects what functions are to be performed  
    private NFA parseRegex(String regex) {
        ArrayList<Token> tokens = RegexEngine.tokenise(regex);
        Parser parser = new Parser(tokens);
        return parser.parse();
    }


    @Test 
    // testing that a single literal parses correctly 
    public void testSingleLiteral() {
        NFA nfa = parseRegex("a");

        assertNotNull(nfa.start);
        assertNotNull(nfa.end);
        assertTrue(nfa.start.transitions.containsKey('a'));

    }    


    
    @Test
    // testing that basic concatenation parses correctly 
    public void testConcatenation(){
        NFA nfa = parseRegex("ab");

        assertNotNull(nfa.start);
        assertNotNull(nfa.end);

        // get state after a
        State afterA = nfa.start.transitions.get('a').get(0);

        // there should be epsilon transition to a state that has a transition on b
        State beforeB = afterA.epsilonTransitions.get(0);

        assertTrue(beforeB.transitions.containsKey('b'));
    }

    @Test
    // test that alternation correctly connects two NFAs    
    public void testAlternation() {
        NFA nfa = parseRegex("a|b");

        // get the first two branches (epsilon transitions) to a and b from start 
        State branchA = nfa.start.epsilonTransitions.get(0);
        State branchB = nfa.start.epsilonTransitions.get(1);

        // each branch should transition on its respective character
        assertTrue(branchA.transitions.containsKey('a'));
        assertTrue(branchB.transitions.containsKey('b'));

        // both branches should now reach the same end state
        State afterA = branchA.transitions.get('a').get(0);
        State afterB = branchB.transitions.get('b').get(0);
        assertEquals(nfa.end, afterA.epsilonTransitions.get(0));
        assertEquals(nfa.end, afterB.epsilonTransitions.get(0));
    }
    

    @Test 
    // test that star allows zero or more matches 
    public void testStar() {
        NFA nfa = parseRegex("a*");

        // get the start of the first epsilon from start
        State firstEpsilon = nfa.start.epsilonTransitions.get(0);

        // first epsilon state should be able to transition on a
        assertTrue(firstEpsilon.transitions.containsKey('a'));

        // get the state after a
        State afterA = firstEpsilon.transitions.get('a').get(0);
        // this should be able to loop back 
        assertTrue(afterA.epsilonTransitions.contains(firstEpsilon));

        // after a should be able to exit to end 
        assertTrue(afterA.epsilonTransitions.contains(nfa.end));

        // start should also be able to skip entirely to end, satisfying zero matches
        assertTrue(nfa.start.epsilonTransitions.contains(nfa.end));
    }


    @Test 
    // test that plus at least one or more transitions
    public void testPlus() {
        NFA nfa = parseRegex("a+");

        // get the start of the first epsilon from start
        State firstEpsilon = nfa.start.epsilonTransitions.get(0);

        // first epsilon state should be able to transition on a
        assertTrue(firstEpsilon.transitions.containsKey('a'));

        // get the state after a
        State afterA = firstEpsilon.transitions.get('a').get(0);
        // this should be able to loop back 
        assertTrue(afterA.epsilonTransitions.contains(firstEpsilon));

        // after a should be able to exit to end 
        assertTrue(afterA.epsilonTransitions.contains(nfa.end));

        // start should not be able to transition straight to end, satifying at least one match
        assertFalse(nfa.start.epsilonTransitions.contains(nfa.end));
    }

    @Test 
    // testing brackets with star (mainly to determine if brackets are working correctly)
    public void testBracketsWithStar() {
        NFA nfa = parseRegex("(ab)*");

        // should be able to go straight to end 
        assertTrue(nfa.start.epsilonTransitions.contains(nfa.end));

        State startUsingAB;

        // find which epsilon transition is to ab and not straight to the end 
        if (nfa.start.epsilonTransitions.get(0) == nfa.end) {
            startUsingAB = nfa.start.epsilonTransitions.get(1);
        }else{
            startUsingAB = nfa.start.epsilonTransitions.get(0);
        }

        // should transition on a first
        assertTrue(startUsingAB.transitions.containsKey('a'));

        // get state after a
        State afterA = startUsingAB.transitions.get('a').get(0);

        // there should be epsilon transition to a state that has a transition on b
        State beforeB = afterA.epsilonTransitions.get(0);

        assertTrue(beforeB.transitions.containsKey('b'));

        // get the state after b 
        State afterB = beforeB.transitions.get('b').get(0);

        //after b should be able to loop back to inner start, proving star wraps ab and not just one
        assertTrue(afterB.epsilonTransitions.contains(startUsingAB));

    }

    @Test 
    // testing precedencing, ab* should be a then b* not (ab)*

    public void testingPrecedenceOfStarOverConcatenation() {
        NFA nfa = parseRegex("ab*");

        // start should not be able to transition directly to end as b is only starred
        assertFalse(nfa.start.epsilonTransitions.contains(nfa.end));

        // get state after a
        State afterA = nfa.start.transitions.get('a').get(0);      

        // there is an epsilon transition from afterA to the start of klenee star (on b)
        State starStart = afterA.epsilonTransitions.get(0);
        // this should be able to skip directly to end 
        assertTrue(starStart.epsilonTransitions.contains(nfa.end));

        State startUsingB;   
        // find which epsilon transition is to b and not straight to the end 
        if (starStart.epsilonTransitions.get(0) == nfa.end) {
            startUsingB = starStart.epsilonTransitions.get(1);
        }else{
            startUsingB = starStart.epsilonTransitions.get(0);
        }

        assertTrue(startUsingB.transitions.containsKey('b')); // should transition on b
        assertFalse(startUsingB.transitions.containsKey('a')); // should NOT transition on a
    }


    // now its time to put it all together for a complex equation, using the one provided on canvas

    @Test 
    public void testComplex() {
        NFA nfa = parseRegex("(ab)*|c+");

        // check there is two epsilon transition out of start, confirming alternation is applied last 
        assertEquals(2, nfa.start.epsilonTransitions.size());

        // grab those two branches
        State branch1 = nfa.start.epsilonTransitions.get(0);
        State branch2 = nfa.start.epsilonTransitions.get(1);

        // branch 1 should be *, has 2 epsilon transitions (end and inside)
        assertEquals(2, branch1.epsilonTransitions.size());
        // branch 2 should be +, has 1 epsilon transition (inside)
        assertEquals(1, branch2.epsilonTransitions.size());

        // find which epsilon transition is to ab not straight to end 
        State starUsingAB;
        if (branch1.epsilonTransitions.get(0) == nfa.end) {
            starUsingAB = branch1.epsilonTransitions.get(1);
        }else{
            starUsingAB = branch1.epsilonTransitions.get(0);
        }

        // star using ab should start with a 
        assertTrue(starUsingAB.transitions.containsKey('a'));

        // c+ branch should not be able to skip to end
        assertFalse(branch2.epsilonTransitions.contains(nfa.end));

        // after transition to c and then be able to transition to the end and beginning of loop
        State plusAfterC = branch2.epsilonTransitions.get(0).transitions.get('c').get(0);

        
        assertTrue(plusAfterC.epsilonTransitions.contains(branch2.epsilonTransitions.get(0)));
        assertTrue(plusAfterC.epsilonTransitions.get(1).epsilonTransitions.contains(nfa.end));
        
    }
}

