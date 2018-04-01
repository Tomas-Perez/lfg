package restapi.services;

import model.User;
import restapi.SimpleJSONObject;
import utils.UserManager;
import utils.dbConnection.FactoryProvider;

import javax.persistence.NoResultException;

/**
 * @author Tomas Perez Molina
 */
public class LoginService {
    private UserManager userManager;

    public LoginService() {
        this.userManager = new UserManager(FactoryProvider.getFactory());
    }

    public SimpleJSONObject login(String email, String password){
        User user;
        SimpleJSONObject result;

        try{
            user = userManager.getByEmail(email);
            if(password.equals(user.getPassword()))
                result = new SimpleJSONObject(SimpleJSONObject.Status.SUCCESS, user, null);
            else
                result = new SimpleJSONObject(new SimpleJSONObject.Error(0, "Wrong password"));
        } catch (NoResultException e){
            result = new SimpleJSONObject(new SimpleJSONObject.Error(0, "No user"));
        }

        return result;
    }
}
