package mgadtech.gx.chauffage.ui.planifications;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.api.DoucheAPI;
import mgadtech.gx.chauffage.models.Douche;
import mgadtech.gx.chauffage.ui.DetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Douche> mValues;
    DateFormat dateFormat  = new SimpleDateFormat("HH:mm");
    Fragment fragment;

    public MyItemRecyclerViewAdapter(List<Douche> items, Fragment fragment) {
        mValues = items;
        this.fragment = fragment;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Calendar calendar = Calendar.getInstance();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(), DetailActivity.class);
                fragment.getActivity().startActivity(intent);
            }
        });
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(dateFormat.format(mValues.get(position).getStart_date()));
        holder.mContentView.setText(mValues.get(position).getNom_user());
        if (Math.abs(holder.mItem.getStart_date().getTime() - calendar.getTime().getTime())  <= 30 *60 *1000)
        {
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (calendar.getTimeInMillis()-holder.mItem.getStart_date().getTime()>30 *60 *1000)
        {
            holder.imageView.setImageDrawable(holder.mView.getResources().getDrawable(R.drawable.ic_baseline_cloud_done_24));
            holder.imageView.setColorFilter(holder.mView.getResources().getColor(R.color.colorAccent));
            holder.imageView.setVisibility(View.VISIBLE);
        }
        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                MenuBuilder menuBuilder = new MenuBuilder(holder.mView.getContext());
                MenuInflater inflater = new MenuInflater(holder.mView.getContext());
                inflater.inflate(R.menu.category_options_menu, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(holder.mView.getContext(),
                        menuBuilder, holder.mView);
                optionsMenu.setForceShowIcon(true);
                optionsMenu.setGravity(Gravity.RIGHT);

                // Set Item Click Listener
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit: // Handle option1 Click
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("doucheObject", holder.mItem);
                                EventDialog dialog = new EventDialog((EventDialog.EventDialogListener)fragment);
                                dialog.setArguments(bundle);
                                dialog.show(fragment.getActivity().getSupportFragmentManager(), "Douche");
                                return true;


                            case R.id.del: // Handle option2 Click
                                DoucheAPI.deleteDouche(holder.mItem.getId());
                                return true;

                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {}
                });

                //displaying the popup
                optionsMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView imageView;
        public final ImageButton optionsButton;
        public Douche mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.tempE);
            mContentView = (TextView) view.findViewById(R.id.content);
            imageView = view.findViewById(R.id.imageState);
            optionsButton = view.findViewById(R.id.optionsButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}