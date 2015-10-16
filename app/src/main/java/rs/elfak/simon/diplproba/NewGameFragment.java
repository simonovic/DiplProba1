package rs.elfak.simon.diplproba;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class NewGameFragment extends Fragment implements View.OnClickListener
{
    View v;
    Button dateBtn, timeBtn, invFr, createGame;
    EditText name, time, date, comm, numChoosenFr, address;
    ArrayList<User> friends, choosenFr;
    boolean[] chFrBool;
    static int trueCnt = 0;
    static boolean first = true;

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

    public static class SomeDialog extends DialogFragment {
        ArrayList<User> friends;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String frList = ((MainActivity)getActivity()).getFrResp();
            Gson gson = new GsonBuilder().serializeNulls().create();
            friends = gson.fromJson(frList, new TypeToken<ArrayList<User>>() {}.getType());
            List<String> pom = new ArrayList<String>();
            for (Iterator<User> u = friends.iterator(); u.hasNext(); )
            {
                User fr = u.next();
                pom.add(fr.getUname()+" ("+fr.getFname()+" "+fr.getLname()+")");
            }
            final CharSequence frlist[] = pom.toArray(new CharSequence[pom.size()]);
            final boolean bl[] = first ? new boolean[frlist.length] : ((MainActivity)getActivity()).getChoosenFr();
            if (first) first = false;
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Prijatelji:")
                    .setMultiChoiceItems(frlist, bl, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked)
                                trueCnt++;
                            else
                                trueCnt--;
                            Toast.makeText(getActivity().getApplicationContext(), "Klik na item!"+bl[which]+isChecked, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_friends, null))
                    .setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing (will close dialog)
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).setChoosenFr(bl);
                            ((MainActivity) getActivity()).populateChFr();
                        }
                    })
                    .create();
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
        timeBtn = (Button)v.findViewById(R.id.timeBtn);
        dateBtn = (Button)v.findViewById(R.id.dateBtn);
        invFr = (Button)v.findViewById(R.id.chUser);
        createGame = (Button)v.findViewById(R.id.createGame);
        timeBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        invFr.setOnClickListener(this);
        createGame.setOnClickListener(this);
        name = (EditText)v.findViewById(R.id.name);
        time = (EditText)v.findViewById(R.id.time);
        date = (EditText)v.findViewById(R.id.date);
        comm = (EditText)v.findViewById(R.id.com);
        numChoosenFr = (EditText)v.findViewById(R.id.numFr);
        address = (EditText)v.findViewById(R.id.address);
        address.setFocusable(false);
        address.setClickable(true);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Radi klik na adresu!", Toast.LENGTH_SHORT).show();
            }
        });
        choosenFr = new ArrayList<User>();
        String frList = ((MainActivity)getActivity()).getFrResp();
        if (!frList.equals("")) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            friends = gson.fromJson(frList, new TypeToken<ArrayList<User>>(){}.getType());
        }
        numChoosenFr.setText("Broj pozvanih prijatelja: 0");
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
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SomeDialog newFragment = new SomeDialog ();
                newFragment.show(ft, "dialog");
                break;
            case R.id.createGame:
                createGame();
                break;
        }
    }

    private void createGame()
    {
        String n, t, d, c;
        n = name.getText().toString();
        t = time.getText().toString();
        d = date.getText().toString();
        c = comm.getText().toString();
        if (n.equals("") || t.equals("") || d.equals("") || c.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "Morate popuniti sva polja!", Toast.LENGTH_SHORT).show();
        } else if (true) {
            Toast.makeText(getActivity().getApplicationContext(), "Potrebna su minimum dva prijatelja!", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject data = new JSONObject();
            try {
                data.put("name", n);
                data.put("time", t);
                data.put("date", d);
                data.put("comm", c);
                //data.put("upass", up); //prijatelji
            } catch (JSONException e) { e.printStackTrace(); }
            //LoginActivity.socket.emit("newGameRequest", data);
        }
    }

    public void populateChoosenFr()
    {
        chFrBool = ((MainActivity)getActivity()).getChoosenFr();
        //int trueCount = Arrays.deepToString((Object[])chFrBool).replaceAll("[^t]", "").length();
        numChoosenFr.setText("Broj pozvanih prijatelja: "+trueCnt);
        Toast.makeText(getActivity().getApplicationContext(), "Radi: "+chFrBool[0], Toast.LENGTH_SHORT).show();
    }
}
