import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

public class NFA_Test {


    @Test
    // testing that literal NFA has the correct structure 
    public void testLiteral(){
        NFA nfa = NFA.literal('a');

        assertNotNull(nfa.start); // should have a start state
        assertNotNull(nfa.end); // should have an end state
        assertNotEquals(nfa.start, nfa.end); // start and end should not be equal
        assertTrue(nfa.start.transitions.containsKey('a')); // start state should have the transition 'a'
        assertEquals(nfa.end, nfa.start.transitions.get('a').get(0)); // 'a' transition from start shoud lead to end
    }

    @Test 
    // test that two NFAs successfully concatenate 
    public void testConcatenate(){
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');

        NFA ab = NFA.concatenate(a, b);

        assertEquals(a.start, ab.start); 
        assertEquals(b.end, ab.end); 
        assertTrue(a.end.epsilonTransitions.contains(b.start)); // there should epsilon transition from a end to to b start 
    }

    @Test 
    // test that alternate correctly connects two NFAs
    public void testAlternate(){
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');

        NFA alternate = NFA.alternate(a, b);

        assertNotEquals(alternate.start, a.start); // starts should be different 
        assertNotEquals(alternate.start, b.start); // starts should be different 
        assertNotEquals(alternate.end, a.end); // ends should be different 
        assertNotEquals(alternate.end, b.end); // end should be different 

        assertTrue(alternate.start.epsilonTransitions.contains(a.start)); // new start should epsilon to a 
        assertTrue(alternate.start.epsilonTransitions.contains(b.start)); // new start should epsilon to b
        
        assertTrue(b.end.epsilonTransitions.contains(alternate.end)); // a should epsilon to new end created in alternate 
        assertTrue(b.end.epsilonTransitions.contains(alternate.end)); // b should epsilon to new end created in alternate 

    }

    @Test 
    // test that kleene start allows zero or more matches 
    public void testStar() {

        NFA a = NFA.literal('a');
        NFA aStar = NFA.kleeneStar(a);

        assertTrue(aStar.start.epsilonTransitions.contains(a.start)); // can enter a
        assertTrue(aStar.start.epsilonTransitions.contains(aStar.end)); // can skip straight to the end 

        assertTrue(a.end.epsilonTransitions.contains(a.start)); // can loop back to a start from a end 
        assertTrue(a.end.epsilonTransitions.contains(aStar.end)); // can finish 
    }


    @Test 
    // test that kleene plus allows at least one or more matches

    public void testPlus(){

        NFA a = NFA.literal('a');
        NFA aPlus = NFA.kleenePlus(a);

        assertTrue(aPlus.start.epsilonTransitions.contains(a.start)); // must enter a  
        assertFalse(aPlus.start.epsilonTransitions.contains(aPlus.end)); // cannot skip straight to end 

        assertTrue(a.end.epsilonTransitions.contains(a.start)); // can loop back to a start from a end 
        assertTrue(a.end.epsilonTransitions.contains(aPlus.end)); // can finish 

    }

    @Test
    // test that concatenation of three literals works correctly
    public void testTripleConcatenation() {
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');
        NFA c = NFA.literal('c');
        NFA ab = NFA.concatenate(a, b);
        NFA abc = NFA.concatenate(ab, c);
        assertNotNull(abc.start);
        assertNotNull(abc.end);
        assertTrue(abc.start.transitions.containsKey('a'));
    }

    @Test
    // test that alternation of three literals works correctly
    public void testTripleAlternation() {
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');
        NFA c = NFA.literal('c');
        NFA ab = NFA.alternate(a, b);
        NFA abc = NFA.alternate(ab, c);
        assertNotNull(abc.start);
        assertNotNull(abc.end);
        assertEquals(2, abc.start.epsilonTransitions.size());
    }

    @Test
    // test that star of a group works correctly
    public void testStarOfGroup() {
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');
        NFA ab = NFA.concatenate(a, b);
        NFA starred = NFA.kleeneStar(ab);
        // start should epsilon to end (zero matches)
        assertTrue(starred.start.epsilonTransitions.contains(starred.end));
        // start should epsilon to inner start
        assertTrue(starred.start.epsilonTransitions.contains(ab.start));
    }

    @Test
    // test that literal NFA has exactly one character transition
    public void testLiteralOneTransition() {
        NFA nfa = NFA.literal('a');
        assertEquals(1, nfa.start.transitions.size()); // only one character transition
        assertTrue(nfa.start.epsilonTransitions.isEmpty()); // no epsilon transitions
    }

    @Test
    // test that concatenation produces no new states
    public void testConcatenateNoNewStates() {
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');
        State aStart = a.start;
        State bEnd = b.end;
        NFA ab = NFA.concatenate(a, b);
        // concatenate should reuse existing states not create new ones
        assertEquals(aStart, ab.start);
        assertEquals(bEnd, ab.end);
    }

    @Test
    // test that alternate produces two new states
    public void testAlternateNewStates() {
        NFA a = NFA.literal('a');
        NFA b = NFA.literal('b');
        State aStart = a.start;
        State bEnd = b.end;
        NFA alt = NFA.alternate(a, b);
        // alternate should create new start and end states
        assertNotEquals(aStart, alt.start);
        assertNotEquals(bEnd, alt.end);
    }
}
