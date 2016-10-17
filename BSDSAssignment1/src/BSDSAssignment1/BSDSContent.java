/**
 * Created by Ian Gortan on 9/20/2016.
 */
package BSDSAssignment1;
// Contains content for message publication

public class BSDSContent {
    final static int len = 140;
    private String title;
    private String message;
    private long timeToLIve;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeToLIve() {
        return timeToLIve;
    }

    public void setTimeToLIve(long timeToLIve) {
        this.timeToLIve = timeToLIve;
    }
}
