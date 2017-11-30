package binaryGA;

// @author sasha

public class BinaryRule {

    //Variables
    char[] condition;
    char output;
    
    //Constructors
    public BinaryRule(char[] condition, char consequent) {
        this.condition = condition;
        this.output = consequent;
    }
       
    //Methods
    public char[] getAntecedent() {
        return condition;
    }

    public char getConsequent() {
        return output;
    }

    @Override
    public String toString() {
        
        String result = "";
        for (int i = 0; i < condition.length; i++) {
            result += condition[i];
        }
        
        result += " " + output;
        
        return result; 
    }

}//End BinaryRule
