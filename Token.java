public class Token {
    public enum Type{
        LITERAL, // a-z, A-Z, 0-9, space
        STAR, // *
        PLUS, // +
        ALTERNATION, // |
        LEFTBRACKET, // (
        RIGHTBRACKET, // )
    }

    // class members
    public Type type; // what kind of token 
    public char value; // need something to store LITERAL tokens as they can be different. 


    // constructor for literal tokens
    public Token (Type type, char value){
        this.type = type;
        this.value = value;
    }

    // constructor for non-literal tokens
    public Token (Type type){
        this.type = type;
        this.value = 0;
    }







}
