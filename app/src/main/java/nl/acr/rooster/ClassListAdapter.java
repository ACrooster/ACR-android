package nl.acr.rooster;

import android.content.Intent;
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

    private final List<ClassInfo> classInfoList;

    public ClassListAdapter(List<ClassInfo> classInfoList) {
        this.classInfoList = classInfoList;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);

        return  new ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, final int position) {
        if (position < classInfoList.size()) {
            final ClassInfo ci = classInfoList.get(position);
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
                if (Framework.IsEmployee()) {

                    holder.teacher.setText(ci.group);
                } else {

                    holder.teacher.setText(ci.teacher);
                }
                holder.timeStart.setText(ci.timeStart);
                holder.timeEnd.setText(ci.timeEnd);
                holder.classRoom.setText(ci.classRoom);
                holder.card.setCardBackgroundColor(ci.getColor());

                if (ci.status == Framework.STATUS_FREE) {
                    holder.card.setCardElevation(0);
                    holder.card.setClickable(false);
                } else {
                    if (ci.status ==  Framework.STATUS_CANCELLED) {
                        holder.card.setCardElevation(0);
                    } else {
                        holder.card.setCardElevation(2);
                    }
                    holder.card.setClickable(true);
                    holder.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MoreInfoActivity.selectedClass = ci;
                            v.getContext().startActivity(new Intent(v.getContext(), MoreInfoActivity.class));
                        }
                    });
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
        protected final TextView subject;
        protected final TextView teacher;
        protected final TextView timeStart;
        protected final TextView timeEnd;
        protected final TextView classRoom;
        protected final CardView card;
        protected final TextView date;

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

