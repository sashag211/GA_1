package binaryGA;

//@author sasha
import java.util.Arrays;
import java.util.Random;

public class BinaryPopulation {

    //Variables
    private Random rng = new Random();

    private int populationSize;
    private int chromosomeLength;
    private int ruleLength;
    private BinaryIndividual fittestIndividual;
    private BinaryIndividual[] individuals;

    //Constructors
    public BinaryPopulation(int populationSize, int chromosomeLength, int ruleLength) {
        this.populationSize = populationSize;
        this.chromosomeLength = chromosomeLength;
        this.ruleLength = ruleLength;
        this.fittestIndividual = new BinaryIndividual();
        this.individuals = new BinaryIndividual[populationSize];
        //Initialise a new population
        initialise();
    }

    //Methods
    public int getPopulationSize() {
        return populationSize;
    }

    public int getChromosomeLength() {
        return chromosomeLength;
    }

    public BinaryIndividual[] getIndividuals() {
        return individuals;
    }

    public BinaryIndividual getIndividual(int index) {
        return individuals[index];
    }

    public BinaryIndividual getFittestIndividual() {
        return fittestIndividual;
    }

    public void setFittestIndividual(BinaryIndividual fittestIndividual) {
        this.fittestIndividual = fittestIndividual;
    }

    //Searches current population for the fittest individual
    public void findFittest() {

        fittestIndividual = new BinaryIndividual();

        for (int i = 0; i < populationSize; i++) {

            if (individuals[i].getFitness() > fittestIndividual.getFitness()) {
                fittestIndividual = individuals[i];
            }
        }

        setFittestIndividual(fittestIndividual);
    }

    //Replaces the least fit individual in the population with the fittest
    public void putFittest() {

        int lowestFitness = individuals[0].getFitness();
        int lowestFitnessIndex = 0;

        for (int i = 0; i < populationSize; i++) {

            if (individuals[i].getFitness() < lowestFitness) {
                lowestFitness = individuals[i].getFitness();
                lowestFitnessIndex = i;
            }
        }

        individuals[lowestFitnessIndex] = getFittestIndividual();
    }

    //Initialise a new population
    public void initialise() {

        for (int i = 0; i < populationSize; i++) {

            char[] tmpChromosome = new char[chromosomeLength];

            int consequentIndex = ruleLength - 1;
            for (int j = 0; j < chromosomeLength; j++) {

                //Check current index is not a rule consequent
                if (j != consequentIndex) {

                    //If current index is part of an antecedent,
                    //set the current gene to of the three possible values
                    int tmpRng = rng.nextInt(3);
                    if (tmpRng == 2) {
                        tmpChromosome[j] = '#';
                    } else {
                        tmpChromosome[j] = (tmpRng == 1) ? '1' : '0';
                    }
                    //Else set the consequent value to boolean
                } else {
                    tmpChromosome[j] = rng.nextBoolean() ? '1' : '0';
                    consequentIndex += ruleLength;
                }
            }

            //Add the new indiviudal to the population
            individuals[i] = new BinaryIndividual(tmpChromosome, ruleLength);
        }
    }

    //Selects the fittest of two random individuals from the population
    public void tournamentSelection() {

        BinaryIndividual[] newPopulation = new BinaryIndividual[populationSize];

        for (int i = 0; i < populationSize; i++) {

            BinaryIndividual selectedIndividual;

            //Select two candidates randomly
            int candidate1 = rng.nextInt(populationSize);
            int candidate2 = rng.nextInt(populationSize);

            //Select the fittest
            if (individuals[candidate1].getFitness() >= individuals[candidate2].getFitness()) {
                selectedIndividual = individuals[candidate1];
            } else {
                selectedIndividual = individuals[candidate2];
            }

            //Add selected to the new population
            newPopulation[i] = selectedIndividual;
        }

        this.individuals = newPopulation;
    }

    //Selects individuals with probability proportional to their fitness
    public void rouletteSelection() {

        BinaryIndividual[] newPopulation = new BinaryIndividual[populationSize];

        //Calculate total fitness
        int totalFitness = 0;
        for (int i = 0; i < populationSize; i++) {
            totalFitness += individuals[i].getFitness();
        }

        for (int i = 0; i < populationSize; i++) {

            BinaryIndividual selectedIndividual = new BinaryIndividual();

            //Choose a selection point between 0 and the total fitness
            int selectionPoint = rng.nextInt(totalFitness);

            int tmpFitness = 0;
            for (int j = 0; j < populationSize; j++) {

                //Sum fitness of consecutive individuals
                tmpFitness += individuals[j].getFitness();

                //If current sum matches selection point,
                //select individual at current index
                if (tmpFitness >= selectionPoint) {
                    selectedIndividual = individuals[j];
                    break;
                }
            }

            //Add selected to the new population
            newPopulation[i] = selectedIndividual;
        }

        this.individuals = newPopulation;
    }

    //Generates one child from two randomly selected parents,
    //inbreeding flag prevents two identical parents being selected if set
    public void oneChildCrossover(boolean inbreeding) {

        BinaryIndividual[] newPopulation = new BinaryIndividual[populationSize];

        for (int i = 0; i < populationSize; i++) {

            //Select two random parents
            BinaryIndividual parent1 = individuals[rng.nextInt(populationSize)];
            BinaryIndividual parent2 = individuals[rng.nextInt(populationSize)];

            //If inbreeding flag set, re-select while parents are identical
            if (!inbreeding) {
                while (isDuplicate(parent1, parent2)) {
                    parent2 = individuals[rng.nextInt(populationSize)];
                }
            }

            //Clone first parents genes to child
            BinaryIndividual offspring = new BinaryIndividual(parent1.getChromosome().clone(), ruleLength);

            //Select random crossover point
            int crossoverPoint = rng.nextInt(chromosomeLength);

            //Copy second parents genes into the childs chromosome,
            //from the crossover point onwards
            for (int j = crossoverPoint; j < chromosomeLength; j++) {
                offspring.getChromosome()[j] = parent2.getChromosome()[j];

            }

            //Add the child to the new population
            newPopulation[i] = offspring;
        }

        this.individuals = newPopulation;
    }

    //Generates two children from two consecutive parents 
    public void twoChildCrossover() {

        BinaryIndividual[] newPopulation = new BinaryIndividual[populationSize];

        for (int i = 0; i < populationSize; i += 2) {

            //Select two consecutive parents
            BinaryIndividual parent1 = individuals[i];
            BinaryIndividual parent2 = individuals[i + 1];

            //Clone both parents genes to their respective child
            BinaryIndividual offspring1 = new BinaryIndividual(parent1.getChromosome().clone(), ruleLength);
            BinaryIndividual offspring2 = new BinaryIndividual(parent2.getChromosome().clone(), ruleLength);

            //Select random crossover point
            int crossoverPoint = rng.nextInt(chromosomeLength);

            //Copy second parents genes into the first childs chromosome,
            //copy first parents genes into second childs chromosome,
            //from the crossover point onwards
            for (int j = crossoverPoint; j < chromosomeLength; j++) {
                offspring1.getChromosome()[j] = parent2.getChromosome()[j];
                offspring2.getChromosome()[j] = parent1.getChromosome()[j];
            }

            //Add children to the new population
            newPopulation[i] = offspring1;
            newPopulation[i + 1] = offspring2;
        }

        this.individuals = newPopulation;
    }

    //Mutates genes with probability of mutation rate
    public void mutation(double mutationRate) {

        for (int i = 0; i < populationSize; i++) {

            int consequentIndex = ruleLength - 1;
            for (int j = 0; j < chromosomeLength; j++) {

                //Determine if gene is to be mutated
                if (Math.random() <= mutationRate) {

                    //Check index is not a rule consequent
                    if (j != consequentIndex) {

                        //If current index is part of an antecedent,
                        //flip the current gene to of the two possible values
                        if (individuals[i].getChromosome()[j] == '1') {
                            individuals[i].getChromosome()[j] = (rng.nextBoolean()) ? '#' : '0';
                        } else if (individuals[i].getChromosome()[j] == '0') {
                            individuals[i].getChromosome()[j] = (rng.nextBoolean()) ? '#' : '1';
                        } else {
                            individuals[i].getChromosome()[j] = (rng.nextBoolean()) ? '1' : '0';
                        }
                        //Else flip the consequent value
                    } else if (j == consequentIndex) {

                        if (individuals[i].getChromosome()[j] == '1') {
                            individuals[i].getChromosome()[j] = '0';
                        } else {
                            individuals[i].getChromosome()[j] = '1';
                        }
                    }
                }
                //If current index is a consequent index 
                if (j == consequentIndex) {
                    consequentIndex += ruleLength;
                }

            }
        }
    }

    //Checks two parent candidates for equality,
    //returns false if not equal
    private boolean isDuplicate(BinaryIndividual parent1, BinaryIndividual parent2) {

        boolean result = true;
        
        //Get parents rules
        BinaryRule[] parent1Rules = parent1.getRules();
        BinaryRule[] parent2Rules = parent2.getRules();

        for (int i = 0; i < parent1Rules.length; i++) {

            //Get current rule
            BinaryRule parent1Rule = parent1Rules[i];
            BinaryRule parent2Rule = parent2Rules[i];

            //Compare rules
            if (!Arrays.equals(parent1Rule.getAntecedent(), parent2Rule.getAntecedent())
                    && parent1Rule.getConsequent() != parent2Rule.getConsequent()) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {

        String result = "";
        for (int i = 0; i < populationSize; i++) {
            result += individuals[i].toString();
            result += "\n";
        }
        return result;
    }

}// End BinaryPopulation
