import security.basicauth.*;

import java.lang.reflect.Method;

import play.Application;
import play.Logger;
import play.GlobalSettings;
import play.Configuration;
import play.mvc.Http.Request;
import play.mvc.Action;

public class Global extends GlobalSettings {

    Boolean protectAll;
    BasicAuth authenticator;

    @Override
    public void onStart(Application app) {
        Configuration configuration = app.configuration();
        protectAll = configuration.getBoolean("basicAuth.protectAll");
        if(protectAll == null) {
            protectAll = false;
        }
        if(protectAll) {
            authenticator = new BasicAuth(configuration.getString("basicAuth.realm"),new AnythingGoesValidator());
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