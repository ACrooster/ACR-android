package nl.acr.rooster;

enum Status {
    NORMAL,
    CHANGED,
    CANCELED,
    ACTIVITY,
    FREE
}

public class ClassInfo {
    protected String subject;
    protected String teacher;
    protected String times;
    protected String classRoom;
    protected Status status;

    protected int getColor() {

        // TODO: Move this into color.xml
        switch (status) {
            case NORMAL:
            default:
                return 0xffa1a1a1;

            case CHANGED:
                return 0xffa1a1e1;

            case CANCELED:
                return 0xffe1a1a1;

            case FREE:
                return 0xffc4c4c4;

            case ACTIVITY:
                return 0xffa1e1a1;

        }
    }
}
