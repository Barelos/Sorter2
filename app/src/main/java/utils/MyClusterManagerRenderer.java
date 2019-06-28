package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sorter2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import models.ClusterMarker;
import models.Gym.Type;

import static models.Gym.Type.Basketball;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private IconGenerator iconGenerator;
    private ImageView imageView;
    private int iconResource;
    private Context context;

    private final int width;

    private final int height;

    public int getIconResource() {
        return iconResource;
    }

    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);

        this.context = context;

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        width = (int) context.getResources().getDimension(R.dimen.custom_marker_width);
        height = (int) context.getResources().getDimension(R.dimen.custom_marker_height);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        int padd = (int) context.getResources().getDimension(R.dimen.custom_marker_padd);
        imageView.setPadding(padd, padd, padd, padd);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        switch (item.getIconPicture()){
            case Basketball:
                imageView.setImageResource(R.drawable.basketball);
                break;
            case Football:
                imageView.setImageResource(R.drawable.football);
                break;
            case Tennis:
                imageView.setImageResource(R.drawable.tennis);
                break;
        }
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }
}
