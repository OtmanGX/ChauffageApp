package mgadtech.gx.chauffage.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mgadtech.gx.chauffage.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private TextView temp1 ;
    private TextView temp2 ;
    private FirebaseDatabase database ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        temp1 = root.findViewById(R.id.qte);
        temp2 = root.findViewById(R.id.temp2);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        getNewData("data/TemperatureE", temp1);
        getNewData("data/TemperatureS", temp2);
    }


    public void getNewData(String table, final TextView textView) {
        DatabaseReference myRef = database.getReference(table);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("BDD", "onChildAdded:" + snapshot.getKey());
                // A new comment has been added, add it to the displayed list
                int value = snapshot.getValue(Integer.class);
                textView.setText(String.valueOf(value)+"°C");
                Log.d("BDD", "Value:" + value);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.limitToLast(1).addChildEventListener(childEventListener);
    }
}