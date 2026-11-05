import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.io.FileNotFoundException;
import java.io.EOFException;

/**
 * TaskList class
 *
 * Manages a list of tasks for the user
 *
 * @author Brennan Chrisman
 * @version Nov. 5, 2026
 */

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<Task>();
        startup();
    }

    /**
     * Sets up the task list by reading the list of Task objects in List.txt
     */
    public void startup() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("List.txt"))) {
            while (true) {
                Task task = (Task) ois.readObject();
                tasks.add(task);
            }
        } catch (EOFException e) {

        } catch (ClassNotFoundException e) {
            IO.println(e.getStackTrace());
        } catch (FileNotFoundException e) {
            IO.println(e.getStackTrace());
        } catch (IOException e) {
            IO.println(e.getStackTrace());
        }
    }

    /**
     * Shutsdown the task list and writes the list of tasks to the file List.txt
     */
    public void shutdown() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("List.txt"))) {
            for (Task task : tasks) {
                oos.writeObject(task);
            }
        } catch (FileNotFoundException e) {
            IO.println(e.getStackTrace());
        } catch (IOException e) {
            IO.println(e.getStackTrace());
        }
    }

    /**
     * Adds a Task to tasks in the correct sorting order
     *
     * @param Task task, the task to add
     * @return true if successful, false if the task already exists
     */
    public boolean addTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            int compared = task.getDate().compareTo(tasks.get(i).getDate());
            if (compared == 0 && task.getTitle().equals(tasks.get(i).getTitle())) {
                return false;
            } else if (compared == 0) {
                int titleCompared = task.getTitle().compareTo(tasks.get(i).getTitle());
                if (titleCompared == 0) {
                    return false;
                } else if (titleCompared < 0) {
                    tasks.add(i, task);
                    return true;
                } else {
                    tasks.add(i + 1, task);
                    return true;
                }
            } else if (compared < 0) {
                tasks.add(i, task);
                return true;
            }
        }
        tasks.add(task);
        return true;
    }

    /**
     * Removes the task with the given title
     *
     * @param String title, the title to look for
     * @return boolean, true if successful, false if not found
     */
    public boolean removeTask (String title) {
        for (int i = 0; i < tasks.size(); i++) {
            if (title.equals(tasks.get(i).getTitle())) {
                tasks.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the task with the specified title
     *
     * @param String title, the title to look for
     * @return Task, the task with the specified title
     */
    public Task getTask(String title) {
        for (int i = 0; i < tasks.size(); i++) {
            if (title.equals(tasks.get(i).getTitle())) {
                return tasks.get(i);
            }
        }
        return null;
    }
}