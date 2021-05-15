package exercise.find.roots;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SuccessActivity extends AppCompatActivity {

    private TextView rootsView, secondsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        rootsView = findViewById(R.id.rootsView);
        secondsView = findViewById(R.id.secondsView);

        Intent callerIntent = getIntent();
        long originalNum = callerIntent.getLongExtra("original_number", -1);
        long root1 = callerIntent.getLongExtra("root1", -1);
        long root2 = callerIntent.getLongExtra("root2", -1);
        long calcTime = callerIntent.getLongExtra("calc_time", -1);

        rootsView.setText(getString(R.string.roots_print, originalNum, root1, root2));
        secondsView.setText(String.format(Locale.ENGLISH, "%d", calcTime));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("roots_text", rootsView.getText());
        outState.putCharSequence("calc_time_text", secondsView.getText());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rootsView.setText(savedInstanceState.getCharSequence("roots_text"));
        secondsView.setText(savedInstanceState.getCharSequence("calc_time_text"));
    }
}
