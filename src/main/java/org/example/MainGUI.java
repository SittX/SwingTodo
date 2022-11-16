package org.example;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.example.Task.*;

public class MainGUI extends JFrame {
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


    public MainGUI() {
        populateComboBox();

        super.setSize(500, 500);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setContentPane(mainPanel);
        super.setVisible(true);
        createTaskBtn.addActionListener(e->{
            String title = newTaskTxtInput.getText();
            String priority = taskPriorityCmb.getSelectedItem().toString();
            create(title,priority);
            newTaskTxtInput.setText("");
        });
        deleteTaskBtn.addActionListener(e->{
            String title = deleteTaskIdTxtField.getText();
            delete(title);
        });
    }

    private void delete(String title) {
        try(Connection con = DriverManager.getConnection(url,user,psw);
            PreparedStatement stat = con.prepareStatement("DELETE FROM Tasks t WHERE t.title = ?");
        ) {
            stat.setString(1,title);
            stat.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateComboBox() {
        taskPriorityCmb.addItem(new TaskPriority(Priority.VERY_EASY));
        taskPriorityCmb.addItem(new TaskPriority(Priority.EASY));
        taskPriorityCmb.addItem(new TaskPriority(Priority.MEDIUM));
        taskPriorityCmb.addItem(new TaskPriority(Priority.HARD));
    }

    public void create(String title,String priority) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("INSERT INTO Tasks(title,priority) VALUES(?,?);")
        ) {
            stat.setString(1, title);
            stat.setString(2, priority);

            stat.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
