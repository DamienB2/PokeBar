package com.example.pokebar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class addBarActivity extends AppCompatActivity {

    private EditText barName, barDescription, streetName, cityName, countryName;
    private Button addMyBar;

    private double doubleLat, doubleLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bar);

        barName = findViewById(R.id.editTextTextBarName);
        barDescription = findViewById(R.id.editTextTextBarDescription);
        streetName = findViewById(R.id.editTextStreet);
        cityName = findViewById(R.id.editTextTextCity);
        countryName = findViewById(R.id.editTextTextCountry);

        addMyBar = findViewById(R.id.btnAddBar);


        addMyBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = barName.getText().toString().trim();
                String Description = barDescription.getText().toString().trim();
                String address = streetName.getText().toString() + ", " + cityName.getText().toString() + ", " + countryName.getText().toString();
                int Pokemon = (int) (Math.random()*151);

                Geocoder geocoder = new Geocoder(addBarActivity.this);
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocationName(address, 1);

                    if(addressList != null){
                        doubleLat = addressList.get(0).getLatitude();
                        doubleLong = addressList.get(0).getLongitude();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Bar bar = new Bar(Name, Description, Pokemon, doubleLat, doubleLong);


                FirebaseDatabase.getInstance().getReference("Bars").push().setValue(bar).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    Toast.makeText(addBarActivity.this, "Your bar has been added to the map !", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(addBarActivity.this,DownloadPokemonActivity.class);
                                    intent.putExtra("ID",Integer.toString(Pokemon));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(addBarActivity.this, "Failed to add your bar...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}