package binaryGA;

//@author sasha
import java.util.ArrayList;

public class BinaryFitnessFunction {

    //Variables
    private final int rulesPerIndividual;
    private int ruleLength;
    private ArrayList<BinaryRule> ruleSet;

    //Constructors
    public BinaryFitnessFunction(ArrayList data, int rulesPerIndividual, int ruleLength) {
        this.rulesPerIndividual = rulesPerIndividual;
        this.ruleLength = ruleLength;
        this.ruleSet = new ArrayList<BinaryRule>();
        //Creates array of rules from raw data
        this.ruleSet = createRuleSet(data);
    }

    //Methods
    public ArrayList<BinaryRule> getRuleSet() {
        return ruleSet;
    }

    //Evaluates an entire populations fitness
    public void evaluatePopulation(BinaryPopulation population) {
        for (int i = 0; i < population.getPopulationSize(); i++) {
            fitnessFunction(population.getIndividual(i));
        }
    }

    //Evaluates an individuals fitness
    public void evaluateIndividual(BinaryIndividual individual) {
        fitnessFunction(individual);
    }

    //Calcualte fitness for a given individual
    private void fitnessFunction(BinaryIndividual individual) {

        int fitness = 0;

        //Get the data for comparison
        BinaryRule[] individualsRules = individual.getRules();

        for (int i = 0; i < ruleSet.size(); i++) {
            BinaryRule rule = ruleSet.get(i);

            //Compare individuals rule to ruleSet
            for (int j = 0; j < individualsRules.length; j++) {

                //If antecedents match check output
                if (compareAntecedents(individualsRules[j].condition, rule.condition)) {

                    //If consequents also match increase fitness
                    if (individualsRules[j].output == rule.output) {
                        fitness++;
                    }
                    break;
                }
            }
        }

        //Set individuals fitness
        individual.setFitness(fitness);
    }

    //Compares an individuals condition to a rules condition,
    //returns true if equal
    private boolean compareAntecedents(char[] individuals, char[] ruleSet) {
        for (int i = 0; i < ruleLength - 1; i++) {
            if (individuals[i] != ruleSet[i] && individuals[i] != '#') {
                return false;
            }
        }
        return true;
    }

    //Creates an ArrayList of rules from the input data,
    private ArrayList createRuleSet(ArrayList data) {

        ArrayList tmp;
        String antecedent;
        String consequent;

        for (Object obj : data) {
            tmp = (ArrayList) obj;

            antecedent = tmp.get(0).toString();
            consequent = tmp.get(1).toString();

            //Add new rule to set
            ruleSet.add(new BinaryRule(antecedent.toCharArray(), consequent.charAt(0)));
        }

        return ruleSet;
    }

}// End BinaryFitnessFunction
