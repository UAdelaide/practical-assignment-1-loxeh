import org.junit.Test;
import static org.junit.Assert.*; // need more than just assertEquals for this test

import java.util.ArrayList;

public class State_Test {
    

    @Test 
    public void testStateDefaults() { // test new state is created with correct defaults 
        State s = new State();

        assertFalse(s.isAccepting); // should not be accepting by default
        assertTrue(s.transitions.isEmpty()); // no transitions by default
        assertTrue(s.epsilonTransitions.isEmpty()); // no epsilion transitions by default
    }

    @Test 
    public void testStateIds() { // test that state ids are unique and increment 
        State s1 = new State();
        State s2 = new State();

        assertNotEquals(s1.id, s2.id);
        assertEquals(s1.id + 1, s2.id);
    }

    @Test
    public void testAddingTransitions() { // test the addition of transitions
        State s1 = new State();
        State s2 = new State();

        s1.addTransition('a', s2);
        assertTrue(s1.transitions.containsKey('a'));
        assertEquals(s2, s1.transitions.get('a').get(0));
    }

    @Test
    public void testingAddingEpsilonTransitions() {
        State s1 = new State();
        State s2 = new State();

        s1.addEpsilon(s2);

        assertEquals(1, s1.epsilonTransitions.size()); // size of epsilonTransitions should only be 1 
        assertEquals(s2, s1.epsilonTransitions.get(0)); // epsilon transition lead to s2
    }

    @Test 
    public void testMultipleTransition() { // test that mutltiple transitions on the same character are stored
        State s1 = new State();
        State s2 = new State();
        State s3 = new State();

        s1.addTransition('a', s2);
        s1.addTransition('a', s3);

        assertEquals(2, s1.transitions.get('a').size());
    }

    @Test
    // test that adding a second transition on same character adds to existing list
    public void testAddingTransitionExistingKey() {
        State s1 = new State();
        State s2 = new State();
        State s3 = new State();
        s1.addTransition('a', s2); // first transition on 'a' - creates new list
        s1.addTransition('a', s3); // second transition on 'a' - adds to existing list
        assertEquals(2, s1.transitions.get('a').size()); // both states should be in list
        assertTrue(s1.transitions.get('a').contains(s2));
        assertTrue(s1.transitions.get('a').contains(s3));
    }
}
