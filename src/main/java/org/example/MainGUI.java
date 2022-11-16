package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MainGUI extends JFrame {
    public String database = "SwingTodo";
    public String url = "jdbc:mysql://localhost:3306/" + database;
    public String user = "root";
    public String psw = "kstmysql";
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

    public MainGUI() {
        populateComboBox();


        createTaskBtn.addActionListener(e -> {
            String title = newTaskTxtInput.getText();
            String priority = taskPriorityCmb.getSelectedItem().toString();
            create(title, priority);
            newTaskTxtInput.setText("");
        });
        deleteTaskBtn.addActionListener(e -> {
            String title = deleteTaskIdTxtField.getText();
            delete(title);
            deleteTaskIdTxtField.setText("");
        });
        getTasksBtn.addActionListener(e ->{
            // Remove all the items in the viewAllTasksPanel
            viewAllTasksPanel.removeAll();

            fetch(viewAllTasksPanel);

            // Revalidate new  items and redrew the panel
            viewAllTasksPanel.revalidate();
            viewAllTasksPanel.repaint();
        });

        super.setSize(500, 500);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setContentPane(mainPanel);
        super.setVisible(true);
        updateTaskBtn.addActionListener(e->{
            String oldTitle= oldTaskTitleTxtField.getText();
            String newTitle = newTaskTitleTxtField.getText();
           update(oldTitle,newTitle);
                }
        );
    }

    private void delete(String title) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("DELETE FROM Tasks t WHERE t.title = ?")
        ) {
            stat.setString(1, title);
            int affectedRowCount =stat.executeUpdate();
            resultLabel.setText(affectedRowCount + " rows are affected.");
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

    public void create(String title, String priority) {
        try (Connection con = DriverManager.getConnection(url, user, psw);
             PreparedStatement stat = con.prepareStatement("INSERT INTO Tasks(title,priority) VALUES(?,?);")
        ) {
            stat.setString(1, title);
            stat.setString(2, priority);

            int status = stat.executeUpdate();
            if(status > 0){
resultLabel.setText("New Task is inserted into the database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fetch(JPanel parentPanel){
       try(Connection con = DriverManager.getConnection(url,user,psw);
       PreparedStatement stat = con.prepareStatement("SELECT * FROM Tasks");){
           ResultSet rs = stat.executeQuery();
           while(rs.next()){
               JLabel title = new JLabel(rs.getString("title"));
               JLabel  priority = new JLabel(rs.getString("priority"));
               parentPanel.add(title);
               parentPanel.add(priority);
           }

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }

    public void update(String oldTaskTitle,String newTaskTitle){
        try(Connection con = DriverManager.getConnection(url,user,psw);
        PreparedStatement stat = con.prepareStatement("UPDATE Tasks SET title = ? WHERE title = ?");){
           stat.setString(1,newTaskTitle);
           stat.setString(2,oldTaskTitle);

          int result = stat.executeUpdate();
          if(result > 0){
              resultLabel.setText("Task is updated.");
          }else{
              resultLabel.setText("Update Operation failed.Please try again.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
