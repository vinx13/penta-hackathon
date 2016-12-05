import java.util.*;

import GA.*;

/**
 * Created by Vincent on 2016/11/26.
 */
public class Bin {

    public enum BinStyle {
        LINK, REST, END
    }

    public enum NoteType {Normal, Rest}

    private static final Random random = new Random();

    private BitSet bits = new BitSet(9);

    public void setValue(int n) {
        for (int i = 0; i < 9; i++) {
            bits.set(i, n % 2 == 1);
            n >>= 1;
        }
    }

    public int getValue() {
        return toInt(0, 9);
    }

    public int getPitch() {
        int pitch = toInt(2, 9);
        //System.out.println(String.format("Pitch = %d", pitch));

        return pitch;
    }

    public BinStyle getStyle() {
        int style = toInt(0, 2);
        if (style <= 2) return BinStyle.LINK;
        //if (style == 2) return BinStyle.REST;
        if (style == 3) return BinStyle.END;
        return null;
    }

    private int toInt(int begin, int end) {
        int sum = 0;
        int pow = 1;
        for (int i = begin; i < end; i++) {
            int v = bits.get(i) ? 1 : 0;
            sum += v * pow;
            pow <<= 1;
        }
        return sum;
    }
    public String toString() {
        return bits.toString();
    }
}