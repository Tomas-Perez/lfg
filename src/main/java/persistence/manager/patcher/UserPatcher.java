package persistence.manager.patcher;

import persistence.entity.UserEntity;

/**
 * @author Tomas Perez Molina
 */
public class UserPatcher {
    private String username;
    private String password;
    private String email;
    private String image;
    private boolean admin;

    public UserPatcher(String username, String password, String email, boolean admin, String image) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.admin = admin;
        this.image = image;
    }

    public boolean patchesUsername(){
        return username != null;
    }

    public boolean patchesEmail(){
        return email != null;
    }

    public boolean patchesImage(){
        return image != null;
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

    public void patch(UserEntity user){
        if(patchesUsername()) user.setUsername(username);
        if(this.password != null) user.setPassword(password);
        if(patchesEmail()) user.setEmail(email);
        if(patchesImage()) user.setImage(image);
        user.setAdmin(admin);
    }

    public static class Builder{
        private String username;
        private String password;
        private String email;
        private boolean admin = false;
        private String image;

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

        public Builder withImage(String image){
            this.image = image;
            return this;
        }

        public Builder admin(){
            this.admin = true;
            return this;
        }

        public UserPatcher build(){
            return new UserPatcher(username, password, email, admin, image);
        }
    }
}
