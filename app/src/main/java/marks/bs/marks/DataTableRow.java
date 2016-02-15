package marks.bs.marks;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;

import marks.bs.datamodels.Mark;

/**
 * Created by Богдан on 02.02.2016.
 */
public class DataTableRow extends TableRow {
    private Mark mark;

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public DataTableRow(Context context) {

        super(context);
    }

    public DataTableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
