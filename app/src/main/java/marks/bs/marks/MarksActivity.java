package marks.bs.marks;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import marks.bs.datamodels.Data;
import marks.bs.datamodels.Mark;

public class MarksActivity extends Activity implements View.OnClickListener {
    private TableLayout tableLayout;
    private Button back;
    private Data data;
    private Bundle bundle;
    private Button addMark;
    private ArrayList<Mark> marks;
    private RelativeLayout addMarkLayout;
    private Button closeAddMark;
    private Button addMarkSubmit;
    private EditText mark;
    private EditText maxMark;
    private EditText description;
    private DataTableRow rowEditing;
    private Mark markEditing;
    private String currentSubject;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog.Builder viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        init();

        showAddMark(false);

        addMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkSubmit.setText(R.string.add_mark);
                showAddMark(true);
            }
        });

        closeAddMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAddMarkLayout();
                showAddMark(false);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    data.saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        addMarkSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mark mark = getNewMark();
                if (!((Button)v).getText().toString().equals(getString(R.string.edit))) {
                    if (mark != null) {
                        addMark(mark);
                        showMark(mark);
                        clearAddMarkLayout();
                        try {
                            data.saveData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if(mark != null) {
                        editMark(markEditing, mark);
                        modifyRowWithNewData(rowEditing, getNewMark());
                        showAddMark(false);
                        clearAddMarkLayout();
                        try {
                            data.saveData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        tableLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        addTableHeader();

        showMarks();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            data.saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(addMarkLayout.getVisibility() == View.VISIBLE) {
            addMarkLayout.setVisibility(View.INVISIBLE);
            clearAddMarkLayout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(final View v) {
        String info = new String();
        Mark m = ((DataTableRow) v.getParent()).getMark();
        int percents = (int) Math.round(((float)m.getMark() / (float)m.getMaxMark()) * 100.0);
        info += m.getMark() + "/" + m.getMaxMark() + " (" + percents + "% = "
                + countGrade(percents) + ")" ;
        info += "\n" + m.getDescription();
        dialogBuilder.setMessage(info);
        dialogBuilder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rowEditing = (DataTableRow) v.getParent();
                markEditing = rowEditing.getMark();
                addMarkSubmit.setText(R.string.edit);
                mark.setText(Integer.toString(markEditing.getMark()));
                maxMark.setText(Integer.toString(markEditing.getMaxMark()));
                description.setText(markEditing.getDescription());
                showAddMark(true);
            }
        });

        dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMark(((DataTableRow) v.getParent()).getMark());
                ((ViewGroup) ((View) v.getParent()).getParent()).removeView((DataTableRow) v.getParent());
                showAddMark(false);
                clearAddMarkLayout();
            }
        });
        dialogBuilder.show();
    }


    private void init() {
        data = Data.getInstance(SubjectChooseActivity.filesDir);

        addMark = (Button) findViewById(R.id.add_mark);
        back = (Button) findViewById(R.id.back);
        addMarkSubmit = (Button) findViewById(R.id.add_mark_submit);
        closeAddMark = (Button) findViewById(R.id.add_mark_close);

        mark = (EditText) findViewById(R.id.your_mark);
        maxMark = (EditText) findViewById(R.id.max_mark);
        description = (EditText) findViewById(R.id.description_mark);

        tableLayout = (TableLayout) findViewById(R.id.table);
        addMarkLayout = (RelativeLayout) findViewById(R.id.add_mark_layout);

        bundle = getIntent().getExtras();
        currentSubject = bundle.getString("subject");

        marks = data.getMarks(currentSubject);

        dialogBuilder = new AlertDialog.Builder(this);
        viewDialog = new AlertDialog.Builder(this);

    }


    private void addTableHeader() {
        float textSize = 16;

        TableRow row = new TableRow(this);

        TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setText(R.string.mark);
        textView.append("    ");
        row.addView(textView, 0);

        textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setText(R.string.max_mark);
        textView.append("    ");
        row.addView(textView, 1);

        textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setText(R.string.description);
        row.addView(textView, 2);

        row.setBackgroundColor(Color.LTGRAY);

        tableLayout.addView(row);

    }


    private void clearAddMarkLayout() {
        mark.setText("");
        maxMark.setText("");
        description.setText("");
        showAddMark(false);
    }


    private void showMarks() {
        for (Mark tempMark : marks) {
            tableLayout.addView(createRow(tempMark));
        }
    }


    private void showMark(Mark mark) {
        tableLayout.addView(createRow(mark));

    }


    private DataTableRow createRow(Mark mark) {
        float textSize = 14;

        DataTableRow row;
        TextView text;
        row = new DataTableRow(this);

        int percents = (int) Math.round(((float)mark.getMark() / (float)mark.getMaxMark()) * 100.0);

        text = new TextView(this);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setOnClickListener(this);
        text.setTextSize(textSize);
        text.setText(Integer.toString(mark.getMark()));
        text.append("(" + percents + "% = " + countGrade(percents) + ")");
        row.addView(text, 0);

        text = new TextView(this);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setOnClickListener(this);
        text.setTextSize(textSize);
        text.setText(Integer.toString(mark.getMaxMark()));
        row.addView(text, 1);

        text = new TextView(this);
        text.setSingleLine(false);
        text.setOnClickListener(this);
        text.setTextSize(textSize);
        text.setText(mark.getDescription());
        row.addView(text, 2);
        row.setBackground(getResources().getDrawable(R.drawable.table_row_background));
        row.setPadding(2, 2, 2, 2);

        row.setMark(mark);

        return row;
    }


    private DataTableRow modifyRowWithNewData(DataTableRow row, Mark newMark) {
        float textSize = 14;

        TextView text;
        row.removeAllViews();

        int percents = (int) Math.round(((float)newMark.getMark() / (float)newMark.getMaxMark()) * 100.0);

        text = new TextView(this);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setOnClickListener(this);
        text.setOnClickListener(this);
        text.setTextSize(textSize);
        text.setText(Integer.toString(newMark.getMark()));
        text.append("(" + percents + "% = " + countGrade(percents) + ")");
        row.addView(text, 0);

        text = new TextView(this);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setOnClickListener(this);
        text.setOnClickListener(this);
        text.setTextSize(textSize);
        text.setText(Integer.toString(newMark.getMaxMark()));
        row.addView(text, 1);

        text = new TextView(this);
        text.setOnClickListener(this);
        text.setOnClickListener(this);
        text.setTextSize(textSize);
        text.setText(newMark.getDescription());
        row.addView(text, 2);
        row.setBackground(getResources().getDrawable(R.drawable.table_row_background));
        row.setPadding(2, 2, 2, 2);

        row.setMark(newMark);

        return row;
    }


    private void addMark(Mark mark) {
        data.addMark(currentSubject, mark);
    }


    private void deleteMark(Mark mark) {
        data.deleteMark(currentSubject, mark);
    }


    private void editMark(Mark oldMark, Mark newMark) {
        data.editMark(currentSubject, oldMark, newMark);
    }


    private Mark getNewMark() {
        String mark = this.mark.getText().toString();
        String maxMark = this.maxMark.getText().toString();
        String description = this.description.getText().toString();

        if(mark.isEmpty() || maxMark.isEmpty()) {
            LinearLayout linearLayout = new LinearLayout(this);
            TextView textView = new TextView(this);
            textView.setText(R.string.toast_add_mark);
            textView.setBackground(getResources().getDrawable(R.drawable.toast_white_style));
            textView.setTextSize(14);
            textView.setPadding(10, 10, 10, 10);
            linearLayout.addView(textView);

            Toast toast = new Toast(this);
            toast.setView(linearLayout);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            return null;
        } else {
            double dMark = Double.parseDouble(mark);
            double dMaxMark = Double.parseDouble(maxMark);
            return new Mark((int)Math.round(dMark), (int)Math.round(dMaxMark), description);
        }
    }


    private void showAddMark(boolean isShowed) {
        addMarkLayout.setVisibility(isShowed ? View.VISIBLE : View.INVISIBLE);
    }


    private int countGrade(int percents) {
        if(percents >= 90) {
            return 5;
        } else if(percents >= 75) {
            return 4;
        } else if(percents >= 60) {
            return 3;
        } else if(percents >= 35) {
            return 2;
        } else {
            return 1;
        }

    }

}

