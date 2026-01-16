package model;

public abstract class User {
   protected String id;
   protected String username;
   protected String password;
   protected String role; // "STUDENT", "COORDINATOR", "EVALUATOR"

   public User(String id, String username, String password, String role) {
       this.id = id;
       this.username = username;
       this.password = password;
       this.role = role;
   }

   public String getUsername() {
       return username;
   }

   public String getPassword() {
       return password;
   }

   public String getRole() {
       return role;
   }

   public String getId() {
       return id;
   }
}
