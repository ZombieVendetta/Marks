package marks.bs.marks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

import marks.bs.datamodels.Data;

public class SubjectChooseActivity extends Activity implements View.OnClickListener {
    private Intent intent;
    private RelativeLayout addLayout;
    private Button close;
    private Button addSubject;
    private ScrollView scrollView;
    private EditText subjectName;
    private Button button;
    private LinearLayout buttonLayout;

    private Set<String> subjects;
    private Data data;

    private float buttonHeight;
    private float buttonWidth;
    private float fontSize;

    private String subjEditing;
    private Button subjEditingView;

    public static String filesDir;

    private AlertDialog.Builder dialogBuilder;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_choose);

        init();

        showAdd(false);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdd(false);
                subjectName.setText("");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubject.setText(R.string.subj_add);
                showAdd(true);
            }
        });

        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((((Button)v).getText()).toString().equals(getString(R.string.edit))) {
                    data.editSubject(subjEditing, String.valueOf(subjectName.getText()));
                    subjEditingView.setText(String.valueOf(subjectName.getText()));
                    showAdd(false);
                    subjectName.setText("");
                } else {
                    if(!data.subjectExist(String.valueOf(subjectName.getText()))) {
                        data.addSubject(String.valueOf(subjectName.getText()));
                        addSubjectButton(String.valueOf(subjectName.getText()));
                        showAdd(false);
                        subjectName.setText("");
                    } else {
                        showToast(getString(R.string.toast_add_subject));
                    }
                }
            }

        });



        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();

        buttonHeight = 200 * (metrics.densityDpi / 160f);
        buttonWidth = 200 * (metrics.densityDpi / 160f);
        fontSize = 20 * (metrics.densityDpi / 160f);

        data = Data.getInstance(filesDir);
        subjects = data.getSubjects();

        showSavedSubjects();
    }

    @Override
    public void onBackPressed() {
        if(addLayout.getVisibility() == View.VISIBLE) {
            showAdd(false);
            subjectName.setText("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            data.saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra("subject", ((Button) v).getText());

        startActivity(intent);
    }


    private void showToast(String text) {
        LinearLayout linearLayout = new LinearLayout(this);
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackground(getResources().getDrawable(R.drawable.toast_white_style));
        textView.setTextSize(14);
        textView.setPadding(10, 10, 10, 10);
        linearLayout.addView(textView);

//        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        Toast toast = new Toast(this);
        toast.setView(linearLayout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    private void showSavedSubjects() {
        for (String s : subjects) {
            addSubjectButton(s);
        }
    }


    private void addSubjectButton(final String nameOfSubject) {
        final Button subject = new Button(this);

        subject.setHeight((int) buttonWidth);
        subject.setWidth((int) buttonHeight);
        subject.setBackground(getResources().getDrawable(R.drawable.white_style));

        subject.setText(nameOfSubject);
        subject.setTextSize(fontSize);
        subject.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        subject.setLayoutParams(layoutParams);

        subject.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Button temp = (Button) v;
                dialogBuilder.setMessage(temp.getText());
                dialogBuilder.setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjEditing = temp.getText().toString();
                        subjEditingView = temp;
                        addSubject.setText(R.string.edit);
                        showAdd(true);
                        subjectName.setText(temp.getText());
                    }
                });
                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {dialog.cancel();}
                    });
                dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSubject(String.valueOf(temp.getText()));
                        ((ViewGroup) temp.getParent()).removeView(temp);
                        showAdd(false);
                        subjectName.setText("");
                    }
                });
                dialogBuilder.show();
                return true;
            }
        });

        subject.setOnClickListener(this);

        buttonLayout.addView(subject);
    }


    private void init() {
        dialogBuilder = new AlertDialog.Builder(this);
        intent = new Intent(this, MarksActivity.class);
        addLayout = (RelativeLayout) findViewById(R.id.add_subject_layout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        close = (Button) findViewById(R.id.add_subject_close);
        addSubject = (Button) findViewById(R.id.add_subject_submit);
        button = (Button) findViewById(R.id.add_subject);
        buttonLayout = (LinearLayout) findViewById(R.id.linearButtonLayout);
        filesDir = this.getFilesDir().getAbsolutePath().toString();
        subjectName = (EditText) findViewById(R.id.subject_name);
    }


    private void deleteSubject(String name) {
        data.deleteSubject(name);
    }


    private void showAdd(boolean isShowed) {
        if(!isShowed)
            addLayout.setVisibility(View.INVISIBLE);
        else
            addLayout.setVisibility(View.VISIBLE);
    }
}
