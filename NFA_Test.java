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

}
