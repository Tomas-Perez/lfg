package restapi.services;

import managers.ConstraintException;
import restapi.pojos.RegisterResponse;
import restapi.pojos.SimpleJSONObject;
import managers.UserManager;
import managers.FactoryProvider;

/**
 * @author Tomas Perez Molina
 */
public class RegisterService {
    private UserManager userManager;

    public RegisterService() {
        this.userManager = new UserManager(FactoryProvider.getFactory());
    }

    public SimpleJSONObject register(String email, String password, String username){
        SimpleJSONObject result;

        try {
            Integer id = userManager.addUser(username, password, email, false);
            RegisterResponse r = new RegisterResponse(id);
            result = new SimpleJSONObject(SimpleJSONObject.Status.SUCCESS, r, null);
        } catch (ConstraintException e) {
            result = new SimpleJSONObject(new SimpleJSONObject.Error(0, e.getMessage()));
        }

        return result;
    }
}
