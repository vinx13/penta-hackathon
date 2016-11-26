import jm.constants.RhythmValues;
import jm.music.data.Note;
import jm.music.data.Rest;

/**
 * Created by Vincent on 2016/11/26.
 */
public class SubjectReader {

    private int currentIndex, nextIndex;
    Subject subject;
    Bin[] vector;


    public SubjectReader() {
    }


    public SubjectReader(Subject subject) {
        setSubject(subject);
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.vector = subject.getVector();
        reset();
    }

    public void reset() {
        currentIndex = 0;
        nextIndex = 0;
    }

    private void analyze() {


        while (++nextIndex < 64) {
            Bin.BinStyle style = vector[nextIndex].getStyle();
            if (!(style == Bin.BinStyle.LINK ||
                    (style == Bin.BinStyle.REST && vector[nextIndex - 1].getStyle() == Bin.BinStyle.REST))
                    )
                break;
        }
    }

    public boolean next() {
        if (nextIndex >= 64) return false;
        currentIndex = nextIndex;
        analyze();
        return true;
    }

    public int getNoteLength() {
        return nextIndex - currentIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public int getNotePitch() {
        return vector[currentIndex].getPitch();
    }

    public Bin.NoteType getNoteType() {
        Bin.BinStyle style = vector[currentIndex].getStyle();
        if (style == Bin.BinStyle.REST) return Bin.NoteType.Rest;
        return Bin.NoteType.Normal;
    }

    public Note getNote() {
        Note note;
        if(getNoteType()== Bin.NoteType.Normal) {
            note = new Note(getNotePitch(), getNoteDuration());
        } else {
            note = new Rest(getNoteDuration());
        }
        return note;
    }

    public int getBarIndex(int binIndex) {
        return binIndex / 16;
    }

    public int getCurrentBarIndex() {
        return getBarIndex(currentIndex);
    }

    public double getNoteDuration () {
        return getNoteLength() / 4.0; // number of beats
    }
}
