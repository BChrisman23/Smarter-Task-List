import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Task Class
 *
 * Represents a task that must be completed with a name and due date
 *
 * @author Brennan Chrisman
 * @version Nov. 5, 2026
 */

public class Task implements Serializable{
    private String title;
    private LocalDate date;
    private ArrayList<String> notes;

    /**
     * Constructs a new Task object with the specified title and date
     * @param String title, the title of the task
     * @param LocalDate date, the due date of the task
     */
    public Task(String title, LocalDate date) {
        this.title = title;
        this.date = date;
        notes = new ArrayList<>();
    }

    /**
     * Gives the title of this task
     * @return String, the title of this task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this task to the given value
     *
     * @param String title, the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gives the due date of this task
     *
     * @return LocalDate,  the due date of this task
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the due date of this task
     *
     * @param LocalDate date, the new due date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Adds a note to this task
     * @param String note, the note to add to this task
     */
    public void addnote(String note) {
        notes.add(note);
    }

    /**
     * Lists the notes for this task
     *
     * @return String, each note on its own line
     */
    public String getnotes() {
        String info = "";
        for (String note : notes) {
            info += "- " + note + "\n";
        }
        return info;
    }

    @Override
    public String toString() {
        return  title + ", " + date;
    }
}