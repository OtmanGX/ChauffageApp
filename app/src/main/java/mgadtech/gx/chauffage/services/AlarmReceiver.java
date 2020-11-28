package mgadtech.gx.chauffage.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.w("Service", "REceived");
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }
}
