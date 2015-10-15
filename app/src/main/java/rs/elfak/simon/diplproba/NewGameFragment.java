package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewGameFragment extends Fragment
{
    View v;

    public static NewGameFragment newInstance() {
        NewGameFragment fragment = new NewGameFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_newgame, container, false);
        return v;
    }
}
