package nl.acr.rooster;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import go.framework.Framework;

public class WidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences settings = context.getSharedPreferences(StartActivity.PREFS_NAME, 0);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ClassInfo>>() {
        }.getType();
        List<ClassInfo> classList = gson.fromJson(settings.getString("classPort", ""), type);

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            int nowPos = 0;
            long currentTime = System.currentTimeMillis() / 1000;
            // Find the first class that has not yet ended
            for (int i = 0; i < classList.size(); i++) {

                ClassInfo classInfo = classList.get(i);

                if (currentTime < classInfo.timeEndUnix && classInfo.status != Framework.STATUS_FREE && classInfo.status != Framework.STATUS_DATE && classInfo.status != Framework.STATUS_EXAM) {
                    nowPos = i;
                    break;
                }
            }

            int laterPos = 0;
            // Find the next class that has not yet ended
            for (int i = nowPos+1; i < classList.size(); i++) {

                ClassInfo classInfo = classList.get(i);

                if (currentTime < classInfo.timeEndUnix && classInfo.status != Framework.STATUS_FREE && classInfo.status != Framework.STATUS_DATE && classInfo.status != Framework.STATUS_EXAM) {
                    laterPos = i;
                    break;
                }
            }

            ClassInfo classNow = classList.get(nowPos);
            int colorNow = context.getResources().getColor(R.color.mdtp_transparent_black);
            if (classNow.status != Framework.STATUS_NORMAL) {
                colorNow = classNow.getColor();
            }

            views.setTextViewText(R.id.subjectNow, classNow.subject);
            views.setTextColor(R.id.subjectNow, colorNow);
            views.setTextViewText(R.id.timeStartNow, classNow.timeStart);
            views.setTextColor(R.id.timeStartNow, colorNow);
            views.setTextViewText(R.id.timeEndNow, classNow.timeEnd);
            views.setTextColor(R.id.timeEndNow, colorNow);
            views.setTextViewText(R.id.teacherNow, classNow.teacher);
            views.setTextColor(R.id.teacherNow, colorNow);
            views.setTextViewText(R.id.classRoomNow, classNow.classRoom);
            views.setTextColor(R.id.classRoomNow, colorNow);


            ClassInfo classLater = classList.get(laterPos);
            int colorLater = context.getResources().getColor(R.color.mdtp_transparent_black);
            if (classLater.status != Framework.STATUS_NORMAL) {
                colorLater = classLater.getColor();
            }

            views.setTextViewText(R.id.subjectLater, classLater.subject);
            views.setTextColor(R.id.subjectLater, colorLater);
            views.setTextViewText(R.id.timeStartLater, classLater.timeStart);
            views.setTextColor(R.id.timeStartLater, colorLater);
            views.setTextViewText(R.id.timeEndLater, classLater.timeEnd);
            views.setTextColor(R.id.timeEndLater, colorLater);
            views.setTextViewText(R.id.teacherLater, classLater.teacher);
            views.setTextColor(R.id.teacherLater, colorLater);
            views.setTextViewText(R.id.classRoomLater, classLater.classRoom);
            views.setTextColor(R.id.classRoomLater, colorLater);

            Intent clickIntent = new Intent(context, WidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
