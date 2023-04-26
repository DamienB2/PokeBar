package com.example.pokebar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadPokemonActivity extends AppCompatActivity {

    private TextView PokemonTitle;
    private ImageView imageView1, imageView2, imageView3;
    private Button QrPersoBtn, QrCodeBtn, GoMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_pokemon);

        PokemonTitle = findViewById(R.id.textViewPokemonDPTitle);
        imageView1 = findViewById(R.id.imageViewQrCode);
        imageView2 = findViewById(R.id.imageViewQrPokemonDP);
        imageView3 = findViewById(R.id.imageViewQrPokemonPerso);
        QrCodeBtn = findViewById(R.id.QrCodeBtn);
        QrPersoBtn = findViewById(R.id.QrPersoBtn);
        GoMapBtn = findViewById(R.id.BtnGoMap);


        Intent intent = getIntent();
        String PokemonID = intent.getStringExtra("ID");



        DisplayPokemonImageAndName(PokemonID);
        QrCodeGenerate(PokemonID);

        QrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadImage(imageView1);
            }
        });

        QrPersoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadImage(imageView3);
            }
        });

        GoMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DownloadPokemonActivity.this,MapsActivity.class));
            }
        });




    }

    private void DownloadImage(ImageView imageView) {

        //Obtenez la référence de l'image affichée dans ImageView
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        //Vérifiez si la permission d'écriture externe est accordée
        //Créez un nouveau fichier dans le stockage externe
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "image.jpg");
        try {
            //Copiez le contenu de l'image dans le nouveau fichier
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Image téléchargée avec succès", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors du téléchargement de l'image", Toast.LENGTH_SHORT).show();
        }
    }

    //cahnge this because it won't show customQRCODE first time
    public void QrCodePerso(View view) {
        // Capturer les images de chaque ImageView
        Bitmap image1 = Bitmap.createBitmap(imageView1.getWidth(), imageView1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(image1);
        imageView1.draw(canvas1);

        Bitmap image2 = Bitmap.createBitmap(imageView2.getWidth(), imageView2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(image2);
        imageView2.draw(canvas2);

        // Redimensionner les deux images en 1000x1000
        Bitmap resizedImage1 = Bitmap.createScaledBitmap(image1, 1000, 1000, true);
        Bitmap resizedImage2 = Bitmap.createScaledBitmap(image2, 1000, 1000, true);

        // Créer un nouveau Bitmap pour dessiner les deux images superposées
        Bitmap combined = Bitmap.createBitmap(resizedImage2.getWidth(), resizedImage2.getHeight(), Bitmap.Config.ARGB_8888);

        // Dessiner la première image sur le nouveau Bitmap
        Canvas canvasCombined = new Canvas(combined);
        canvasCombined.drawBitmap(resizedImage1, 0, 0, null);

        // Dessiner la deuxième image sur le nouveau Bitmap, en utilisant une opacité de 0.4 pour la superposer
        Paint paint = new Paint();
        paint.setAlpha(115); // 40% de transparence
        canvasCombined.drawBitmap(resizedImage2, 0, 0, paint);

        // Afficher l'image finale dans une ImageView
        ImageView finalImageView = findViewById(R.id.imageViewQrPokemonPerso);
        finalImageView.setImageBitmap(combined);
    }

    private void DisplayPokemonImageAndName(String PokemonID) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://pokeapi.co/api/v2/pokemon/"+PokemonID.trim();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);

                            //get the pokemon name
                            String requestName = jObject.getString("name");
                            PokemonTitle.setText(requestName + " !");

                            //get the pokemon image link
                            JSONObject PokemonSprites = jObject.getJSONObject("sprites");
                            JSONObject SpritesOther = PokemonSprites.getJSONObject("other");
                            JSONObject SpritesOfficial = SpritesOther.getJSONObject("official-artwork");
                            String ImageUrl = SpritesOfficial.getString("front_default");
                            linkToImageView(ImageUrl);

                        } catch (Exception e) {
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

    private void linkToImageView(String imageUrl) {
        int maxWidth = 1000;
        int maxHeight = 1000;

        ImageView imageView = imageView2;

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
                        Toast.makeText(DownloadPokemonActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(imageRequest);
    }

    private void QrCodeGenerate(String pokemonID) {

        //ImageView for generated QR code
        ImageView imageCode = imageView1;

        String myText = pokemonID.trim();
        //initializing MultiFormatWriter for QR code
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            //BitMatrix class to encode entered text and set Width & Height
            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400,400);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            imageCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView

        } catch (WriterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}