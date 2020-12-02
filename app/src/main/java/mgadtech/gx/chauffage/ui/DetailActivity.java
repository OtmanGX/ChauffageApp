package mgadtech.gx.chauffage.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.models.Temperature;
import mgadtech.gx.chauffage.ui.history.MyDoucheRecyclerViewAdapter;
import mgadtech.gx.chauffage.utils.GraphUtils;

import static mgadtech.gx.chauffage.api.DoucheAPI.myRef;

public class DetailActivity extends AppCompatActivity {

    LineChart mChart;
    GraphUtils graph;
    private Thread thread;
    private List<Temperature> temperatures = new ArrayList<Temperature>();
    RecyclerView.Adapter adapter;
    private String doucheID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TempRecyclerViewAdapter(temperatures, this);
        recyclerView.setAdapter(adapter);
        doucheID = getIntent().getStringExtra("doucheID");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initChart();
    }

    private void initChart(){
        readValues();

        mChart = (LineChart) findViewById(R.id.chart);
        graph = new GraphUtils(this, mChart, 2000, 5000);

//        feedMultiple();
    }

    private void readValues() {
        final ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                long tempE =  (Long) map.get("TemperatureE");
                long tempS = (Long) map.get("TemperatureS");
                long time = (Long) map.get("timestamp");

                adapter.notifyDataSetChanged();
                if (previousChildName == null)
                    graph.initGraph(time, "today");
                long diff = time-graph.referenceTimestamp;

                Temperature temp = new Temperature(tempE, tempS, new Date(time), (int) (diff/1000));
                temperatures.add(temp);
                graph.addEntry(diff, tempE, tempS);
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
        myRef.child("data").child(doucheID).addChildEventListener(childEventListener);
//        myRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
//                String doucheID = map.get("doucheID").toString();
//                myRef.child("data").child(doucheID).addChildEventListener(childEventListener);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                float inValue = (float) (Math.random() * 15) + 10f;
                float outValue = (float) (Math.random() * 5) + 30f;
                long diff = System.currentTimeMillis()-graph.referenceTimestamp;
                graph.addEntry(diff, inValue, outValue);
            }
        };

        thread = new Thread() {
            public void run() {
                for (int i = 0; i < 200; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
    }
}