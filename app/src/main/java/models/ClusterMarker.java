package models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private Gym gym;

    public ClusterMarker(Gym gym) {
        this.gym = gym;
    }

    @Override
    public LatLng getPosition() {
        return gym.getLocation();
    }

    @Override
    public String getTitle() {
        return gym.getName();
    }

    @Override
    public String getSnippet() {
        return gym.getDiscription();
    }

    public Gym.Type getIconPicture() {
        return gym.getType();
    }
}
