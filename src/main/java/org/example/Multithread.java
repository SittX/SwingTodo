package org.example;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Multithread {
    // Read data from the database
    // Write data to a file
    // Fetch data from the API
    private final String DB_URL = "jdbc:mysql://localhost:3306/MockJSON_db";
    private final String USER = "root";
    private final String PSW = "kstmysql";

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

    public void serialize(List<Employee> emp){
       try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("emp.txt",true)))){

          output.writeObject(emp);
          Thread.sleep(2000);
           System.out.println("Serializing data to the txt file.");
       } catch (IOException e) {
           throw new RuntimeException(e);
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }
    }

    public void deserialize(List<Employee> emp){
        try(ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream("emp.txt")))) {

            List<Employee> employeeList = (List<Employee>) input.readObject();

            employeeList.stream().forEach(e->{
                System.out.println(e.toString());
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
