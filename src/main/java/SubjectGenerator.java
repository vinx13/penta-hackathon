import GA.*;
import jm.constants.Pitches;
import jm.constants.RhythmValues;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import sun.jvm.hotspot.debugger.MachineDescriptionIntelX86;

/**
 * Created by Vincent on 2016/11/26.
 */
public class SubjectGenerator {
    private static final int MAX_ITERATION = 4000;

    public Phrase generateSubject() {

        Population<Subject> population = createInitialPopulation(30);
        Evaluator evaluator = new Evaluator();

        GeneticAlgorithm<Subject, Double> ga = new GeneticAlgorithm<Subject, Double>(population, evaluator);

        addListener(ga);

        ga.evolve(MAX_ITERATION);
        Subject subject = ga.getBest();
        evaluator.calculate(subject);
        return subject.toPhrase();

        /*
        Phrase subject = new Phrase();
        subject.add(new Note(Pitches.G4, RhythmValues.QN));
        subject.add(new Note(Pitches.BF4,RhythmValues.QN));
        subject.add(new Note(Pitches.A4,RhythmValues.QN));
        subject.add(new Note(Pitches.D4, RhythmValues.QN));

        subject.add(new Note(Pitches.A4,RhythmValues.EN));
        subject.add(new Note(Pitches.BF4,RhythmValues.EN));
        subject.add(new Note(Pitches.C5,RhythmValues.EN));
        subject.add(new Note(Pitches.A4, RhythmValues.EN));
        subject.add(new Note(Pitches.BF4, RhythmValues.EN));
        subject.add(new Note(Pitches.A4,RhythmValues.EN));
        subject.add(new Note(Pitches.G4,RhythmValues.QN));
        return subject;
        */
    }


    private Population<Subject> createInitialPopulation(int populationSize) {
        Population<Subject> population = new Population<Subject>();
        //Subject base = new Subject();
        for (int i = 0; i < populationSize; i++) {
            // each member of initial population
            // is mutated clone of base chromosome
            //Subject subject = base.mutate();
            population.addChromosome(new Subject());
        }
        return population;
    }

    /**
     * After each iteration Genetic algorithm notifies listener
     */
    private void addListener(GeneticAlgorithm<Subject, Double> ga) {
        // just for pretty print
        System.out.println(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));

        // Lets add listener, which prints best chromosome after each iteration
        ga.addIterationListener(new IterartionListener<Subject, Double>() {

            private final double threshold = 1e-5;

            @Override
            public void update(GeneticAlgorithm<Subject, Double> ga) {

                Subject best = ga.getBest();
                double bestFit = ga.fitness(best);
                int iteration = ga.getIteration();

                // Listener prints best achieved solution
                System.out.println(String.format("%s\t%s\t%s", iteration, bestFit, best));

                // If fitness is satisfying - we can stop Genetic algorithm
                if (bestFit < this.threshold) {
                    ga.terminate();
                }

            }
        });
    }

}
