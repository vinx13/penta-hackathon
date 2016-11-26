import com.sun.tools.corba.se.idl.constExpr.Not;

/**
 * Created by Vincent on 2016/11/26.
 */

public class Evaluator implements GA.Fitness<Subject, Double> {

    private final int[] target = {10, 20, 30, 40, 50};
    private final boolean DEBUG = false;

    private SubjectReader reader = new SubjectReader();


    /*
    1. Similar pitch: To avoid the tune jumping around randomly between distant notes, each note is discouraged being different from its predecessor. There is no penalty for a difference of up to 2 whole steps away, but beyond that, the subject is penalized proportionally to the square of the difference between each note and its predecessor.

    2. Similar note length: This test compares a note’s length to the length of the previous note in the subject, and encourages them to be alike. This helps lend continuity to the piece by encouraging 16th or 8th note runs, or slower quarter notes to be grouped together, rather than the rhythm switching around arbitrarily.

    3. Power of 2: The reader.next test is whether a note’s length is a power of 2 or not. If it is, the subject is rewarded. The goal of this reward is to encourage more 16th, 8th, quarter, and half notes in a piece, rather than dotted eighth and dotted quarter notes. These other notes will still appear, but will be less common, helping to piece to sound more regular.

    4. Tie over a bar line: We discourage notes from being held over between measures (the last bin in a measure’s style tells us this). This helps keep the measures separate and distinct, as they should be.

    5. Notes start on 1st or 3rd bin of a beat: By encouraging notes that start on the 1st or 3rd bins of a beat, we encourage a regular beat throughout the subject. This keeps the subject from sounding syncopated or lost, because it tends to come back to eighth not boundaries (the first and third bins in each set of 4).

    6. In key: We reward notes for being in the key of C. This goes a long way toward making the subject sound better. It removes the majority of sharps and flats from the subject, making it much nicer to listen to.

    7. Not a rest: Since rests have none of the penalties of the normal notes (pitch being the main penalty), they tend to accumulate in subjects. This test rewards a subject for each bin that is not a rest, thus combating the penalties of the notes and keeping the subject from becoming overrun with rests.

    8. Note bonus: Because of the penalties that notes tend to receive, another pattern that pops up is that very long notes appear, often the entire length of a measure of more. This results in a rather boring subject. To fight this effect, we give a bonus to the subject for every note it contains. This helps to keep the average value of a note positive, and results in many more notes per fugue than when the average value of a note is negative.

    9. Out of range penalty: Some subjects would not have pitches differing from each other largely, but they would all end up changing in the same direction, which would result in the subject going very high, or very low. This test checks to see if the pitch of a note is outside of our predefined range, and penalizes it if it is.

     */
    @Override
    public Double calculate(Subject chromosome) {
        reader.setSubject(chromosome);
        Double[] fitnesses = new Double[9];
        fitnesses[0] = phase1(1.0);
        fitnesses[1] = phase2(1.0);
        fitnesses[2] = phase3(1.0);
        fitnesses[3] = phase4(1.0);
        fitnesses[4] = phase5(1.0);
        fitnesses[5] = phase6(1.0);
        fitnesses[6] = phase7(1.0);
        fitnesses[7] = phase8(1.0);
        fitnesses[8] = phase9(10000.0);
        double fitness = 0.0;
        for (int i = 0; i < 9; i++)
            fitness += fitnesses[i];

        return fitness;
    }

    double phase1(double factor) {
        // pitch
        reader.reset();
        int total = 0;
        int lastPitch = 0;
        while (reader.next()) {
            if (reader.getNoteType() == Bin.NoteType.Normal) {
                lastPitch = reader.getNotePitch();
                break;
            }
        }
        while (reader.next()) {
            if (reader.getNoteType() == Bin.NoteType.Normal) {
                int pitch = reader.getNotePitch();
                int diff = Math.abs(pitch - lastPitch);
                total += diff;
                lastPitch = pitch;
            }
        }
        double fitness = total * factor;
        print(1, fitness);
        return fitness;
    }

    double phase2(double factor) {
        // length
        reader.reset();
        int total = 0;
        int lastLength = 0;
        reader.next();
        lastLength = reader.getNoteLength();
        while (reader.next()) {
            int length = reader.getNoteLength();
            int diff = Math.abs(length - lastLength);
            total += diff;
            lastLength = length;
        }
        double fitness = total * factor;
        print(2, fitness);
        return fitness;
    }

    double phase3(double factor) {
        // encourage power of 2
        reader.reset();
        int total = 0;
        while (reader.next()) {
            if (reader.getNoteType() == Bin.NoteType.Normal) {
                int length = reader.getNoteLength();
                int power2 = ((length & (length - 1)) == 0) ? 1 : 0;
                total += power2;
            }
        }
        double fitness = -1 * total * factor;
        print(3, fitness);
        return fitness;
    }

    double phase4(double factor) {
        reader.reset();
        int total = 0;
        while (reader.next()) {
            if (reader.getBarIndex(reader.getCurrentIndex()) != reader.getBarIndex(reader.getNextIndex()) && reader.getNextIndex() % 16 != 0)
                ++total;
        }
        double fitness = total * factor;
        print(4, fitness);
        return fitness;
    }

    double phase5(double factor) {
        // encourage notes starting from 1st or 3rd bin
        reader.reset();
        int total = 0;
        while (reader.next()) {
            if (reader.getCurrentIndex() % 2 == 0)
                ++total;
        }
        double fitness = -1 * total * factor;
        print(5, fitness);
        return fitness;
    }

    double phase6(double factor) {
        // encourage notes in C
        reader.reset();
        int total = 0;
        while (reader.next()) {
            if (reader.getNoteType() == Bin.NoteType.Normal && reader.getCurrentIndex() % 12 == 0) {
                ++total;
            }
        }
        double fitness = -1 * total * factor;
        print(6, fitness);
        return fitness;
    }

    double phase7(double factor) {
        // discourage rest
        reader.reset();
        int total = 0;
        while (reader.next()) {
            if (reader.getNoteType() == Bin.NoteType.Rest) {
                ++total;
            }
        }
        double fitness = total * factor;
        print(7, fitness);
        return fitness;
    }

    double phase8(double factor) {
        // penalty on length
        reader.reset();
        int total = 0;
        while (reader.next()) {
            total += reader.getNoteLength();
        }
        double fitness = total * factor;
        print(8, fitness);
        return fitness;
    }

    double phase9(double factor) {
        reader.reset();
        int delta = 10;
        int cen = 66;
        int lo = cen - delta;
        int hi = cen + delta;
        int total = 0;
        while (reader.next()) {
            if (reader.getNoteType() == Bin.NoteType.Normal) {
                int pitch = reader.getNotePitch();
                if (pitch < lo || pitch > hi) ++total;
            }
        }
        double fitness = total * factor;
        print(9, fitness);
        return fitness;
    }


    void print(int phase, double fitness) {

        if (DEBUG)
            System.out.println(String.format("Fitness of phase %d: %f", phase, fitness));
    }
}

