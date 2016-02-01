package nl.acr.rooster;

import android.graphics.Color;

import go.framework.Framework;

class ClassInfo {
    String subject;
    String teacher;
    String timeStart;
    String timeEnd;
    String classRoom;
    final int status;

    long timeStartUnix;
    long timeEndUnix;
    int timeSlot;

    String date;

    ClassInfo(String subject, String teacher, String timeStart, String timeEnd, String classRoom, long status, long timeStartUnix, long timeEndUnix, long timeSlot) {
        this.subject = subject;
        this.teacher = teacher;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.classRoom = classRoom;
        this.status = (int)status;

        this.timeStartUnix = timeStartUnix;
        this.timeEndUnix = timeEndUnix;
        this.timeSlot = (int)timeSlot;
    }

    ClassInfo(String date, long timeStartUnix) {
        this.date = date;
        this.status = (int)Framework.STATUS_DATE;

        this.timeStartUnix = timeStartUnix;
    }

    ClassInfo() {
        this.status = (int)Framework.STATUS_EMPTY;
    }

    int getColor() {

        int color;

        // TODO: Move this into color.xml
        switch (status) {
            case (int)Framework.STATUS_NORMAL:
            default:
//                return 0xffa1a1a1;
                color = 0xffffffff;
                break;

            case (int)Framework.STATUS_CHANGED:
//                return 0xffa1a1e1;
                color = 0xff2196f3;
                break;

            case (int)Framework.STATUS_CANCELLED:
//                return 0xffe1a1a1;
                color = 0xffff5722;
                break;

            case (int)Framework.STATUS_FREE:
//                return 0xffc4c4c4;
                color = 0xfff5f5f5;
                break;

            case (int)Framework.STATUS_ACTIVITY:
//                return 0xffa1e1a1;
                color = 0xffcddc39;
                break;

            case (int)Framework.STATUS_EXAM:
                color = 0xff9c27b0;
                break;

        }

        long currentUnix = System.currentTimeMillis() / 1000;
        if (timeStartUnix < currentUnix && timeEndUnix > currentUnix) {

            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.7f; // value component
            color = Color.HSVToColor(hsv);
        } else if (timeStartUnix - 60 * 10 < currentUnix && timeStartUnix > currentUnix) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.9f; // value component
            color = Color.HSVToColor(hsv);
        }

        return color;
    }
}
