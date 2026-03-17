// This file will be used for representing a single state in an NFA 
import java.util.ArrayList;
import java.util.HashMap;

public class State {
    
    public int id; // unique id for this state

    public HashMap<Character, ArrayList<State>> transitions; // transitions on a character, mapping a character to a list of states

    public ArrayList<State> epsilonTransitions; // list of states reachable on epsilon, needs to be separate from regular transitions

    public boolean isAccepting; // determining whether this is an accepting state

    private static int counter = 0; // counter for giving each state a unique id, shared across all instances of state 

    // Constructor for state 
    public State(){
        this.id = counter++;
        this.transitions = new HashMap<Character, ArrayList<State>>();
        this.epsilonTransitions = new ArrayList<State>();
        this.isAccepting = false;
    }


    // add a transition of character "c" to state "to"
    public void addTransition(char c, State to) {
        if (!transitions.containsKey(c)){
            transitions.put(c, new ArrayList<State>()); // create new ArrayList if no transition on c exists yet 
        }
        transitions.get(c).add(to); // add to into the HashMap for c 
    }

    // just add state into epsilonTransitions if it exists, no HashMap needed
    public void addEpsilon(State to) {
        epsilonTransitions.add(to);
    }


}
