package entry.AccessControl;

public class User {
    protected final String username;
    protected final Role role;

    public User(String username, Role role) 
    { 
        this.username = username;
        this.role = role; 
    }

    public String toString() 
    { 
        return username + "(" + role + ")"; 
    }
}
