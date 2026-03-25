// High level of how the Evaluator should behave - provided by Claude Sonnet 4.6 with context provided in for using epsilon closures for this. 

// "Evaluator" compares the input string against its respective NFA 
// there are there steps
// 1. computing epsilon closure for the start state 
// 2. for each character in the input step NFA forward, move to all reachable states then compute the epsilon closure
// 3. after reading of the character in put, check if any current state is an accpeting state 

// ArrayList chosen as data structure to contain epsilon transitions yet to process
// HashSet chosen as data structure to represent states the NFA is currently in, holds only unique values and constant time 


import java.util.HashSet;
import java.util.ArrayList; 


public class Evaluator {

    protected NFA nfa; // protected for inheritence by VerboseEvaluator 

    public Evaluator (NFA nfa) {
        this.nfa = nfa;
        nfa.end.isAccepting = true; // final state of NFA should be accepting state 
    }

    public HashSet<State> epsilonClosure(HashSet<State> states) {
        HashSet<State> closure = new HashSet<State>(states);
        ArrayList<State> transitionsToProcess = new ArrayList<State>(states);

        while (!transitionsToProcess.isEmpty()) { // keep going until to transitions to process
            State currentState = transitionsToProcess.remove(transitionsToProcess.size() - 1); // removes the last element of transitions to be processed and set current state to 

            for (State nextState : currentState.epsilonTransitions) {
                if (!closure.contains(nextState)) { // have we seen this state before
                    closure.add(nextState); // if not add to closure
                    transitionsToProcess.add(nextState); // if not add to transitions to be explored
                }
            }
        }
        return closure;
    }

    public HashSet<State> stepThrough(HashSet<State> states, char c) {
        HashSet<State> nextState = new HashSet<State>(); // reachable on character c 

        for (State currentState : states) { // each state we are currently in
            if (currentState.transitions.containsKey(c)) { // has transition on c
                nextState.addAll(currentState.transitions.get(c)); // add in 
            }
        }

        return epsilonClosure(nextState); // compute reachable states, may have changed after stepping on c

    }

    // just evaluating if current state is end state (accepting state)
    public boolean isAcceptingState (HashSet<State> states) {
        for (State s : states) {
            if (s.isAccepting) {
                return true;
            }
        }
        return false;
    }

    public boolean evaluate(String inputString) {
        HashSet<State> currentStates = new HashSet<State>();
        currentStates.add(nfa.start); // start with just the nfa start 
        currentStates = epsilonClosure(currentStates); // get states from nfa start 

        for (int i = 0; i < inputString.length(); i++) {
            currentStates = stepThrough(currentStates, inputString.charAt(i));  // step through each character of input string, replacing current states with new set of reachable states each time 
        }
        return isAcceptingState(currentStates); // check if any state in currentStates is the accepting state 

    }

}
