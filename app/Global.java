import authentication.basicauth.*;

import java.io.IOException;
import java.lang.reflect.Method;

import play.Application;
import play.Logger;
import play.GlobalSettings;
import play.Configuration;
import play.mvc.Http.Request;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import static play.mvc.Http.HeaderNames.WWW_AUTHENTICATE;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Action.Simple.unauthorized;

public class Global extends GlobalSettings {

    boolean protectAll;
    BasicAuth authenticator;

    @Override
    public void onStart(Application app) {
        Configuration configuration = app.configuration();
        protectAll = configuration.getBoolean("basicAuth.protectAll");
        if(protectAll) {
            authenticator = new BasicAuth(configuration.getString("basicAuth.realm"),
                new Validator() {
                public boolean validate(String username, String password) {
                    return true;
                }
            });
            Logger.info("All requests will be protected with basic authentication");
        }
    }  

    @Override
    public Action onRequest(Request request, Method actionMethod) {
        if(protectAll && !authenticator.authenticate(request, actionMethod)) {
            return authenticator.getUnauthorizedAction();
        }

        return super.onRequest(request, actionMethod);
    }
}