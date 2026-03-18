// This file is responsible for the creation of NFA fragments 
// There are 4 main fragments to be considered: Basic (literal), Concatenation, Alternation, Klenee Star, Kleene Plus 
public class NFA {
    public State start;
    public State end;

    // basic constructor 
    public NFA (State start, State end) {
        this.start = start;
        this.end = end;
    }

    // single literal character transition (start -> a -> end)
    public static NFA literal(char c) { 
        State start = new State();
        State end = new State();

        start.addTransition(c, end); // just simply add the transition to end using c from start 
        return new NFA(start, end);
    }

    // concatenation of two NFAs (ab: a.start -> a -> a.end/b.start -> b -> b.end)
    public static NFA concatenate(NFA a, NFA b) { 
            a.end.addEpsilon(b.start); // use an epsilon transition to transition straight from the end of a to the start of b 
            return new NFA (a.start, b.end);
    }

    // alternation of two NFAs
    // a|b: start -> ε -> a.start -> a -> a.end -> ε -> end 
    //            -> ε -> b.start -> b -> b.end -> ε ->
    public static NFA alternate(NFA a, NFA b) {
        State start = new State();
        State end = new State();

        start.addEpsilon(a.start);
        start.addEpsilon(b.start);
        a.end.addEpsilon(end);
        b.end.addEpsilon(end);

        return new NFA(start, end);
    }

    // Klenee start for a single NFA 
    // a*: start -> ε -> a.start -> a -> a.end -> ε -> end etc
    //           -> ε -> end 
    public static NFA kleeneStar (NFA a) {
        State start = new State(); 
        State end = new State();

        start.addEpsilon(a.start); // can start at a 
        start.addEpsilon(end); // can skip straight to end 
        a.end.addEpsilon(a.start); // can loop back
        a.end.addEpsilon(end);

        return new NFA(start, end);
    }

    // Kleene Plus for a single NFA
    // same as Kleene Star but there is no transition straight from start to end
    // start -> ε -> a.start -> a -> a.end -> ε -> end 
    //                               ε -> a.start 
    public static NFA kleenePlus (NFA a) {
        State start = new State();
        State end = new State();

        start.addEpsilon(a.start); // must start at a
        a.end.addEpsilon(a.start); // can loop back
        a.end.addEpsilon(end); // can exit 

        return new NFA(start, end);
    }

}
