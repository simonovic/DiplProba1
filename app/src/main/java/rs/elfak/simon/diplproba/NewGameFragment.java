package rs.elfak.simon.diplproba;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class NewGameFragment extends Fragment implements View.OnClickListener
{
    View v;
    ListView lv;
    ArrayList<String> invitesID;
    Button dateBtn;
    Button timeBtn;
    Button invFr;
    Button createGame;

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    public static NewGameFragment newInstance() {
        NewGameFragment fragment = new NewGameFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_newgame, container, false);
        lv = (ListView)v.findViewById(R.id.listView);
        timeBtn = (Button)v.findViewById(R.id.timeBtn);
        dateBtn = (Button)v.findViewById(R.id.dateBtn);
        invFr = (Button)v.findViewById(R.id.chUser);
        createGame = (Button)v.findViewById(R.id.createGame);
        timeBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        invFr.setOnClickListener(this);
        createGame.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeBtn:
                DialogFragment timeDialog = new TimePickerFragment();
                timeDialog.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
            case R.id.dateBtn:
                DialogFragment dateDialog = new DatePickerFragment();
                dateDialog.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.chUser:
                break;
            case R.id.createGame:
                break;
        }
    }
}
