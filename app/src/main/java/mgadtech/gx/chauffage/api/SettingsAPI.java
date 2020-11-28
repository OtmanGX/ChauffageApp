package mgadtech.gx.chauffage.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mgadtech.gx.chauffage.models.Douche;

public class SettingsAPI {
    public static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static String getDelay() {
        String key = myRef.push().getKey();
        myRef.child("events").child("delay");
        return key;
    }

    public static void setDelay(int value) {
        myRef.child("events").child("delay").setValue(value);
    }
    public static void setMarche(int value) {
        myRef.child("events").child("marche").setValue(value);
    }


}
