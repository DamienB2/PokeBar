package com.example.pokebar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pokebar.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private String pokemonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bars");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String Name = String.valueOf(dataSnapshot.child("BarName").getValue());
                    String Desc = String.valueOf(dataSnapshot.child("BarDescription").getValue());
                    String PokemonID = String.valueOf(dataSnapshot.child("Pokemon").getValue());
                    String Latitude = String.valueOf(dataSnapshot.child("Latitude").getValue());
                    double Lat = Double.parseDouble(Latitude);
                    String Longitude = String.valueOf(dataSnapshot.child("Longitude").getValue());
                    double Long = Double.parseDouble(Longitude);

                    mMap = googleMap;


                    //La demande est trop lente ce qui fait que pokemon name arrive null.
                    getBarPokemon(PokemonID);
                    System.out.println(pokemonName);
                    Name = Name + " | " + pokemonName;


                    LatLng marker = new LatLng(Lat, Long);
                    mMap.addMarker(new MarkerOptions().position(marker).title(Name).snippet(Desc));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                    mMap.getMaxZoomLevel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getBarPokemon(String pokemonID) {

        RequestQueue queue = Volley.newRequestQueue(this);


        String url = "https://pokeapi.co/api/v2/pokemon/"+pokemonID;


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jObject = new JSONObject(response);

                            //get the pokemon name
                            String requestName = jObject.getString("name");
                            System.out.println(pokemonName);
                            pokemonName = requestName;

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
}