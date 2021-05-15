package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiverForSuccess = null;
    private BroadcastReceiver broadcastReceiverForFailure = null;
    private ProgressBar progressBar;
    private EditText editTextUserInput;
    private Button buttonCalculateRoots;
    private boolean calculatingRoots, validNumAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        editTextUserInput = findViewById(R.id.editTextInputNumber);
        buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

        // set initial UI:
        progressBar.setVisibility(View.GONE); // hide progress
        editTextUserInput.setText(""); // cleanup text in edit-text
        editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
        buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

        // Set calculatingRoots status to false
        calculatingRoots = false;

        // set listener on the input written by the keyboard to the edit-text
        editTextUserInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                // text did change
                String newText = editTextUserInput.getText().toString();
                validNumAvailable = TextUtils.isDigitsOnly(newText);
                buttonCalculateRoots.setEnabled(validNumAvailable && !calculatingRoots);
            }
        });

        // set click-listener to the button
        buttonCalculateRoots.setOnClickListener(v -> {
            try {
                Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
                String userInputString = editTextUserInput.getText().toString();
                long userInputLong = Long.parseLong(userInputString);
                if (userInputLong <= 0) {
                    return;
                }

                intentToOpenService.putExtra("number_for_service", userInputLong);
                startService(intentToOpenService);

                // Set calculatingRoots status to true
                calculatingRoots = true;

                // set views states according to the spec (below)
                progressBar.setVisibility(View.VISIBLE);
                editTextUserInput.setEnabled(false);
                buttonCalculateRoots.setEnabled(false);
            } catch (NumberFormatException ignored) {
            }
        });

        // register a broadcast-receiver to handle action "found_roots"
        broadcastReceiverForSuccess = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent incomingIntent) {
                if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots"))
                    return;
                // success finding roots!

                // Set calculatingRoots status to false
                calculatingRoots = false;

                // set views states according to the spec (below)
                progressBar.setVisibility(View.GONE);
                editTextUserInput.setEnabled(true);
                buttonCalculateRoots.setEnabled(validNumAvailable);

                // Extract roots and the rest from the intent
                long originalNum = incomingIntent.getLongExtra("original_number", -1);
                long root1 = incomingIntent.getLongExtra("root1", -1);
                long root2 = incomingIntent.getLongExtra("root2", -1);
                long calcTime = incomingIntent.getLongExtra("calc_time", -1);

                // Create new intent and open success activity
                Intent successIntent = new Intent(MainActivity.this, SuccessActivity.class);
                successIntent.putExtra("original_number", originalNum);
                successIntent.putExtra("root1", root1);
                successIntent.putExtra("root2", root2);
                successIntent.putExtra("calc_time", calcTime);
                startActivity(successIntent);
            }
        };
        registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));

        // register a broadcast-receiver to handle action "stopped_calculations"
        broadcastReceiverForFailure = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent incomingIntent) {
                if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations"))
                    return;
                // failure finding roots!

                // Set calculatingRoots status to false
                calculatingRoots = false;

                // set views states according to the spec (below)
                progressBar.setVisibility(View.GONE);
                editTextUserInput.setEnabled(true);
                buttonCalculateRoots.setEnabled(validNumAvailable);

                // Extract roots and the rest from the intent
                long abortTime = incomingIntent.getLongExtra("time_until_give_up_seconds", -1);

                // Show a toast
                Toast.makeText(MainActivity.this, getString(R.string.abort_print, abortTime), Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(broadcastReceiverForFailure, new IntentFilter("stopped_calculations"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiverForSuccess);
        this.unregisterReceiver(broadcastReceiverForFailure);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("edit_text", editTextUserInput.getText());
        outState.putBoolean("valid_num", validNumAvailable);
        outState.putBoolean("calc_roots", calculatingRoots);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Get data back
        editTextUserInput.setText(savedInstanceState.getCharSequence("edit_text"));
        validNumAvailable = savedInstanceState.getBoolean("valid_num");
        calculatingRoots = savedInstanceState.getBoolean("calc_roots");

        // Set UI
        progressBar.setVisibility(calculatingRoots ? View.VISIBLE : View.GONE);
        editTextUserInput.setEnabled(!calculatingRoots);
        buttonCalculateRoots.setEnabled(validNumAvailable && !calculatingRoots);
    }
}