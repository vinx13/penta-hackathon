import jm.midi.MidiSynth;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Write;

/**
 * Created by Vincent on 2016/11/26.
 */
public class Application {
    public static void main(String[] args) {
        SubjectGenerator generator = new SubjectGenerator();
        Part subjectPart = generator.generateSubjectPart();
        Score score = new Score();
        score.setDenominator(4);
        score.setNumerator(4);
        score.add(subjectPart);
        Write.midi(score, "awesome.midi");
    }
}
