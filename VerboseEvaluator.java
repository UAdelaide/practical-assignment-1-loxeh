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
        // renumberStates(); 
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

        // moving around accepting state and start state
        // states.remove(nfa.start);
        // states.add(0, nfa.start);

        // states.remove(nfa.end);
        // states.add(nfa.end);

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

    // construction of states is done backwards - nothing wrong with this implementation but not very easy to read
    // this function renumbers so its a lot easier to read 
    // private void renumberStates() {
    //     int newId = 0;
    //     for (State state : allStates) {
    //         state.id = newId++;
    //     }
    // }

    // now printing the transition table as we have collected all states and alphabet - complex one, looks okay in gradescope (alignment slightly off)


    // public void printTableComplex() {
    //     ArrayList<Character> alphabet = getAlphabet();

    //     // find longest state name for padding
    //     int maxStateWidth = 0;

    //     for (State state : allStates) {
    //         int width = ("q" + state.id).length() + 1; // +1 for > or *
    //         if (width > maxStateWidth) maxStateWidth = width;
    //     }

    //     int colWidth = maxStateWidth + 2;

    //     // find longest epsilon transition string for padding
    //     int maxEpsWidth = "epsilon".length();
    //     for (State state : allStates) {

    //         String eps = "";

    //         for (State next : state.epsilonTransitions) {
    //             if (eps.length() > 0) eps += ",";
    //             eps += "q" + next.id;
    //         }

    //         if (eps.length() > maxEpsWidth) maxEpsWidth = eps.length();
    //     }
    //     int epsWidth = maxEpsWidth + 2;

    //     // print header
    //     System.out.printf("%-" + colWidth + "s", "");
    //     System.out.printf("%-" + epsWidth + "s", "epsilon");

    //     for (char c : alphabet) {
    //         System.out.printf("%-" + colWidth + "s", c);
    //     }

    //     System.out.println("other");

    //     // print each state row
    //     for (State state : allStates) {
    //         StringBuilder row = new StringBuilder();
            
    //         // prefix and state name
    //         String prefix = "";
    //         if (state == nfa.start) prefix = ">";
    //         else if (state.isAccepting) prefix = "*";
    //         else prefix = " ";
    //         row.append(String.format("%-" + colWidth + "s", prefix + "q" + state.id));

    //         // epsilon transitions
    //         if (state.epsilonTransitions.isEmpty()) {
    //             row.append(String.format("%-" + epsWidth + "s", ""));
    //         } else {
    //             String eps = "";
    //             for (State next : state.epsilonTransitions) {
    //                 if (eps.length() > 0) eps += ",";
    //                 eps += "q" + next.id;
    //             }
    //             row.append(String.format("%-" + epsWidth + "s", eps));
    //         }

    //         // character transitions
    //         for (char c : alphabet) {
    //             if (state.transitions.containsKey(c)) {
    //                 String chars = "";
    //                 for (State next : state.transitions.get(c)) {
    //                     if (chars.length() > 0) chars += ",";
    //                     chars += "q" + next.id;
    //                 }
    //                 row.append(String.format("%-" + colWidth + "s", chars));
    //             } else {
    //                 row.append(String.format("%-" + colWidth + "s", ""));
    //             }
    //         }

    //         System.out.println(row.toString().stripTrailing()); // trim trailing spaces
    //     }
    // }

    // now printing the transition table as we have collected all states and alphabet - simple one as per Annas post 
    public void printTableSimple() {
        for (State state : allStates) {
            StringBuilder row = new StringBuilder();

            //prefix and state name 
            if (state == nfa.start){
                row.append(">");
            }else if (state.isAccepting){
                row.append("*");
            }else{
                row.append(" ");
            }
            row.append("q" + state.id + ": ");
            
            // flag for checking when to add a comma
            boolean firstTransition = true;

            // epsilon transitions
            for (State next : state.epsilonTransitions) {
                if (!firstTransition){
                    row.append(", ");
                }
                row.append("ε -> q" + next.id);
                firstTransition = false;
            }

            // character transitions
            for (char c : state.transitions.keySet()) {
                for (State next : state.transitions.get(c)) {
                    if (!firstTransition) row.append(", ");
                    row.append(c + " -> q" + next.id);
                    firstTransition = false;
                }
            }

            System.out.println(row.toString());

            }
        }


}
