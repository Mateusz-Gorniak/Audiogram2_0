package pl.gorniak.audiogram2_0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailTxv;
    private TextView fullname;
    private TextView location;
    private TextView phoneNumber;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        emailTxv = findViewById(R.id.emailtext);
        fullname = findViewById(R.id.name_surname);
        location = findViewById(R.id.locationtext);
        phoneNumber = findViewById(R.id.locationtext);


    }
}