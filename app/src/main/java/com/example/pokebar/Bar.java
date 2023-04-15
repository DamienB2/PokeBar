package com.example.pokebar;

public class Bar {
    public String BarName;
    public String BarDescription;
    public int Pokemon;
    public double Latitude;
    public double Longitude;


    public Bar() {}

    public Bar(String BarName, String BarDescription , int Pokemon, double Latitude, double Longitude){
        this.BarName = BarName;
        this.BarDescription = BarDescription;
        this.Pokemon = Pokemon;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

}
