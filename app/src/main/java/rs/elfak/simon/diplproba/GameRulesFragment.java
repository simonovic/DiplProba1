package rs.elfak.simon.diplproba;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameRulesFragment extends Fragment
{
    View v;

    public GameRulesFragment() {}

    public static GameRulesFragment newInstance() {
        GameRulesFragment fragment = new GameRulesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rules, container, false);
        return v;
    }
}
