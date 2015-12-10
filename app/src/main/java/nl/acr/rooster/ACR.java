package nl.acr.rooster;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formUri = "https://collector.tracepot.com/235160b9",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)

public class ACR extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }
}
