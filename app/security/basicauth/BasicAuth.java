package security.basicauth;

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

public class BasicAuth {

    protected UnauthorizedAction unauthorizedAction;
    protected Validator validator;

    public BasicAuth(String realm, Validator validator) {
        unauthorizedAction = new UnauthorizedAction(realm);
        this.validator = validator;
    }

    public boolean authenticate(Request request, Method actionMethod) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null) {
            return false;
        }
        String auth = authHeader.substring(6);
        try {
            byte[] decodedAuth = new sun.misc.BASE64Decoder().decodeBuffer(auth);
            String[] credString = new String(decodedAuth, "UTF-8").split(":");

            if (credString == null || credString.length != 2) {
                return false;
            }

            String username = credString[0];
            String password = credString[1];

            if(!validator.validate(username, password)) {
                return false;
            }

            return true;
        }
        catch(IOException e) {
            return false;
        }
    }

    public Action getUnauthorizedAction() {
        return unauthorizedAction;
    }

    public static class UnauthorizedAction extends Action {

        protected String realm;
        
        public UnauthorizedAction(String realm) {
            this.realm = "Basic realm=\"" + realm + "\"";
        }

        @Override
        public Result call(Http.Context context) throws Throwable {
            context.response().setHeader(WWW_AUTHENTICATE, realm);
            return unauthorized();
        }
    }
}