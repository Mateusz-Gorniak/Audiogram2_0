package pl.gorniak.audiogram2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AboutGraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_graph);
    }

    public void backToMainPage(View view) {
        Intent backToMainPage = new Intent(this, MainActivity.class);
        startActivity(backToMainPage);
        finish();
    }
}