package org.example;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainGUI extends JFrame {
    private final String database = "SwingTodo";
    private final String url = "jdbc:mysql://localhost:3306/" + database;
    private final String user = "root";
    private final String psw = "kstmysql";
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel bottomPanel;
    private JPanel rightPanel;
    private JPanel centerPanel1;
    private JButton createTaskBtn;
    private JButton updateTaskBtn;
    private JButton deleteTaskBtn;
    private JTabbedPane tabbedPane1;
    private JTextField newTaskTxtInput;
    private JComboBox<TaskPriority> taskPriorityCmb;
    private JTextField deleteTaskIdTxtField;
    private JLabel resultLabel;
    private JButton getTasksBtn;
    private JPanel viewAllTasksPanel;
    private JPanel updateTaskPanel;
    private JPanel deleteTaskPanel;
    private JPanel createTaskPanel;
    private JTextField oldTaskTitleTxtField;
    private JTextField newTaskTitleTxtField;
    private JPanel multithreadingPanel;
    private JButton multithreadBtn;

    public MainGUI() {
        populateComboBox();

        createTaskBtn.addActionListener(e -> {
            Thread t1 = new Thread(() -> {
                String title = newTaskTxtInput.getText();
                String priority = taskPriorityCmb.getSelectedItem().toString();
                create(title, priority);
                newTaskTxtInput.setText("");
            });
            t1.setName("Task creation thread");
            t1.start();
        });

        deleteTaskBtn.addActionListener(e -> {
            Thread t1 = new Thread(() -> {
                String title = deleteTaskIdTxtField.getText();
                delete(title);
                deleteTaskIdTxtField.setText("");
            });
            t1.setName("Task deletion thread");
            t1.start();
        });

        getTasksBtn.addActionListener(e -> {
            Thread t1 = new Thread(() -> {
                fetch(viewAllTasksPanel);
            });
            // Remove all the items in the viewAllTasksPanel
            viewAllTasksPanel.removeAll();

            t1.start();
            try {
                t1.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // Revalidate new  items and redrew the panel
            viewAllTasksPanel.revalidate();
            viewAllTasksPanel.repaint();
        });

        updateTaskBtn.addActionListener(e -> {
                    String oldTitle = oldTaskTitleTxtField.getText();
                    String newTitle = newTaskTitleTxtField.getText();
                    update(oldTitle, newTitle);
                }
        );

        // This action has multithreading features and it is really important
        multithreadBtn.addActionListener(e -> {

            // Create a thread-safe synchronizedList
            List<Employee> employeeList = Collections.synchronizedList(new ArrayList<>());

            // Create threads for each process ( Fetching, Serializing, Deserializing )
            Thread fetchingThread = new Thread(() -> {
                System.out.println("Current Thread " + Thread.currentThread().getName());
                Multithread obj = new Multithread();
                obj.fetchUser(employeeList);

            });

            Thread serializingThread = new Thread(() -> {
                System.out.println("Current Thread " + Thread.currentThread().getName());
                Multithread obj = new Multithread();
                obj.serialize(employeeList);
            });

            Thread deserializingThread = new Thread(() -> {
                System.out.println("Current Thread " + Thread.currentThread().getName());
                Multithread obj = new Multithread();
                obj.deserialize(employeeList);
            });

            // This is main thread for managing all the above 3 threads
            // If we don't create this main thread, the above 3 threads will be managed by the program's main thread, and they will not be in multithreading mode since each thread depends on each other.
            // The program will be halted and unresponsive while executing those threads.
            // To avoid this , we create a new parent thread that will manage those 3 threads.
            // The new parent thread will be managed by main thread.
            // This allows the program's main thread to be free after starting the mainThread.
            Thread mainThread = new Thread(()->{
            fetchingThread.start();
            try {
                fetchingThread.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            System.out.println("Thread before starting serializing thread " + Thread.currentThread().getName());
            serializingThread.start();

            try {
                serializingThread.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            System.out.println("Thread before starting deserializing thread " + Thread.currentThread().getName());
            deserializingThread.start();
            });

            mainThread.start();
            System.out.println("Thread after all the threads died " + Thread.currentThread().getName());
        });
        super.setSize(500, 500);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setContentPane(mainPanel);
        super.setVisible(true);
    }

    public void delete(String title) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("DELETE FROM Tasks t WHERE t.title = ?")
        ) {
            stat.setString(1, title);
            int affectedRowCount = stat.executeUpdate();
            resultLabel.setText(affectedRowCount + " rows are affected.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateComboBox() {
        taskPriorityCmb.addItem(new TaskPriority(Priority.VERY_EASY));
        taskPriorityCmb.addItem(new TaskPriority(Priority.EASY));
        taskPriorityCmb.addItem(new TaskPriority(Priority.MEDIUM));
        taskPriorityCmb.addItem(new TaskPriority(Priority.HARD));
    }

    public void create(String title, String priority) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("INSERT INTO Tasks(title,priority) VALUES(?,?);")
        ) {
            stat.setString(1, title);
            stat.setString(2, priority);

            int status = stat.executeUpdate();
            if (status > 0) {
                resultLabel.setText("New Task is inserted into the database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fetch(JPanel parentPanel) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("SELECT * FROM Tasks")) {
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                JLabel title = new JLabel(rs.getString("title"));
                JLabel priority = new JLabel(rs.getString("priority"));
                parentPanel.add(title);
                parentPanel.add(priority);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String oldTaskTitle, String newTaskTitle) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("UPDATE Tasks SET title = ? WHERE title = ?")) {
            stat.setString(1, newTaskTitle);
            stat.setString(2, oldTaskTitle);

            int result = stat.executeUpdate();
            if (result > 0) {
                resultLabel.setText("Task is updated.");
            } else {
                resultLabel.setText("Update Operation failed.Please try again.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
