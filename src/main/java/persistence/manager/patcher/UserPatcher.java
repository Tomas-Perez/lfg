package persistence.manager.patcher;

import persistence.model.User;

/**
 * @author Tomas Perez Molina
 */
public class UserPatcher {
    private String username;
    private String password;
    private String email;
    private boolean admin;

    public UserPatcher(String username, String password, String email, boolean admin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.admin = admin;
    }

    public boolean patchesUsername(){
        return username != null;
    }

    public boolean patchesEmail(){
        return email != null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void patch(User user){
        if(this.username != null) user.setUsername(username);
        if(this.password != null) user.setPassword(password);
        if(this.email != null) user.setEmail(email);
        user.setAdmin(admin);
    }

    public static class Builder{
        private String username;
        private String password;
        private String email;
        private boolean admin = false;

        public Builder withUsername(String username){
            this.username = username;
            return this;
        }

        public Builder withPassword(String password){
            this.password = password;
            return this;
        }

        public Builder withEmail(String email){
            this.email = email;
            return this;
        }

        public Builder admin(){
            this.admin = true;
            return this;
        }

        public UserPatcher build(){
            return new UserPatcher(username, password, email, admin);
        }
    }
}
