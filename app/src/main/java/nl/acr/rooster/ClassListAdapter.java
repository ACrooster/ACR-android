package nl.acr.rooster;

import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import go.framework.Framework;

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
        if (position < classInfoList.size()) {
            ClassInfo ci = classInfoList.get(position);
            if (ci.status == Framework.STATUS_DATE) {
                holder.card.setVisibility(View.GONE);
                holder.date.setVisibility(View.VISIBLE);

                holder.date.setText(ci.date);
            } else if(ci.status == Framework.STATUS_EMPTY) {
                holder.card.setVisibility(View.GONE);
                holder.date.setVisibility(View.GONE);
            } else {
                holder.card.setVisibility(View.VISIBLE);
                holder.date.setVisibility(View.GONE);

                holder.subject.setText(ci.subject);
                holder.teacher.setText(ci.teacher);
                holder.timeStart.setText(ci.timeStart);
                holder.timeEnd.setText(ci.timeEnd);
                holder.classRoom.setText(ci.classRoom);
                holder.card.setCardBackgroundColor(ci.getColor());

                if (ci.status == Framework.STATUS_FREE) {
                    holder.card.setCardElevation(0);
                    holder.card.setClickable(false);
                } else {
                    holder.card.setCardElevation(4);
                    holder.card.setClickable(true);
                }

                if (ci.status == Framework.STATUS_CANCELLED) {
                    holder.subject.setPaintFlags(holder.subject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.subject.setPaintFlags(holder.subject.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return classInfoList.size();
    }

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        protected TextView subject;
        protected TextView teacher;
        protected TextView timeStart;
        protected TextView timeEnd;
        protected TextView classRoom;
        protected CardView card;
        protected TextView date;

        public ClassViewHolder(View v) {
            super(v);
            subject = (TextView)v.findViewById(R.id.subject);
            teacher = (TextView)v.findViewById(R.id.teacher);
            timeStart = (TextView)v.findViewById(R.id.timeStart);
            timeEnd = (TextView)v.findViewById(R.id.timeEnd);
            classRoom = (TextView)v.findViewById(R.id.classRoom);
            card = (CardView)v.findViewById(R.id.card_view);
            date = (TextView)v.findViewById(R.id.date);
        }
    }

}

