package pl.gorniak.audiogram2_0;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ExaminationActivity extends AppCompatActivity {

    final Integer[] frequencies = { 125, 250, 500, 1000, 1500, 2000, 3000, 4000, 6000,};
    final Double decibelsArray[] = {1.0, 3.16227766, 10.0, 31.6227766, 100.0, 316.227766, 1000.0, 3162.27766, 10000.0, 31622.7766};
    final Integer dbArray[] = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};

    final int duration = 10; //tone duration in seconds
    final int sampleRate = 44100;// sample rate, 441000 times per seconds
    final int numSamples = duration * sampleRate;
    final double samlples [] = new double[numSamples];
    int[] leftEar = new int[9];
    int[] rightEar = new int[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
    }

    public void onAnswerYes(View view) {
    }

    public void onAnswerNo(View view) {
    }

    public void onPlay(View view) {
    }

    public void showAlertDialogButtonClicked(View view) {

        int hearLossLeft;
        int hearLossRight;
        int hearLoss = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wynik badania");
        builder.setMessage("Twój ubytek słuchu wynosi: " + hearLoss + "dB");

        // add a button
        builder.setPositiveButton("Wykres Audiogram", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ExaminationActivity.this, ResultActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}