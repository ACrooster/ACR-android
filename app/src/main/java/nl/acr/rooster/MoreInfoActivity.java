package nl.acr.rooster;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.tutti.android.bottomsheet.BottomSheetActivity;
import ch.tutti.android.bottomsheet.ResolverDrawerLayout;
import go.framework.Framework;

public class MoreInfoActivity extends BottomSheetActivity {

    public static ClassInfo selectedClass = null;
    private ResolverDrawerLayout mResolverDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (selectedClass == null) {
            return;
        }

        setContentView(R.layout.activity_bottom_sheet_more_info);
        setBottomSheetTitle(R.string.more_info);
        setBottomSheetTitleVisible(true);

        RelativeLayout moreInfo = (RelativeLayout)findViewById(R.id.more_info);

        TextView subject = (TextView)moreInfo.findViewById(R.id.subject);
        subject.setText(selectedClass.subject);

        if (selectedClass.status == Framework.STATUS_CANCELLED) {
            subject.setPaintFlags(subject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        ImageView header = (ImageView)moreInfo.findViewById(R.id.header);
        header.setBackgroundColor(selectedClass.getColor());


        TextView change = (TextView)findViewById(R.id.change_description);
        change.setText(selectedClass.changeDescription);

        if (selectedClass.changeDescription == null) {
            change.setVisibility(View.GONE);
            findViewById(R.id.change).setVisibility(View.GONE);
        }

        TextView teacher = (TextView)findViewById(R.id.teacher);
        teacher.setText(selectedClass.teacher);

        TextView classRoom = (TextView)findViewById(R.id.classRoom);
        classRoom.setText(selectedClass.classRoom);

        TextView group = (TextView)findViewById(R.id.group);
        group.setText(selectedClass.group);
    }
}
