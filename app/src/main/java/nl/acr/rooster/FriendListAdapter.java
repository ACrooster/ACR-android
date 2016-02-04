package nl.acr.rooster;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private final List<FriendInfo> friendInfoList;

    public FriendListAdapter(List<FriendInfo> friendInfoList) {

        this.friendInfoList = friendInfoList;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendListAdapter.FriendViewHolder holder, final int position) {

        final FriendInfo fi = friendInfoList.get(position);

        holder.name.setText((fi.name));
        holder.code.setText(fi.code);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.goTo(fi.code);
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                MainActivity.editPosition = position;
                MainActivity.edit.input("", fi.name, MainActivity.editCallback);
                MainActivity.edit.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return  friendInfoList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        protected final TextView name;
        protected final TextView code;
        protected final RelativeLayout layout;

        public FriendViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.name);
            code = (TextView)v.findViewById(R.id.code);
            layout = (RelativeLayout)v.findViewById(R.id.layout);
        }
    }
}
