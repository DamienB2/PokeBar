package com.example.pokebar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;

public class POTD extends AppCompatActivity {
    TextView POTDName;
    ImageView PokeballImage;
    String IDPOTD;
    String[] pokemonName = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potd);

        POTDName = findViewById(R.id.POTDName);
        POTDName.setVisibility(View.INVISIBLE);
        PokeballImage = findViewById(R.id.POTDPokeball);


        PokeballImage.animate()
                .setDuration(2000)
                .rotationBy(720f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        makeEveryThingVisible();
                    }
                })
                .start();


    }

    private void makeEveryThingVisible() {

        getIdPOTD();
        
        POTDName.setVisibility(View.VISIBLE);

    }

    private void getIdPOTD() {


        FirebaseDatabase.getInstance().getReference("POTD").child("IDPOTD").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                IDPOTD = String.valueOf(snapshot.getValue());
                changeImageToPOTD();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void changeImageToPOTD() {

        RequestQueue queue = Volley.newRequestQueue(this);


        String url = "https://pokeapi.co/api/v2/pokemon/"+IDPOTD;


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);

                            //get the pokemon name
                            String requestName = jObject.getString("name");
                            requestName = requestName.toUpperCase();
                            POTDName.setText("It's " + requestName);


                            String ImageUrl;
                            //get the pokemon image link
                            JSONObject PokemonSprites = jObject.getJSONObject("sprites");
                            JSONObject SpritesOther = PokemonSprites.getJSONObject("other");
                            JSONObject SpritesOfficial = SpritesOther.getJSONObject("official-artwork");
                            ImageUrl = SpritesOfficial.getString("front_default");

                            System.out.println(ImageUrl);

                            linkToImage(ImageUrl);


                        }catch(Exception e) {
                            System.out.println("Unable to get request result");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void linkToImage(String imageUrl) {

        //this part place the image in the imageView with the link
        int maxWidth = 1000;
        int maxHeight = 1000;
        ImageRequest imageRequest = new ImageRequest(
                imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        PokeballImage.setImageBitmap(response);
                    }
                },
                maxWidth,
                maxHeight,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(POTD.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(imageRequest);
    }
}