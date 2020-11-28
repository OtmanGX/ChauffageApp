package mgadtech.gx.chauffage.ui.history;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.api.DoucheAPI;
import mgadtech.gx.chauffage.models.Douche;

/**
 * A fragment representing a list of Items.
 */
public class HistoryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    public static final List<Douche> DOUCHES = new ArrayList<Douche>();
    RecyclerView.Adapter adapter;
    Spinner spinner;
    Calendar from = getCurrentDay();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new MyDoucheRecyclerViewAdapter(DOUCHES, this);
        recyclerView.setAdapter(adapter);

        // Spinner
        final String[] array = getResources().getStringArray(R.array.dates);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w("Date selected", array[position]);
                switch (array[position]) {
                    case "Aujourd'hui" :
                        setDate(getCurrentDay());
                        fetchData();
                        break;
                    case "Semaine" :
                        setDate(getCurrentWeek());
                        fetchData();
                        break;
                    case "Mois":
                        setDate(getCurrentMonth());
                        fetchData();
                        break;
                    case "Année":
                        setDate(getCurrentYear());
                        fetchData();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setDate(Calendar date) {
        from = date;
    }

    private void fetchData() {
        Log.d("Data", "fetching data");
        DOUCHES.clear();
        DoucheAPI.myRef.child("douches").orderByChild("start_date/time").startAt(from.getTimeInMillis()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Douche douche = childSnapshot.getValue(Douche.class);
                    douche.setId(childSnapshot.getKey());
                    DOUCHES.add(douche);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Calendar getCurrentDay() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        Log.i("Start of this week:" ,cal.getFirstDayOfWeek()+"");
        Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
        return cal;
    }

        public Calendar getCurrentWeek() {
            // get today and clear time of day
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            Log.i("Start of this week:" ,cal.getFirstDayOfWeek()+"");
            Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
            return cal;
        }

        public Calendar getCurrentMonth() {
            // get today and clear time of day
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

// get start of this month in milliseconds
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            Log.i("Start of this week:" ,cal.getMinimalDaysInFirstWeek()+"");
            Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
            return cal;
        }

        public Calendar getCurrentYear() {
            // get today and clear time of day
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
            cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
            Log.i("Start of this week:" ,cal.getFirstDayOfWeek()+"");
            Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
            return cal;
        }


}