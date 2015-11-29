package nl.acr.rooster;

import go.framework.Framework;

public class ClassInfo {
    protected String subject;
    protected String teacher;
    protected String timeStart;
    protected String timeEnd;
    protected String classRoom;
    protected int status;

    protected String date;

    ClassInfo(String subject, String teacher, String timeStart, String timeEnd, String classRoom, long status) {
        this.subject = subject;
        this.teacher = teacher;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.classRoom = classRoom;
        this.status = (int)status;
    }

    ClassInfo(String date, int status) {
        this.date = date;
        this.status = status;
    }

    protected int getColor() {

        // TODO: Move this into color.xml
        switch (status) {
            case (int)Framework.STATUS_NORMAL:
            default:
//                return 0xffa1a1a1;
                return 0xffffffff;

            case (int)Framework.STATUS_CHANGED:
//                return 0xffa1a1e1;
                return 0xff2196f3;

            case (int)Framework.STATUS_CANCELLED:
//                return 0xffe1a1a1;
                return 0xffff5722;

            case (int)Framework.STATUS_FREE:
//                return 0xffc4c4c4;
                return 0xfff5f5f5;

            case (int)Framework.STATUS_ACTIVITY:
//                return 0xffa1e1a1;
                return 0xffcddc39;

        }
    }
}
