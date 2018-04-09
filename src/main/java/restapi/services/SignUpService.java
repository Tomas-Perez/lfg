package restapi.services;

import managers.ConstraintException;
import restapi.pojos.SignUpResponse;
import restapi.pojos.SimpleJSONObject;
import managers.UserManager;
import managers.FactoryProvider;

/**
 * @author Tomas Perez Molina
 */
public class SignUpService {
    private UserManager userManager;

    public SignUpService() {
        this.userManager = new UserManager(FactoryProvider.getFactory());
    }

    public SimpleJSONObject signUp(String email, String password, String username){
        SimpleJSONObject result;

        try {
            Integer id = userManager.addUser(username, password, email, false);
            SignUpResponse r = new SignUpResponse(id);
            result = new SimpleJSONObject(SimpleJSONObject.Status.SUCCESS, r, null);
        } catch (ConstraintException e) {
            result = new SimpleJSONObject(new SimpleJSONObject.Error(0, e.getMessage()));
        }

        return result;
    }
}
