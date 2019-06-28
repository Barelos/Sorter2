package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sorter2.R;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Gym;
import models.GymManager;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private GymManager gm;
    private Context context;

    private OnGymListener listener;

    public RecyclerViewAdapter(GymManager gm,
                               Context context,
                               OnGymListener listener)
    {
        this.gm = gm;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called");
        final Gym current = gm.get(i);
        Glide.with(context).asBitmap().load(current.getImage()).into(viewHolder.image);
        viewHolder.gymName.setText(current.getName());
        viewHolder.gymScore.setText(current.getDiscription());
    }

    @Override
    public int getItemCount() {
        return gm.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView image;
        TextView gymName;
        TextView gymScore;
        RelativeLayout parentLayout;

        OnGymListener listener;

        public ViewHolder(@NonNull View itemView, OnGymListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            gymName = itemView.findViewById(R.id.gymName);
            gymScore = itemView.findViewById(R.id.score);
            parentLayout = itemView.findViewById(R.id.parent_layout);

            itemView.setOnClickListener(this);

            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onGymClick(getAdapterPosition());
        }
    }

    public interface OnGymListener{
        void onGymClick(int idx);
    }
}
