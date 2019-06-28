package models;

import java.util.ArrayList;

public class UserManager {
    private static UserManager instance = null;
    private User currentUser;
    private ArrayList<User> users = new ArrayList<>();


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private UserManager(){
        for (int i = 0; i < User.numUsers; i++) {
            users.add(new User(i));
        }
        currentUser = users.get(0);
    }

    public static UserManager getInstance(){
        if (instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public User get(int idx){
        return users.get(idx);
    }

    public User getUserByName(String name){
        for (User user : users){
            if (user.getName().equals(name)){
                return user;
            }
        }
        return null;
    }

    public int size(){
        return users.size();
    }
}
