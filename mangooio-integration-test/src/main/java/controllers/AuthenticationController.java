package controllers;

import io.mangoo.annotations.FilterWith;
import io.mangoo.annotations.Routing;
import io.mangoo.filters.AuthenticationFilter;
import io.mangoo.filters.oauth.OAuthCallbackFilter;
import io.mangoo.filters.oauth.OAuthLoginFilter;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Authentication;
import io.mangoo.routing.bindings.Form;
import io.mangoo.utils.CodecUtils;

public class AuthenticationController {
    private static final String SECRET = "MyVoiceIsMySecret";
    private static final String AUTHENTICATIONREQUIRED = "/authenticationrequired";

    @FilterWith(AuthenticationFilter.class)
    @Routing(method = "GET", url = "/authenticationrequired")
    public Response notauthenticated(Authentication authentication) {
        return Response.withOk()
                .andTextBody(authentication.getAuthenticatedUser());
    }

    @FilterWith(OAuthLoginFilter.class)
    @Routing(method = {"GET", "POST"}, url = "/login")
    public Response login() {
        return Response.withOk().andEmptyBody();
    }

    @FilterWith(OAuthCallbackFilter.class)
    @Routing(method = "GET", url = "/authenticate")
    public Response authenticate(Authentication authentication) {
        if (authentication.hasAuthenticatedUser()) {
            authentication.validLogin(authentication.getAuthenticatedUser(), "bar", CodecUtils.hexJBcrypt("bar"));
            return Response.withRedirect(AUTHENTICATIONREQUIRED);
        }

        return Response.withOk().andEmptyBody();
    }

    @Routing(method = "POST", url = "/dologin")
    public Response doLogin(Authentication authentication) {
        authentication.validLogin("foo", "bar", CodecUtils.hexJBcrypt("bar"));
        return Response.withRedirect(AUTHENTICATIONREQUIRED);
    }
    
    public Response doLoginTwoFactor(Authentication authentication) {
        authentication.validLogin("foo", "bar", CodecUtils.hexJBcrypt("bar"));
        authentication.twoFactorAuthentication(true);
        
        return Response.withRedirect("/");
    }
    
    public Response factorize(Form form, Authentication authentication) {
        if (authentication.hasAuthenticatedUser() && authentication.validSecondFactor(SECRET, form.getInteger("twofactor").orElse(0))) {
            return Response.withRedirect(AUTHENTICATIONREQUIRED);
        }
        
        return Response.withRedirect("/");
    }

    @Routing(method = "GET", url = "/logout")
    public Response logout(Authentication authentication) {
        authentication.logout();
        return Response.withOk().andEmptyBody();
    }
    
    public Response subject() {
        return Response.withOk();
    }
}