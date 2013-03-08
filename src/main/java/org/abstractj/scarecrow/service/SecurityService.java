package org.abstractj.scarecrow.service;

import org.abstractj.scarecrow.config.IdentityManagerFactory;
import org.abstractj.scarecrow.model.User;
import org.picketlink.authentication.Authenticator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;

import static org.abstractj.scarecrow.util.PersistenceUtil.*;

public class SecurityService {


    private final IdentityManagerFactory factory;
    private final IdentityManager identityManager;

    public SecurityService() {
        factory = new IdentityManagerFactory();
        identityManager = factory.create();
    }

    public void init() {
        getTransaction();
        Role roleDeveloper = new SimpleRole("simple");
        Role roleAdmin = new SimpleRole("admin");

        identityManager.add(roleDeveloper);
        identityManager.add(roleAdmin);
        getTransaction().commit();
    }

    public void login(User user) {

        UsernamePasswordCredentials credential = new UsernamePasswordCredentials();
        credential.setUsername(user.getUsername());
        credential.setPassword(new Password(user.getPassword()));

        getTransaction();
        identityManager.validateCredentials(credential);
        getTransaction().commit();

        if (credential.getStatus() != Credentials.Status.VALID)
            throw new RuntimeException("Authentication Failed!");

    }

    public void create(User newUser) {

        org.picketlink.idm.model.User user = new SimpleUser(newUser.getUsername());

//        user.setEmail("john@doe.com");
//        user.setFirstName("John");
//        user.setLastName("Doe");

        /*
         * Note: Password will be encoded in SHA-512 with SecureRandom-1024 salt
         * See http://lists.jboss.org/pipermail/security-dev/2013-January/000650.html for more information
         */
        getTransaction();
        identityManager.add(user);
        identityManager.updateCredential(user, new Password(newUser.getPassword()));

        Role roleDeveloper = identityManager.getRole("simple");
        Role roleAdmin = identityManager.getRole("admin");

        identityManager.grantRole(user, roleDeveloper);
        identityManager.grantRole(user, roleAdmin);

        getTransaction().commit();
    }
}
