// regex the user has enetered should print a transition trable for the ɛ-NFA generated followed by the word "ready"

/* following the example given, we need:
- a header row (epsilon a   b   c   other)
- rows per state (state name using q)
- epsilon transitions and character transitions for each state 
- > for starting state
- * for accepting state
*/

import java.util.ArrayList;
import java.util.HashSet;

public class VerboseEvaluator extends Evaluator { // "extends" for inheritence from Evaluator (inhereting epsiLonClosure(), stepThrough(), isAcceptingState(), evaluate())

    // private NFA nfa;
    private ArrayList<State> allStates; // all states in the NFA 

    public VerboseEvaluator(NFA nfa) {
        super(nfa); // calls Evaluator's constructor 
        // nfa.end.isAccepting = true; - set in Evaluator's constructor
        this.allStates = getAllStates(); // collect all states in NFA on construction
    }

    public ArrayList<State> getAllStates() {
        ArrayList<State> states = new ArrayList<State>(); // all states
        HashSet<State> visited = new HashSet<State>(); // tracking states already been visited
        ArrayList<State> statesToProcess = new ArrayList<State>(); // states to still process
    
        statesToProcess.add(nfa.start); // start traversal at NFA start
        visited.add(nfa.start);

        while (!statesToProcess.isEmpty()) {
            State currentState = statesToProcess.remove(statesToProcess.size() - 1); // pop last state
            states.add(currentState);
            
            // follow epsilon transitions 
            for (State nextState : currentState.epsilonTransitions) {
                if (!visited.contains(nextState)) { // only need to visit each state once 
                    visited.add(nextState);
                    statesToProcess.add(nextState);
                }
            }

            // follow charactrer transitions
            for (ArrayList<State> characterTransitions : currentState.transitions.values()) { // get all the different character transitions 
                for (State nextState : characterTransitions) { 
                    if (!visited.contains(nextState)) { // only need to visit each state once 
                        visited.add(nextState);
                        statesToProcess.add(nextState);
                    }                    
                }
            }
        }

        return states;
    }


    // collect all character that appear in transition across all states
    // need to return all the keys from the allStates HashSet 
    public ArrayList<Character> getAlphabet() {
        ArrayList<Character> alphabet = new ArrayList<Character>();

        for (State state : allStates) {
            for (char c: state.transitions.keySet()) { // get all character this state transitions on 
                if (!alphabet.contains(c)) {
                    alphabet.add(c); // add if not already in alphabet
                }

            }
        }
        return alphabet;
    }

    // now printing the transition table as we have collected all states and alphabet 
    public void printTable() {
        ArrayList<Character> alphabet = getAlphabet();

        System.out.print("    epsilon ");
        for (char c : alphabet) {
            System.out.print(c + "   ");
        }
        System.out.println("other");

        for (State state : allStates) {
            if (state == nfa.start) {
                System.out.print(">"); // start
            } else if (state.isAccepting){
                System.out.print("*"); // accepting 
            }

            // print state name (q + id)
            System.out.print("q" + state.id + "  ");

            // print epsilon transitions 
            if (state.epsilonTransitions.isEmpty()) {
                System.out.print("       ");

            } else {
                String epsilonStates = "";

                for (State nextState : state.epsilonTransitions) {
                    if (epsilonStates.length() > 0) {
                        epsilonStates += ",";
                    }
                    epsilonStates += "q" + nextState.id;
                }
                System.out.print(epsilonStates + " ");
            }

            for (char c : alphabet) {
                if (state.transitions.containsKey(c)) {
                    for (State nextState : state.transitions.get(c)) {
                        System.out.print("q" + nextState.id + " ");
                    }
                } else {
                    System.out.print("     ");
                }
             }

             System.out.println(); // if implementation is correct, other should always be empty
        }
    }


}
