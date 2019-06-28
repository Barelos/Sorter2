package models;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Math.signum;

public class GymManager {

    private static GymManager instance = null;
    private UserManager um;

    public static enum SortFunction{
        NAME,
        CAPACITY,
        CURRENT,
        SCORE,
        Distance
    }

    private ArrayList<Gym> gyms = new ArrayList<>();
    private Comparator[] comparators = {
            new CompareName(),
            new CompareCapacity(),
            new CompareCurrentCapacity(),
            new CompareScore(),
            new CompareDist()
    };

    private GymManager(){
        um = UserManager.getInstance();
        for (int i = 0; i < 3; i++) {
            gyms.add(new Gym(i));
            gyms.get(i).setCurrent(um.getCurrentUser());
        }
    }

    public static GymManager getInstance(){
        if (instance == null){
            instance = new GymManager();
        }
        return instance;
    }

    public ArrayList<Gym> sort(SortFunction func){
        Collections.sort(gyms, comparators[func.ordinal()]);
        return gyms;
    }


    public String toString(){
        String result = "";
        for (Gym gym :
                gyms) {
            result += gym + ", ";
        }
        return result;
    }

    public Gym get(int idx){
        return gyms.get(idx);
    }

    public int size(){
        return gyms.size();
    }

    public Gym getGymFromName(String name){
        for (Gym gym : gyms) {
            if (gym.getName().equals(name)){
                return gym;
            }
        }
        return null;
    }

}

class CompareName implements Comparator<Gym> {
    @Override
    public int compare(Gym o1, Gym o2) {
        o1.setDiscription(String.valueOf(o1.getScore()));
        o2.setDiscription(String.valueOf(o2.getScore()));
        return o1.getName().compareTo(o2.getName());
    }
}

class CompareCapacity implements Comparator<Gym> {
    @Override
    public int compare(Gym o1, Gym o2) {
        o1.setDiscription(String.valueOf(o1.getCapacity()));
        o2.setDiscription(String.valueOf(o2.getCapacity()));
        return (int) signum(o2.getCapacity() - o1.getCapacity());
    }
}

class CompareCurrentCapacity implements Comparator<Gym> {
    @Override
    public int compare(Gym o1, Gym o2) {
        o1.setDiscription(String.valueOf((int) (o1.getCurrentCapacity() * o1.getCapacity())));
        o2.setDiscription(String.valueOf((int) (o2.getCurrentCapacity() * o2.getCapacity())));
        return (int) signum(o1.getCurrentCapacity() - o2.getCurrentCapacity());
    }
}

class CompareScore implements Comparator<Gym> {
    @Override
    public int compare(Gym o1, Gym o2) {
        o1.setDiscription(String.valueOf(o1.getScore()));
        o2.setDiscription(String.valueOf(o2.getScore()));
        return (int) signum(o2.getScore() - o1.getScore());
    }
}

class CompareDist implements Comparator<Gym> {
    @Override
    public int compare(Gym o1, Gym o2) {
        LatLng current = o1.getCurrent().getLocation();
        LatLng o1Location = o1.getLocation();
        LatLng o2Location = o1.getLocation();
        float[] results = new float[1];
        Location.distanceBetween(current.latitude, current.longitude, o1Location.latitude, o1Location.longitude, results);
        float dist1 = results[0];
        Location.distanceBetween(current.latitude, current.longitude, o2Location.latitude, o2Location.longitude, results);
        float dist2 = results[0];
        o1.setDiscription(String.valueOf(Math.round(dist1 / 100) / 10.0) + " km");
        o2.setDiscription(String.valueOf(Math.round(dist2 / 100) / 10.0) + " km");
        return (int) signum(dist2 - dist1);
    }
}
