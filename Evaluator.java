// High level of how the Evaluator should behave - provided by Claude Sonnet 4.6 with context provided in for using epsilon closures for this. 

// "Evaluator" compares the input regular expression against its respective NFA 
// there are there steps
// 1. computing epsilon closure for the start state 
// 2. for each character in the input step NFA forward, move to all reachable states then compute the epsilon closure
// 3. after reading of the character in put, check if any current state is an accpeting state 


public class Evaluator {
    
}
