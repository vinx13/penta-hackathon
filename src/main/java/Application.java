import com.sun.tools.corba.se.idl.constExpr.Not;
import jm.constants.RhythmValues;
import jm.midi.MidiParser;
import jm.midi.MidiSynth;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Write;

import javax.sound.midi.spi.MidiFileReader;
import java.util.Random;

/**
 * Created by Vincent on 2016/11/26.
 */
public class Application {
    private static Random random = new Random();

    public static void main(String[] args) {
        SubjectGenerator generator = new SubjectGenerator();
        Part soprano = new Part(), alto = new Part(), bass = new Part();

        Phrase subject = generator.generateSubject();
        Phrase answer = generateAnswer(subject);
        Phrase counter1 = generateCounterSubject(subject),counter2=generateCounterSubject(subject);
        Phrase counter1Repeat = counter1.copy();
        Phrase subjectRepeat = subject.copy();

        final double FIRST = 0.0, SECOND=8.0,THIRD=16.0;
        subject.setStartTime(FIRST);
        soprano.add(subject);
        counter1.setStartTime(SECOND);
        soprano.add(counter1);
        counter2.setStartTime(THIRD);
        soprano.add(counter2);


        answer.setStartTime(SECOND);
        alto.add(answer);
        counter1Repeat.setStartTime(THIRD);
        alto.add(counter1Repeat);


        subjectRepeat.setStartTime(THIRD);
        bass.add(subjectRepeat);

        Score score = new Score();

        score.setDenominator(4);
        score.setNumerator(4);
        score.add(soprano);
        score.add(alto);
        score.add(bass);
        Write.midi(score, "awesome.midi");
    }

    public static Phrase generateAnswer(Phrase subject) {
        Phrase answer = new Phrase();
        Note[] notes = subject.getNoteArray();
        for (Note note : notes) {
            Note newNote = note.copy();
            newNote.setPitch(newNote.getPitch() - 7);
            answer.add(newNote);
        }
        return answer;
    }

    public static Phrase generateCounterSubject(Phrase subject) {

        Phrase counter1 = new Phrase();
        Note[] notes = subject.getNoteArray();
        for (int i = 0; i < notes.length; i++) {
            if (notes[i].getDuration() == RhythmValues.EN && i < notes.length - 1 && notes[i + 1].getDuration() == RhythmValues.EN) {
                if (false/*random.nextBoolean()*/) {
                    counter1.add(mergeNote(notes[i], notes[i + 1]));
                } else {
                    counter1.addNoteList(splitNote(notes[i]));
                    counter1.addNoteList(splitNote(notes[i + 1]));
                }
                i++;

            } else if (notes[i].getDuration() == RhythmValues.QN) {
                counter1.addNoteList(splitNote(notes[i]));
            } else{
                counter1.addNote(notes[i].copy());
            }

        }
        return transpose(counter1, 7);

    }

    private static Note mergeNote(Note note1, Note note2) {
        return new Note(note1.getPitch(), note1.getRhythmValue() + note2.getRhythmValue());
    }

    private static Note[] splitNote(Note note) {
        Note[] notes = new Note[2];
        double rhythm = note.getRhythmValue() / 2.0;
        notes[0] = new Note(note.getPitch(), rhythm);
        notes[1] = new Note(note.getPitch() + (random.nextInt(12)-6), rhythm);
        return notes;
    }

    private static Phrase transpose(Phrase phrase, int delta) {
        Phrase result = new Phrase();
        for (Note note : phrase.getNoteArray()) {
            result.addNote(note.getPitch() + delta, note.getRhythmValue());
        }
        return result;
    }
}
