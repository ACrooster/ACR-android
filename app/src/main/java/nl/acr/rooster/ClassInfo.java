package nl.acr.rooster;

enum Status {
    NORMAL,
    CHANGED,
    CANCELED,
    ACTIVITY,
    FREE,
    DATE

}

public class ClassInfo {
    protected String subject;
    protected String teacher;
    protected String timeStart;
    protected String timeEnd;
    protected String classRoom;
    protected Status status;

    protected String date;

    ClassInfo(String subject, String teacher, String timeStart, String timeEnd, String classRoom, Status status) {
        this.subject = subject;
        this.teacher = teacher;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.classRoom = classRoom;
        this.status = status;
    }

    ClassInfo(String date, Status status) {
        this.date = date;
        this.status = status;
    }

    protected int getColor() {

        // TODO: Move this into color.xml
        switch (status) {
            case NORMAL:
            default:
//                return 0xffa1a1a1;
                return 0xffffffff;

            case CHANGED:
//                return 0xffa1a1e1;
                return 0xff2196f3;

            case CANCELED:
//                return 0xffe1a1a1;
                return 0xffff5722;

            case FREE:
//                return 0xffc4c4c4;
                return 0xfff5f5f5;

            case ACTIVITY:
//                return 0xffa1e1a1;
                return 0xffcddc39;

        }
    }
}
