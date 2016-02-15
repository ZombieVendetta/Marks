package marks.bs.datamodels;

import android.widget.TableRow;

import java.io.Serializable;

import marks.bs.marks.MarksActivity;

/**
 * Created by Богдан on 01.02.2016.
 */
public class Mark implements Serializable {
    private int mark;
    private int maxMark;
    private String description;

    public Mark(int mark, int maxMark, String description) {
        this.mark = mark;
        this.maxMark = maxMark;
        this.description = description;
    }

    public int getMark() {
        return mark;
    }

    public int getMaxMark() {
        return maxMark;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "mark=" + mark +
                ", maxMark=" + maxMark +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mark mark1 = (Mark) o;

        if (mark != mark1.mark) return false;
        if (maxMark != mark1.maxMark) return false;
        return description.equals(mark1.description);

    }

    @Override
    public int hashCode() {
        int result = mark;
        result = 31 * result + maxMark;
        result = 31 * result + description.hashCode();
        return result;
    }
}
