import GA.Chromosome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jm.music.data.Part;
import jm.music.data.Phrase;

import GA.*;

/**
 * Created by Vincent on 2016/11/26.
 */
public class Subject implements Chromosome<Subject>, Cloneable {
    private static final Random random = new Random();
    private final Bin[] vector;

    public Subject() {
        vector = new Bin[64];
        for (int i = 0; i < 64; i++) {
            Bin bin = new Bin();
            bin.setValue(random.nextInt());
            vector[i] = bin;
        }
    }

    /**
     * Returns clone of current chromosome, which is mutated a bit
     */
    @Override
    public Subject mutate() {
        Subject result = this.clone();

        // just select random element of vector
        // and increase or decrease it on small value
        int index = random.nextInt(this.vector.length);
        int mutationValue = random.nextInt();
        result.vector[index].setValue(mutationValue); // perform bin level mutations
        return result;
    }

    /**
     * Returns list of siblings <br/>
     * Siblings are actually new chromosomes, <br/>
     * created using any of crossover strategy
     */
    @Override
    public List<Subject> crossover(Subject other) {
        Subject thisClone = this.clone();
        Subject otherClone = other.clone();

        // one point crossover
        int index = random.nextInt(this.vector.length - 1);
        for (int i = index; i < this.vector.length; i++) {
            Bin tmp = thisClone.vector[i];
            thisClone.vector[i] = otherClone.vector[i];
            otherClone.vector[i] = tmp;
        }

        return Arrays.asList(thisClone, otherClone);
    }

    @Override
    protected Subject clone() {
        Subject clone = new Subject();
        System.arraycopy(this.vector, 0, clone.vector, 0, this.vector.length);
        return clone;
    }

    public Bin[] getVector() {
        return this.vector;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.vector);
    }

    Phrase toPhrase() {
        Part part = new Part();
        Phrase phrase = new Phrase(0.0);
        SubjectReader reader = new SubjectReader(this);

        while (reader.next()) {
            phrase.add(reader.getNote());
        }
        return phrase;
    }

}