import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
 * @version Nov. 13, 2026
 */

public class TaskList {
    private ArrayList<Task> tasks;
    private ArrayList<Task> recurringTasks;

    public TaskList() {
        tasks = new ArrayList<Task>();
        recurringTasks = new ArrayList<Task>();
        startup();
    }

    /**
     * Sets up the task list by reading the list of Task objects in List.txt
     */
    public void startup() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("List.txt"))) {
            while (true) {
                Task task = (Task) ois.readObject();
                if (task.isRecurring()) {
                    recurringTasks.add(task);
                } else {
                    addTask(task);
                }
            }
        } catch (EOFException e) {

        } catch (ClassNotFoundException e) {
            IO.println(e.getStackTrace());
        } catch (FileNotFoundException e) {
            IO.println(e.getStackTrace());
        } catch (IOException e) {
            IO.println(e.getStackTrace());
        }
        //Update the list with any recurring tasks that need to be added
        update();
    }

    /**
     * Shutsdown the task list and writes the list of tasks to the file List.txt
     */
    public void shutdown() {
        //Ensure all recurring tasks are the task list to be saved in List.txt
        for (Task recur : recurringTasks) {
            boolean isAdded = false;
            for (Task task : tasks) {
                if (recur.equals(task)) {
                    isAdded = true;
                }
            }
            if (!isAdded) {
                addTask(recur);
            }
        }
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
                //If the task is recurring, increment its date by 7 days
                if (tasks.get(i).isRecurring()) {
                    for (int j = 0; j < recurringTasks.size(); j++) {
                        if (recurringTasks.get(j).getTitle().equals(title)) {
                            LocalDate nextDate = recurringTasks.get(j).getDate().plusDays(7);
                            recurringTasks.get(j).setDate(nextDate);
                        }
                    }
                }
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

    /**
     * Runs the TaskList to receive and respond to user input until the user quits
     */
    public void run() {
        String input = "";
        IO.println("To add a Task to the list enter 1,Title,YYYY-MM-DD,<Optional true for weekly recurrence>");
        IO.println("To see a task's notes enter 2,Title");
        IO.println("To remove a task enter 3,Title");
        IO.println("To see the task list enter 4");
        IO.println("To add a note to a task enter 5,Title,note");
        IO.println("To update the list with recurring tasks enter 6");
        IO.println("Enter quit to exit the program and save the list");
        IO.println("Enter help to see the list of commands");
        boolean running = true;
        while (running) {
            input = IO.readln("Enter a command: ");
            String[] command;
            if (input.equals("help")) {
                command = new String[] {"help"};
            } else if (input.equals("quit")) {
                command = new String[] {"quit"};
            } else {
                command = input.split(",");
            }
            switch (command[0]) {
                case "1" -> {
                    if (command.length != 3 && command.length != 4) {
                        IO.println("Incorrect number of arguments");
                        IO.println("Expected: 1,Title,YYYY-MM-DD,<Optional true for weekly recurrence>");
                    } else {
                        LocalDate date = LocalDate.now();
                        try {
                            date =  LocalDate.parse(command[2]);
                        } catch (DateTimeParseException e) {
                            IO.println("Invalid Date: enter in the form YYYY-MM-DD");
                        }
                        Task newTask;
                        if (command.length == 4) {
                            newTask = new Task(command[1], date, true);
                            recurringTasks.add(newTask);
                        } else {
                            newTask = new Task(command[1], date);
                        }
                        boolean result = addTask(newTask);
                        if (result) {
                            IO.println("Task added successfully");
                        } else {
                            IO.println("Task could not be added");
                        }
                    }
                }
                case "2" -> {
                    if (command.length != 2) {
                        IO.println("Incorrect number of arguments");
                        IO.println("Expected: 2,Title");
                    } else {
                        Task viewing = getTask(command[1]);
                        if (viewing != null) {
                            IO.println(viewing);
                            IO.println(viewing.getnotes());
                        } else {
                            IO.println("Task could not be viewed");
                        }
                    }
                }
                case "3" -> {
                    if (command.length != 2) {
                        IO.println("Incorrect number of arguments");
                        IO.println("Expected: 2,Title");
                    } else {
                        boolean result = removeTask(command[1]);
                        if (result) {
                            IO.println("Task removed successfully");
                        } else {
                            IO.println("Task could not be removed");
                        }
                    }
                }
                case "4" -> {
                    if (tasks.isEmpty()) {
                        IO.println("Task list is empty");
                    } else {
                        for (Task task : tasks) {
                            IO.println(task);
                        }
                    }

                }
                case "5" -> {
                    if (command.length != 3) {
                        IO.println("Incorrect number of arguments");
                        IO.println("Expected: 5,Title,Note");
                    } else {
                        Task viewing = getTask(command[1]);
                        viewing.addnote(command[2]);
                        IO.println("note added successfully");
                        IO.println(viewing);
                        IO.println(viewing.getnotes());
                    }
                }
                case "6" -> {
                    update();
                    IO.println("Task list updated successfully");
                }
                case "help" -> {
                    IO.println("To add a Task to the list enter 1,Title,YYYY-MM-DD,<Optional true for weekly recurrence>");
                    IO.println("To see a task's notes enter 2,Title");
                    IO.println("To remove a task enter 3,Title");
                    IO.println("To see the task list enter 4");
                    IO.println("To add a note to a task enter 5,Title,note");
                    IO.println("To update the list with recurring tasks enter 6");
                    IO.println("To see this list of commands enter help");
                }
                case "quit" -> {
                    shutdown();
                    IO.println("Goodbye");
                    running = false;
                }
                default -> {
                    IO.println("Command not recognized");
                    IO.println("Enter help to see a list of command");
                }
            }
        }
    }

    /**
     * Checks and adds any recurring tasks that are due to be added to the main task list
     */
    public void update() {
        for (Task recur : recurringTasks) {
            int daysUntil = (int) LocalDate.now().until(recur.getDate(), ChronoUnit.DAYS);
            if (daysUntil <= 4) {
                addTask(recur);
            }
        }
    }

}