package org.example;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Multithread {

    private final String DB_URL = "jdbc:mysql://localhost:3306/MockJSON_db";
    private final String USER = "root";
    private final String PSW = "kstmysql";

    // Fetch employee records from the database
    public void fetchUser(List<Employee> employeeList){
       try(Connection con = DriverManager.getConnection(DB_URL,USER,PSW);
           PreparedStatement stat = con.prepareStatement("SELECT * FROM employee LIMIT 50;");
       ) {
           ResultSet rs = stat.executeQuery();

           System.out.println("Fetching emp data from database.");
           while(rs.next()){
               employeeList.add(new Employee(
                       rs.getInt("id"),
                       rs.getString("firstName"),
                       rs.getString("lastName"),
                       rs.getString("email"),
                       rs.getString("gender"),
                       rs.getString("ph_number"),
                       rs.getString("college")
               ));
               Thread.sleep(2000);
           }

           System.out.println("Fetching emp data from database finished !");
       } catch (SQLException e) {
           throw new RuntimeException(e);
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }
    }

    // serialize the employeeList data into a txt file
    public void serialize(List<Employee> emp){
       try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("emp.txt",true)))){

          output.writeObject(emp);
          Thread.sleep(2000);
           System.out.println("Serializing data to the txt file.");
       } catch (IOException | InterruptedException e) {
           throw new RuntimeException(e);
       }
    }

    // Deserialize the employee data in the txt file to Java objects
    public void deserialize(List<Employee> emp){
        try(ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream("emp.txt")))) {

            // Deserialize the data from txt file
            List<Employee> employeeList = (List<Employee>) input.readObject();

            // Printout each Employee object to the console
            employeeList.stream().forEach(e->{
                System.out.println(e.toString());
            });

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
