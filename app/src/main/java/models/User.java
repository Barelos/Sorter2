package models;

import com.google.android.gms.maps.model.LatLng;

public class User {
    public static int numUsers = 2;
    private static String[] names = {"Barel", "Eylon"};
    private static String[] passwords = {"password", "password"};
    private static String[] goingToList = {"A", null};

    private String name;
    private String password;
    private String goingTo;
    private LatLng location;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public User(int idx){
        name = names[idx];
        password = passwords[idx];
        goingTo = goingToList[idx];
        location = null;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getGoingTo() {
        return goingTo;
    }
}
