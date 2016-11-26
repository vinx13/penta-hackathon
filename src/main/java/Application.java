import jm.midi.MidiSynth;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Write;

/**
 * Created by Vincent on 2016/11/26.
 */
public class Application {
    public static void main(String[] args) {
        SubjectGenerator generator = new SubjectGenerator();
        Phrase subject = generator.generateSubject();
        Phrase answer = generateAnswer(subject);
        Part soprano = new Part(), alto = new Part(), bass = new Part();
        soprano.add(subject);
        soprano.add(answer);
        Score score = new Score();

        score.setDenominator(4);
        score.setNumerator(4);
        score.add(soprano);
        score.add(alto);
        score.add(bass);
        Write.midi(score, "awesome.midi");
    }

    public static Phrase generateAnswer(Phrase subject) {
        Phrase answer = new Phrase(16.0);
        Note[] notes = subject.getNoteArray();
        for (Note note : notes) {
            Note newNote = note.copy();
            newNote.setPitch(newNote.getPitch() - 3);
            answer.add(newNote);
        }
        return answer;
    }
    Phrase generateCounterSubject(Phrase subject) {
        Phrase counter1 =  new Phrase(16.0);
        return null;

    }
}
