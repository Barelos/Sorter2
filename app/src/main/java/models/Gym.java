package models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Gym {

    public enum Type {
        Basketball,
        Football,
        Tennis
    }

    private static int num = 3;
    private static String[] names = {"A", "B", "C"};
    private static Type[] types = {Type.Basketball, Type.Football, Type.Tennis};
    private static int[] capacities = {20, 25, 5};
    private static double[] currentCapacities = {0.43, 0.2, 0.8};
    private static double[][] hourlyCapacities = {
            {0.9, 1.0, 0.8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0.9, 1.0, 0.8, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.9, 1.0, 0.8, 0}
    };
    private static String[] descriptions = {"1", "2", "3"};
    private static String[] images = {
            "https://tinyurl.com/yxq3joqf",
            "https://tinyurl.com/y4waxfkx",
            "https://tinyurl.com/yy74mryj"};
    private static double[] scores = {3.6, 4.5, 4.6};

    private static double[] Latitudes = {31.780456, 31.780475, 31.779962};
    private static double[] Langtitudes = {35.194697, 35.197127, 35.199026};

    private String name;
    private Type type;
    private int capacity;
    private double currentCapacity;
    private double[] hourlyCapacity;
    private String image;
    private double score;
    private LatLng location;
    private boolean focus;
    private boolean going;
    private User current;


    private String discription;

    public void setCurrent(User current) {
        this.current = current;
    }

    public User getCurrent() {
        return current;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public double[] getHourlyCapacity() {
        return hourlyCapacity;
    }

    public String getDiscription() {
        return discription;
    }

    public String getImage() {
        return image;
    }

    public double getScore() {
        return score;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setGoing(boolean going) {
        this.going = going;
    }

    public boolean isGoing() {
        return going;
    }

    public Gym(int idx){
        name = Gym.names[idx];
        type = Gym.types[idx];
        capacity = Gym.capacities[idx];
        currentCapacity = Gym.currentCapacities[idx];
        hourlyCapacity = Gym.hourlyCapacities[idx];
        discription = Gym.descriptions[idx];
        image = images[idx];
        score = scores[idx];
        location = new LatLng(Latitudes[idx], Langtitudes[idx]);
        focus = false;
        going = false;
    }

    public String toString(){
        return name + "-" + capacity;
    }
}
