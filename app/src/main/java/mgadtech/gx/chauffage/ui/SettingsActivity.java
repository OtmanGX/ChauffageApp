package mgadtech.gx.chauffage.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.api.DoucheAPI;
import mgadtech.gx.chauffage.api.SettingsAPI;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        EditTextPreference editTextPreference;
        EditTextPreference editTextPreference2;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            initializeConfig();
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            editTextPreference = findPreference("delay");
            editTextPreference2 = findPreference("marche");


            editTextPreference.setOnPreferenceChangeListener(listener);
            editTextPreference2.setOnPreferenceChangeListener(listener);

            for (int i=1;i<=7;i++) {
                PreferenceCategory category = findPreference("cycle"+i);
                for (int j=1;j<=6;j++)
                    category.findPreference("t"+j).setOnPreferenceChangeListener(tListener);
            }

            ListPreference listPreference = findPreference("cycle");
            final String []cycles = getResources().getStringArray(R.array.cycles);
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = Arrays.asList(cycles).indexOf((String) newValue);
                    PreferenceCategory category = findPreference("cycle"+(index+1));
                    for (int i=1;i<=6;i++) {
                        SettingsAPI.myRef.child("config").child("t"+i).setValue(
                                Integer.parseInt(((EditTextPreference)category.findPreference("t"+i)).getText()));
                    }
                    return true;
                }
            });

        }

        public void fetchData() {
            SettingsAPI.myRef.child("events").child("marche").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    editTextPreference2.setText(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            SettingsAPI.myRef.child("config").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (int i=1;i<=7;i++) {
                        PreferenceCategory cat = findPreference("cycle"+i);
                        for (int j=1;j<=6;j++)
                        {
                            ((EditTextPreference )cat.findPreference("t"+j)).setText(
                                    String.valueOf(snapshot.child("cycle"+i).child("t"+j).getValue(Integer.class))
                            );
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        public void initializeConfig() {
            for (int j=1;j<=6;j++)
                SettingsAPI.myRef.child("config").child("t"+j).setValue(2);
//            for (int i=1;i<=7;i++) {
//                for (int j=1;j<=6;j++)
//                    SettingsAPI.setTimeCycle("cycle"+i, "t"+j, 2);
//            }
        }
    }

    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d("Preferences", newValue.toString());
            if (preference.getKey().equals("marche"))
                SettingsAPI.setMarche(Integer.parseInt(newValue.toString()));
            else SettingsAPI.setDelay(Integer.parseInt(newValue.toString()));
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener tListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.i("Preference t1", preference.getParent().getTitle().toString());
            String cycle = preference.getParent().getKey();
            SettingsAPI.setTimeCycle(cycle, preference.getKey(), (Integer) newValue);
            return true;
        }
    };
}