package util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.plexus.util.StringUtils;
import restapi.security.authentication.model.AuthenticationToken;
import restapi.security.authentication.model.SignInJSON;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.List;


/**
 * @author Tomas Perez Molina
 */
public class RequestUtil {

    public static AuthenticationToken getToken(URL base, String email, String password) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget signInTarget = client.target(URI.create(new URL(base, "sign-in").toExternalForm()));

        final Response response = signInTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new SignInJSON(
                        email,
                        password))
                );

        return parseResponse(response, AuthenticationToken.class);
    }

    public static <T> List<T> parseListResponse(Response r, Class<T> cls) throws Exception{
        JsonNode node = r.readEntity(JsonNode.class);
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, cls);
        return mapper.readValue(mapper.treeAsTokens(node), type);
    }

    public static <T> T parseResponse(Response r, Class<T> cls) throws Exception{
        JsonNode node = r.readEntity(JsonNode.class);
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructType(cls);
        return mapper.readValue(mapper.treeAsTokens(node), type);
    }

    public static Response get(WebTarget target, AuthenticationToken token){
        return target
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken())
                .get();
    }

    public static <T> Response post(WebTarget target, AuthenticationToken token, T entity){
        return target
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken())
                .post(Entity.json(entity));
    }

    public static Response delete(WebTarget target, AuthenticationToken token){
        return target
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken())
                .delete();
    }

    public static WebTarget newTarget(String uri){
        Client client = ClientBuilder.newClient();
        return client.target(uri);
    }

    public static String getRelativePathDiff(WebTarget t1, WebTarget t2){
        final String path1 = t1.getUri().getPath();
        final String path2 = t2.getUri().getPath();
        final String difference = StringUtils.difference(path1, path2);
        return difference.substring(1);
    }

    public static WebTarget newRelativeTarget(URL base, String path) throws Exception {
        Client client = ClientBuilder.newClient();
        return client.target(URI.create(new URL(base, path).toExternalForm()));
    }
}
