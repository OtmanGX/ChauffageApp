package mgadtech.gx.chauffage.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mgadtech.gx.chauffage.models.Douche;

public abstract class DoucheAPI {
    public static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static String addDouche(Douche douche) {
        String key = myRef.push().getKey();
        myRef.child("douches").child(key).setValue(douche);
        return key;
    }

    public static void modifyDouche(String key, Douche douche) {
        myRef.child("douches").child(key).setValue(douche);
    }

    public static void deleteDouche(String key) {
        myRef.child("douches").child(key).removeValue();
    }

    public static void changeState(int running, String doucheID) {
        myRef.child("events").child("marche").setValue(running);
        myRef.child("events").child("doucheID").setValue(doucheID);
    }

    public static DatabaseReference amountWater(String key) {
        return myRef.child("data").child(key);
    }
}
