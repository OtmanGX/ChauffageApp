package mgadtech.gx.chauffage.services;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import mgadtech.gx.chauffage.api.DoucheAPI;

public class TestWorker extends Worker {
    Context context;
    public TestWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        int running = getInputData().getInt("running", 10);
        String doucheID = getInputData().getString("doucheID");
        // Do the work here--in this case, upload the images.
        Log.d("Worker", "Worker running");
        Log.d("Worker", "doucheID"+doucheID);
        DoucheAPI.changeState(running, doucheID);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run your task here
                Toast.makeText(context, "Il reste 30mn avant la douche", Toast.LENGTH_LONG).show();
            }
        }, 1000 );
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}

