package it.ivert.kc.events;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class LoginEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(LoginEventListenerProvider.class);

    private final KeycloakSession session;

    public LoginEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (!EventType.LOGIN.equals(event.getType())) {
            return;
        }

        log.debug(LoginEventListenerProvider.toString(event));

        String realmId = event.getRealmId();
        String userId = event.getUserId();

        RealmModel realmModel = session.realms().getRealm(realmId);
        UserModel userModel = session.users().getUserById(realmModel, userId);

        userModel.setSingleAttribute("loginDate", String.valueOf(event.getTime()));
        userModel.setSingleAttribute("loginIP", event.getIpAddress());
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {}

    @Override
    public void close() {}

    static String toString(Event event) {
        String str  = "id: " + event.getId() + "\n"
                + "clientId: " + event.getClientId() + "\n"
                + "realmId: " + event.getRealmId() + "\n"
                + "type: " + event.getType() + "\n"
                + "error: " + event.getError() + "\n"
                + "ipAddress: " + event.getIpAddress() + "\n"
                + "sessionId: " + event.getSessionId() + "\n"
                + "userId: " + event.getUserId() + "\n"
                + "time: " + event.getTime() + "\n"
                + "details: ";

        for (String k : event.getDetails().keySet()) {
            str  += "\n" + k + ": " + event.getDetails().get(k);
        }

        return str;
    }
}
