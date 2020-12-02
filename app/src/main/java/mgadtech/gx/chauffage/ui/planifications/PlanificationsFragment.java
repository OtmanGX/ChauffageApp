package mgadtech.gx.chauffage.ui.planifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.Console;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mgadtech.gx.chauffage.api.DoucheAPI;
import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.models.Douche;
import mgadtech.gx.chauffage.services.TestWorker;
import mgadtech.gx.chauffage.ui.planifications.EventDialog.EventDialogListener;

public class PlanificationsFragment extends Fragment implements EventDialogListener {

    private NotificationsViewModel notificationsViewModel;
    public static final List<Douche> DOUCHES = new ArrayList<Douche>();
    public static final List<Douche> DOUCHES2 = new ArrayList<Douche>();
    RecyclerView.Adapter adapter;
    RecyclerView.Adapter adapter2;

    // Worker
    WorkManager workManager;

    // Alarm
    private PendingIntent pendingIntent;
    private AlarmManager manager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_planifications, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        final Fragment fragment = this ;
        Context context = root.getContext();
        RecyclerView recyclerView = root.findViewById(R.id.list1);
        RecyclerView recyclerView2 = root.findViewById(R.id.list2);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyItemRecyclerViewAdapter(DOUCHES, this);
        adapter2 = new MyItemRecyclerViewAdapter(DOUCHES2, this);
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);

        FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDialog dialog = new EventDialog((EventDialogListener)fragment);
                dialog.show(getActivity().getSupportFragmentManager(), "Douche");
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DOUCHES.clear();
        DoucheAPI.myRef.child("douches").limitToLast(10).orderByChild("start_date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("BDD", "douche added");
                Douche douche = snapshot.getValue(Douche.class);
                douche.setId(snapshot.getKey());
                addDouche(douche);
//                if (addDouche(douche) == 1) {
//                    adapter.notifyDataSetChanged();
//                    Log.w("douch name", douche.getNom_user());
//                    Log.w("BDD", "adapter 1");
//                }
//                else adapter2.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("BDD", "douche changed");
                Douche douche = snapshot.getValue(Douche.class);
                douche.setId(snapshot.getKey());
                if (modifyDouche(douche)==1) adapter.notifyDataSetChanged();
                else adapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.w("BDD", "douche removed");
                Douche douche = snapshot.getValue(Douche.class);
                douche.setId(snapshot.getKey());
                cancelWorker(douche.getId());
                Log.w("BDD", "douche key "+douche.getId());
                removeDouche(douche);
//                if (removeDouche(douche) == 1)
//                {
//                    adapter.notifyDataSetChanged();
//                }
//                else adapter2.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Worker
        workManager = WorkManager
                .getInstance(getContext());
    }


    private short addDouche(Douche douche) {
        final Calendar calendar = Calendar.getInstance();

        if (douche.getStart_date().getDate() == calendar.get(Calendar.DAY_OF_MONTH))
        {
            for (Douche d: DOUCHES)
                if (d.getId() == douche.getId())
                    return 0;
            DOUCHES.add(douche);
            return 1;
        }
        else  if (douche.getStart_date().getDate() == calendar.get(Calendar.DAY_OF_MONTH)+1)
        {
            for (Douche d: DOUCHES2)
                if (d.getId() == douche.getId())
                    return 0;
            DOUCHES2.add(douche);
            return 2;
        }
        return 0;
    }

    private short modifyDouche(Douche douche) {
        for (Douche d:
                DOUCHES) {
            if (d == douche) {
                d = douche;
                return 1;
            }
        }
        for (Douche d:
                DOUCHES2) {
            if (d == douche) {
                d = douche;
                return 2;
            }
        }
        return 0;
    }

    private short removeDouche(Douche douche) {
        for (Douche d:
                DOUCHES) {
            Log.d("BDD", "ID: "+douche.getId());
            if (d.equals(douche)) {
//            if (d.equals(douche)) {
                DOUCHES.remove(d);
                return 1;
            }
        }
        for (Douche d:
                DOUCHES2) {
            Log.d("BDD", "ID: "+douche.getId());
            if (d.equals(douche)) {
                DOUCHES2.remove(d);
                return 2;
            }
        }
        Log.w("BDD", "Not found");
        return 0;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Douche douche, String tag) {
        int index = 0 ;
        if (tag != null) {
            index = removeDouche(douche);
        }
        String key = DoucheAPI.addDouche(douche);
        DoucheAPI.changeState(10, key);
        Toast.makeText(getContext(), "Nouvelle douche", Toast.LENGTH_SHORT).show();
//        if (isCorrectTime(douche.getStart_date())) {
//            String key = DoucheAPI.addDouche(douche);
//            Calendar calendar = Calendar.getInstance();
//            int hourOfDay = douche.getStart_date().getHours();
//            int minute = douche.getStart_date().getMinutes();
//            String newTag = String.format("%02d:%02d", hourOfDay, minute);
//            Log.d("BDD", "Tag: " + newTag);
//            Long diff = douche.getStart_date().getTime() - calendar.getTimeInMillis();
//            Log.d("BDD", String.valueOf(diff));
//            if (tag != null) cancelWorker(douche.getId());
//            startWorker(diff, key);
//        } else {
//            Toast.makeText(getContext(), getResources().getString(R.string.invalid_time), Toast.LENGTH_SHORT).show();
//            if (tag != null)
//            {
//                if (index == 1) DOUCHES.add(douche);
//                else DOUCHES2.add(douche);
//            }
//        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private boolean isCorrectTime(Date date) {
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        if (date.getTime()<=currentTime) return false;
        for (Douche douche:
             DOUCHES) {
            Long diff = date.getTime() - douche.getStart_date().getTime() ;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (diff<=30 && diff >=-60) return false;
        }
        for (Douche douche:
             DOUCHES2) {
            Long diff = date.getTime() - douche.getStart_date().getTime() ;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (diff<=30 && diff >=-60) return false;
        }
        return true;
    }


    // *********** Workers ***********
    private void startWorker(Long duration, String doucheID) {
        WorkRequest beforeWorkRequest =
                new OneTimeWorkRequest.Builder(TestWorker.class)
                        .setInitialDelay(TimeUnit.MILLISECONDS.toMinutes(duration)-30, TimeUnit.MINUTES)
                        .setInputData(new Data.Builder()
                                .putString("doucheID", doucheID)
                                .putInt("running", 10).build())
                        .addTag(doucheID)
                        .build();

        WorkRequest afterWorkRequest =
                new OneTimeWorkRequest.Builder(TestWorker.class)
                        .setInitialDelay(TimeUnit.MILLISECONDS.toMinutes(duration)+30, TimeUnit.MINUTES)
                        .setInputData(new Data.Builder()
                                .putString("doucheID", doucheID+"@")
                                .putInt("running", 30).build())
                        .addTag(doucheID)
                        .build();
        workManager
                .enqueue(beforeWorkRequest);
        workManager
                .enqueue(afterWorkRequest);

    }

    private void cancelWorker(String tag) {
        // by tag
        Log.w("Worker", "cancel worker by tag: "+tag);
        workManager
                .cancelAllWorkByTag(tag);
        workManager
                .cancelAllWorkByTag(tag+"@");
    }
}