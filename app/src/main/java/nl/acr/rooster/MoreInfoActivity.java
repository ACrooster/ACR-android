package nl.acr.rooster;

import android.os.Bundle;

import ch.tutti.android.bottomsheet.BottomSheetActivity;

public class MoreInfoActivity extends BottomSheetActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_sheet_more_info);
//        setBottomSheetTitle("Meer informatie");
//        setBottomSheetIcon(R.mipmap.ic_launcher);
        setBottomSheetTitleVisible(false);
    }
}
