package nl.acr.rooster;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    static protected RecyclerView friendList;
    public static final List<FriendInfo> friendArrayList = new ArrayList<>();
    public static final FriendListAdapter fa = new FriendListAdapter(friendArrayList);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule_friends, container, false);

        friendList = (RecyclerView) rootView.findViewById(R.id.friendList);
        friendList.setHasFixedSize(true);
        friendList.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendList.setAdapter(fa);

        return rootView;
    }
}


