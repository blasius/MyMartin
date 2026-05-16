package rw.martinhardware.mymartin.dao;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.entities.User_;

public class UserDao {

    private final Box<User> userBox;

    public UserDao(BoxStore boxStore) {
        this.userBox = boxStore.boxFor(User.class);
    }

    public long saveUser(User user) {
        return userBox.put(user);
    }

    public User getUserById(long id) {
        return userBox.get(id);
    }

    public User getUserByUuid(String uuid) {
        return userBox.query().equal(User_.uuid, uuid, QueryBuilder.StringOrder.CASE_SENSITIVE).build().findFirst();
    }

    public User getUserByEmail(String email) {
        return userBox.query().equal(User_.email, email, QueryBuilder.StringOrder.CASE_INSENSITIVE).build().findFirst();
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userBox.query().equal(User_.phoneNumber, phoneNumber, QueryBuilder.StringOrder.CASE_SENSITIVE).build().findFirst();
    }

    public User getActiveUser() {
        return userBox.query().equal(User_.isActive, true).build().findFirst();
    }

    public List<User> getAllUsers() {
        return userBox.getAll();
    }

    public void updateUser(User user) {
        userBox.put(user);
    }

    public void deleteUser(User user) {
        userBox.remove(user);
    }

    public void deleteUserById(long id) {
        userBox.remove(id);
    }

    public void deleteAllUsers() {
        userBox.removeAll();
    }

    public void setActiveUser(String uuid) {
        // Deactivate all users first
        List<User> users = getAllUsers();
        for (User user : users) {
            user.setActive(false);
            updateUser(user);
        }

        // Activate the specified user
        User activeUser = getUserByUuid(uuid);
        if (activeUser != null) {
            activeUser.setActive(true);
            updateUser(activeUser);
        }
    }

    public void logout() {
        User activeUser = getActiveUser();
        if (activeUser != null) {
            activeUser.setActive(false);
            activeUser.setToken(null);
            activeUser.setRefreshToken(null);
            updateUser(activeUser);
        }
    }
}
