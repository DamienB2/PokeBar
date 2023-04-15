package com.example.pokebar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView QrText;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    LinearLayout pokedexLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        QrText = findViewById(R.id.Text_qrcode);

        //This part is for the navigation menu.
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        //this part is for the pokedex
        pokedexLayout = findViewById(R.id.PokedexContainer);
        final String[] pokemonName = new String[1];
        final String[] pokemonType = new String[1];


        //for the moment, show all 150 pokemons
        /*for (int i = 1; i <= 150; i++) {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://pokeapi.co/api/v2/pokemon/"+i;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObject = new JSONObject(response);

                                JSONArray types = jObject.getJSONArray("types");
                                JSONObject entry = types.getJSONObject(0);
                                JSONObject type = entry.getJSONObject("type");
                                String requestType = type.getString("name");
                                pokemonType[0] = requestType;


                                //get the pokemon name
                                String requestName = jObject.getString("name");
                                pokemonName[0] = requestName;


                                //get the pokemon image link
                                JSONObject PokemonSprites = jObject.getJSONObject("sprites");
                                JSONObject SpritesOther = PokemonSprites.getJSONObject("other");
                                JSONObject SpritesOfficial = SpritesOther.getJSONObject("official-artwork");
                                String ImageUrl = SpritesOfficial.getString("front_default");


                                //get the pokemon id
                                String requestId = jObject.getString("id");

                                addPokemon(pokemonName,pokemonType, ImageUrl,requestId);


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

        }*/
    }


    //adding each pokemon to the pokedex.
    private void addPokemon(String[] name, String[] type, String imageUrl, String id) {
        View view = getLayoutInflater().inflate(R.layout.pokemon_card, null);

        TextView pokemonNom = view.findViewById(R.id.textViewPokemonName);
        TextView pokemonType = view.findViewById(R.id.textViewPokemonType);
        TextView pokemonId = view.findViewById(R.id.textViewPokemonId);
        ImageView imageView = view.findViewById(R.id.imageViewPokemon);

        pokemonNom.setText("Name: "+name[0]);
        pokemonType.setText("Type: "+type[0]);
        pokemonId.setText("Id: "+id);


        //this part place the image in the imageView with the link
        int maxWidth = 1000;
        int maxHeight = 1000;
        ImageRequest imageRequest = new ImageRequest(
                imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                },
                maxWidth,
                maxHeight,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(imageRequest);

        pokedexLayout.addView(view);
    }


    //PopUp Window code
    private void CreatePopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.mainpopup,null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(drawerLayout, Gravity.CENTER, 0, 0);
            }
        });

        TextView GoBack;
        GoBack = popUpView.findViewById(R.id.GoBack);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }



    //This code is for the Navigation Bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_map:
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_POTD:
                CreatePopUpWindow();
                break;
            case R.id.nav_Logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
            case R.id.nav_settings:
                Intent intent2 = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_addBar:
                Intent intent3 = new Intent(MainActivity.this,addBarActivity.class);
                startActivity(intent3);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    //This code let the user press the back button to return to the main page
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }


    public void ScanCode(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up to set flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);

    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
        if(result.getContents() != null){
            Toast.makeText(this, "Vous avez capturé "+result.getContents()+" !", Toast.LENGTH_SHORT).show();
            QrText.setText("Vous avez rajouté "+result.getContents()+" à votre Pokédex !");

        }
    });








}