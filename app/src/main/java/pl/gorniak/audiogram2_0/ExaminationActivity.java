package pl.gorniak.audiogram2_0;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RenderNode;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class ExaminationActivity extends AppCompatActivity {

    AudioManager audioManager;
    final Integer[] frequencies = { 125, 250, 500, 1000, 1500, 2000, 3000, 4000, 6000, 10000};
    final Double decibelsArray[] = {1.0/*0dB*/, 3.16227766/*10dB*/, 10.0/*20dB*/, 31.6227766/*30dB*/, 100.0/*40dB*/, 316.227766/*50dB*/,
            1000.0/*60dB*/, 3162.27766/*70dB*/, 10000.0/*80dB*/, 31622.7766/*90dB*/};
    final Integer dbArray[] = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};

    final int duration = 1; //tone duration in seconds
    final int sampleRate = 44100;// sample rate, 441000 times per seconds
    final int numSamples = duration * sampleRate;
    private final double samples[] = new double[numSamples];
    final byte generatedSnd[] = new byte[2 * numSamples];

    int[] leftEar = new int[9];
    int[] rightEar = new int[9];
    float scale_factor = 0.03f;
    int i = 0;//index i
    int j = 0;//index j

    private TextView volumeTextView;
    private TextView frequencyTextView;
    private Button yesButton;
    private Button noButton;
    private Button playButton;
    private RadioButton rightRbutton;
    private RadioButton leftRbutton;
    private Button resultButton;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);

        volumeTextView = findViewById(R.id.volume_value);
        frequencyTextView = findViewById(R.id.frequency_value);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        playButton = findViewById(R.id.playButton);
        resultButton = findViewById(R.id.hearSound);
        rightRbutton = findViewById(R.id.rbuttonrigth);
        leftRbutton = findViewById(R.id.rbuttonleft);

        leftRbutton.setChecked(true);
        rightRbutton.setChecked(false);
        rightRbutton.setEnabled(false);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Badanie ubytku słuchu");
        builder.setMessage("W celu poprawnego przeprowadzenie badania, uprzednio należy dokonać kalibracji.\n");
        builder.setMessage("Przycisk Odtwórz generuje ton o danej częstotliwości oraz poziomie głośności.\n" +
                "Jeżeli usłyszysz ton - naciśnij przycisk TAK. \nW przeciwnym przypadku - naciśnij przycisk NIE \n" +
                "Można kilkukrotnie naciskać przycisk ODTWÓRZ jednak, zaleca się aby nie robić tego szybciej niż raz na 2 sekundy.");

        // add a button
        builder.setPositiveButton("Rozumiem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        updateText();
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genTone(500);
                playSound();
                //playButton.setEnabled(true);
            }
        });

    }

    private void updateText(){
        frequencyTextView.setText(String.valueOf(frequencies[i]));
        volumeTextView.setText(String.valueOf(dbArray[j]));
    }
    void genTone(int freqOfTone) {
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : samples) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 10));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }
    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
    public void showAlertDialogButtonClicked(View view) {

        int hearLossLeft;
        int hearLossRight;
        int hearLoss = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wynik badania");
        builder.setMessage("Twój ubytek słuchu wynosi: " + hearLoss + "dB");

        hearLossLeft = (leftEar[3]+leftEar[4]+leftEar[6])/3;
        hearLossRight = (rightEar[3]+rightEar[4]+rightEar[6])/3;

        if (hearLossLeft>hearLossRight){
            hearLoss = hearLossRight;
        }
        else{
            hearLoss = hearLossLeft;
        }
        // add a button
        builder.setPositiveButton("Wykres Audiogram", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(ExaminationActivity.this, ResultActivity.class);
                //startActivity(intent);
                //finish();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}