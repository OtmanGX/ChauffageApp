package mgadtech.gx.chauffage.ui.history;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.models.Douche;
import mgadtech.gx.chauffage.ui.DetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MyDoucheRecyclerViewAdapter extends RecyclerView.Adapter<MyDoucheRecyclerViewAdapter.ViewHolder> {

    Fragment fragment;
    private final List<Douche> mValues;
    DateFormat dateFormat  = new SimpleDateFormat("yy/MM/dd HH:mm");

    public MyDoucheRecyclerViewAdapter(List<Douche> items, Fragment fragment) {
        mValues = items;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Calendar calendar = Calendar.getInstance();
        holder.mItem = mValues.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(), DetailActivity.class);
                intent.putExtra("doucheID", holder.mItem.getId());
                fragment.getActivity().startActivity(intent);
            }
        });
        holder.mIdView.setText(dateFormat.format(mValues.get(position).getStart_date()));
        holder.mContentView.setText(mValues.get(position).getNom_user());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Douche mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.tempE);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}