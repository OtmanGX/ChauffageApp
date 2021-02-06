package mgadtech.gx.chauffage.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.models.Temperature;


public class TempRecyclerViewAdapter extends RecyclerView.Adapter<TempRecyclerViewAdapter.ViewHolder> {

    private final List<Temperature> mValues;
    DateFormat dateFormat  = new SimpleDateFormat("HH:mm:ss");
    Activity activity;

    public TempRecyclerViewAdapter(List<Temperature> items, Activity activity) {
        mValues = items;
        this.activity = activity;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.temp_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Calendar calendar = Calendar.getInstance();
        holder.mItem = mValues.get(position);
        holder.tempE.setText(String.valueOf(holder.mItem.getTempE())+ "°C");
        holder.tempS.setText(String.valueOf(holder.mItem.getTempS())+ "°C");
        holder.tempV.setText(String.valueOf(holder.mItem.getTempV())+ "°C");
        holder.tempEm.setText(String.valueOf(holder.mItem.getTempEm())+ "°C");
        holder.timestamp.setText(String.valueOf(holder.mItem.getTimestamp())+ "s");
        holder.date.setText(dateFormat.format(holder.mItem.getDate()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tempE;
        public final TextView tempS;
        public final TextView tempV;
        public final TextView tempEm;
        public final TextView date;
        public final TextView timestamp;
        public Temperature mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tempE = (TextView) view.findViewById(R.id.tempE);
            tempS = (TextView) view.findViewById(R.id.tempS);
            tempV = (TextView) view.findViewById(R.id.tempV);
            tempEm = (TextView) view.findViewById(R.id.tempEm);
            date = (TextView) view.findViewById(R.id.date);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}