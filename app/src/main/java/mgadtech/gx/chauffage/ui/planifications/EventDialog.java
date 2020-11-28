package mgadtech.gx.chauffage.ui.planifications;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

import mgadtech.gx.chauffage.R;
import mgadtech.gx.chauffage.models.Douche;

public class EventDialog extends DialogFragment {

    Calendar calendar;
    Calendar datetime;
    int hour, minute;
    EventDialogListener listener;
    DialogInterface dialogInterface;
    Douche douche = null;
    View view;
    String modifiedTag = null;


    public interface EventDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Douche douche, String tag);
        public void onDialogNegativeClick(DialogFragment dialog);
    }


    public EventDialog(EventDialogListener fragment) {
        super();
        calendar = Calendar.getInstance();
        datetime = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 35);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        listener  = fragment ;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog, null);
        final Button timeButton = view.findViewById(R.id.buttonTime);
        final EditText nameEditText = view.findViewById(R.id.editTextNom);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (isToday())
                        {
                            if(datetime.getTimeInMillis()<calendar.getTimeInMillis()){
                                Toast.makeText(getActivity(), R.string.invalid_time, Toast.LENGTH_LONG).show();
                            } else ((AlertDialog)dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                        } else ((AlertDialog)dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        // Arguments
        if (getArguments() != null) {
            douche = (Douche) getArguments().getSerializable("doucheObject");
            modifiedTag = String.format("%02d:%02d", douche.getStart_date().getHours(), douche.getStart_date().getMinutes());
            timeButton.setText(modifiedTag);
            nameEditText.setText(douche.getNom_user());
        } else {
            timeButton.setText(String.format("%02d:%02d", hour, minute));
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle("Nouvelle Douche")
                // Add action buttons
                .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String nom = nameEditText.getText().toString();
                        if (!isToday()) {
                            datetime.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        if (douche == null)
                        {
                            douche = new Douche(nom, datetime.getTime());
                            listener.onDialogPositiveClick(EventDialog.this, douche, null);
                        } else {
                            douche.setStart_date(datetime.getTime());
                            douche.setNom_user(nom);
                            listener.onDialogPositiveClick(EventDialog.this, douche, modifiedTag);
                        }


                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EventDialog.this.getDialog().cancel();
                        listener.onDialogNegativeClick(EventDialog.this);
                    }
                });

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                dialogInterface = dialog;
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
//        android.app.AlertDialog alertDialog = dialog;
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return dialog;
    }

    private boolean isToday() {
        return  ((RadioButton)view.findViewById(R.id.radioButton)).isChecked();
    }

}
