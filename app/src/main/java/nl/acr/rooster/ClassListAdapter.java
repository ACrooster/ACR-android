package nl.acr.rooster;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ClassViewHolder> {

    private List<ClassInfo> classInfoList;

    public ClassListAdapter(List<ClassInfo> classInfoList) {
        this.classInfoList = classInfoList;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);

        return  new ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        ClassInfo ci = classInfoList.get(position);
        if (ci.status == Status.DATE) {

            holder.card.setVisibility(View.GONE);
            holder.date.setVisibility(View.VISIBLE);

            holder.date.setText(ci.date);
        } else {
            holder.card.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.GONE);

            holder.subject.setText(ci.subject);
            holder.teacher.setText(ci.teacher);
            holder.times.setText(ci.times);
            holder.classRoom.setText(ci.classRoom);
            holder.card.setCardBackgroundColor(ci.getColor());
        }
    }

    @Override
    public int getItemCount() {
        return classInfoList.size();
    }

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        protected TextView subject;
        protected TextView teacher;
        protected TextView times;
        protected TextView classRoom;
        protected CardView card;
        protected TextView date;

        public ClassViewHolder(View v) {
            super(v);
            subject = (TextView)v.findViewById(R.id.subject);
            teacher = (TextView)v.findViewById(R.id.teacher);
            times = (TextView)v.findViewById(R.id.times);
            classRoom = (TextView)v.findViewById(R.id.classRoom);
            card = (CardView)v.findViewById(R.id.card_view);
            date = (TextView)v.findViewById(R.id.date);
        }
    }

}

