package nl.acr.rooster;

import android.graphics.Color;

import go.framework.Framework;

class ClassInfo {
    String subject = "";
    String teacher = "";
    String group = "";
    String timeStart = "";
    String timeEnd = "";
    String classRoom = "";
    final int status;

    long timeStartUnix = 0;
    long timeEndUnix = 0;
    int timeSlot = 0;

    String changeDescription = "";

    String date;

//    ClassInfo(String subject, String teacher, String timeStart, String timeEnd, String classRoom, long status, long timeStartUnix, long timeEndUnix, long timeSlot) {
//        this.subject = subject;
//        this.teacher = teacher;
//        this.timeStart = timeStart;
//        this.timeEnd = timeEnd;
//        this.classRoom = classRoom;
//        this.status = (int)status;
//
//        this.timeStartUnix = timeStartUnix;
//        this.timeEndUnix = timeEndUnix;
//        this.timeSlot = (int)timeSlot;
//    }

    ClassInfo(int id) {

        this.subject = Framework.GetClassName(id);
        this.teacher = Framework.GetClassTeacher(id);
        this.group = Framework.GetClassGroup(id);
        this.timeStart = Framework.GetClassStartTime(id);
        this.timeEnd = Framework.GetClassEndTime(id);
        this.classRoom = Framework.GetClassRoom(id);
        this.status = (int)Framework.GetClassStatus(id);

        this.timeStartUnix = Framework.GetClassStartUnix(id);
        this.timeEndUnix = Framework.GetClassEndUnix(id);
        this.timeSlot = (int)Framework.GetClassTimeSlot(id);

        this.changeDescription = Framework.GetChangeDescription(id);
    }

    ClassInfo(long timeUnix, long timeSlot) {

        this.status = (int)Framework.STATUS_FREE;

        this.timeStartUnix = timeUnix;
        this.timeEndUnix = timeUnix;
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
                color = 0xffff9800;
                break;

        }

        long currentUnix = System.currentTimeMillis() / 1000;
        if (timeStartUnix < currentUnix && timeEndUnix > currentUnix) {

            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.7f;
            color = Color.HSVToColor(hsv);
        } else if (timeStartUnix - 60 * 10 < currentUnix && timeStartUnix > currentUnix && status != Framework.STATUS_FREE) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.9f;
            color = Color.HSVToColor(hsv);
        }

        return color;
    }
}
