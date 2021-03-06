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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExaminationActivity extends AppCompatActivity {

    private final static String USER = "users";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    LocalDateTime myDateObj = LocalDateTime.now();
    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    AudioManager audioManager;
    final Integer[] frequencies = { 125, 250, 500, 1000, 1500, 2000, 3000, 4000, 6000, 10000};
    final Double [] volumes = {1.0/*0dB*/, 3.16227766/*10dB*/, 10.0/*20dB*/, 31.6227766/*30dB*/, 100.0/*40dB*/, 316.227766/*50dB*/,
            1000.0/*60dB*/, 3162.27766/*70dB*/, 10000.0/*80dB*/, 31622.7766/*90dB*/};
    final Integer dbArray[] = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
    float scale_factor = 0.02f;
    final int duration = 1; //tone duration in seconds
    final int sampleRate = 44100;// sample rate, 441000 times per seconds
    final int numSamples = duration * sampleRate;
    private final double samples[] = new double[numSamples];
    final byte generatedSnd[] = new byte[2 * numSamples];
    int leftEarFlag = 1, rightEarFlag=0;
    int[] leftEar = new int[10];
    int[] rightEar = new int[10];
    int i = 0;//index i frequency
    int j = 0;//index j volume
    private String keyid;
    private TextView volumeTextView;
    private TextView frequencyTextView;
    private Button yesButton;
    private Button noButton;
    private Button playButton;
    private RadioButton rightRbutton;
    private RadioButton leftRbutton;
    private Button resultButton;
    Intent result;

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
        yesButton.setEnabled(false);
        noButton.setEnabled(false);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USER);
        keyid = auth.getUid();



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Badanie ubytku s??uchu");
        builder.setMessage("W celu poprawnego przeprowadzenie badania, uprzednio nale??y dokona?? kalibracji.\n");
        builder.setMessage("Przycisk Odtw??rz generuje ton o danej cz??stotliwo??ci oraz poziomie g??o??no??ci.\n" +
                "Je??eli us??yszysz ton - naci??nij przycisk TAK. \nW przeciwnym przypadku - naci??nij przycisk NIE \n" +
                "Mo??na kilkukrotnie naciska?? przycisk ODTW??RZ jednak, zaleca si?? aby nie robi?? tego szybciej ni?? raz na 2 sekundy.");

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

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesButton.setEnabled(false);
                noButton.setEnabled(false);
                genTone(frequencies[i], volumes[j]);
                playSound();
                yesButton.setEnabled(true);
                noButton.setEnabled(true);
                //playButton.setEnabled(true);
            }
        });



        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(leftEarFlag  == 1){
                    leftEar[i] = dbArray[j];
                }
                if(rightEarFlag == 1){
                    rightEar[i] = dbArray[j];
                }
                i++;
                j=0;
                if(frequencies[i] >= 10000 ){
                    if(leftEarFlag == 0){
                        noButton.setEnabled(false);
                        yesButton.setEnabled(false);
                        playButton.setEnabled(false);
                        resultButton.setEnabled(true);
                        resultButton.setVisibility(View.VISIBLE);

                    }
                    Toast.makeText(ExaminationActivity.this, "Maksymalna warto???? cz??stotliwo??ci", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ExaminationActivity.this,"Teraz nast??pi zmiana kana??u emisji d??wi??ku, tak aby zbada?? ucho prawe", Toast.LENGTH_SHORT).show();
                    leftRbutton.setChecked(false);
                    leftRbutton.setEnabled(false);
                    rightRbutton.setEnabled(true);
                    rightRbutton.setChecked(true);
                    i=0;
                    j=0;
                    leftEarFlag=0;
                    rightEarFlag=1;
                }
                updateText();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                j++;
                if(frequencies[i] > 6000 ){
                    if(leftEarFlag == 0) {
                        noButton.setEnabled(false);
                        yesButton.setEnabled(false);
                        playButton.setEnabled(false);
                        resultButton.setEnabled(true);
                        resultButton.setVisibility(View.VISIBLE);
                    }
                    if(rightEarFlag == 0){
                        Toast.makeText(ExaminationActivity.this,"Teraz nast??pi zmiana kana??u emisji d??wi??ku, tak aby zbada?? ucho prawe", Toast.LENGTH_SHORT).show();
                        leftRbutton.setChecked(false);
                        leftRbutton.setEnabled(false);
                        rightRbutton.setEnabled(true);
                        rightRbutton.setChecked(true);
                        i=0; j=0;
                        leftEarFlag=0;
                        rightEarFlag=1;
                    }
                }
                if(dbArray[j] >= 90){
                    if(leftEarFlag  == 1){
                        leftEar[i] = 90;
                    }
                    if(rightEarFlag == 1){
                        rightEar[i] = 90;
                    }

                    i++;
                    Toast.makeText(ExaminationActivity.this,"Maksymalny poziom d??wi??ku", Toast.LENGTH_SHORT).show();
                    j=0;
                }
                updateText();
            }
        });

    }

    private void updateText(){
        frequencyTextView.setText(String.valueOf(frequencies[i]));
        volumeTextView.setText(String.valueOf(dbArray[j]));
    }
    void genTone(int freqOfTone, double volume) {
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            samples[i] =  (Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone)));
        }

        int idx = 0;
        for (final double dVal : samples) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * volume));
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
        audioTrack.setStereoVolume(leftEarFlag,rightEarFlag);
        audioTrack.play();
    }
    public void showAlertDialogButtonClicked(View view) {

        int hearLossLeft;
        int hearLossRight;
        int hearLoss = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wynik badania");

        hearLossLeft = (leftEar[3]+leftEar[4]+leftEar[6])/3;
        hearLossRight = (rightEar[3]+rightEar[4]+rightEar[6])/3;


        if (hearLossLeft>hearLossRight){
            hearLoss = hearLossRight;
        }
        else{
            hearLoss = hearLossLeft;
        }

        myRef.child(keyid).child("lastExamination").setValue(2);
        String formattedDate = myDateObj.format(myFormatObj);
        Log.d("TAAG",keyid);
        Log.d("TAAG",formattedDate);
        myRef.child(keyid).child("lastExamination").setValue("Ubytek:" +hearLoss + "dB \nWykonano:" + formattedDate);
        //Log.d("TAG",leftEar[0] + " " +leftEar[1] + " " +leftEar[2] + " " + leftEar[3] + " " +leftEar[4] + " "  +leftEar[5] + " ");
        //Log.d("TAG",rightEar[0] + " " +rightEar[1] + " " +rightEar[2] + " " + rightEar[3] + " " +rightEar[4] + " "  +rightEar[5] + " ");


        builder.setMessage("Tw??j ubytek s??uchu wynosi: " + hearLoss + "dB");
        // add a button
        builder.setPositiveButton("Wykres Audiogram", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result = new Intent(getBaseContext(), GraphActivity.class);
                result.putExtra("leftEar", leftEar);
                result.putExtra("rightEar", rightEar);
                startActivity(result);
                finish();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}