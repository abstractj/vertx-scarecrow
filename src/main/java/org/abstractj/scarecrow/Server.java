package org.abstractj.scarecrow;

import org.abstractj.scarecrow.config.Persistence;
import org.abstractj.scarecrow.config.PicketLink;
import org.abstractj.scarecrow.model.User;
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.internal.DefaultIdentityManager;
import org.picketlink.idm.internal.DefaultIdentityStoreInvocationContextFactory;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.internal.DefaultIdentity;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.deploy.Verticle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Server extends Verticle {

    public void start() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();

        entityManager.getTransaction().begin();

        //PicketLink
        IdentityManager identityManager = new DefaultIdentityManager();
        IdentityConfiguration config = new IdentityConfiguration();
        config.addStoreConfiguration(PicketLink.getIdentityStoreConfiguration());

        DefaultIdentityStoreInvocationContextFactory icf = new DefaultIdentityStoreInvocationContextFactory(factory);

        icf.setEntityManager(entityManager);
        identityManager.bootstrap(config, icf);

        create(identityManager);

        login(identityManager);

        entityManager.getTransaction().commit();
        entityManager.close();
        factory.close();


        RouteMatcher rm = new RouteMatcher();

        rm.get("/cars", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response.end("You requested cars");
            }
        });

        rm.post("/auth/login", new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                req.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        User user = (User) Json.decodeValue(buffer.toString(), User.class);
                        req.response.end(user.getUsername());
                    }
                });
            }
        });


        rm.post("/auth/logout", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response.end("Logout");
            }
        });

        rm.post("/auth/enroll", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response.end("Register");
            }
        });

        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                final String appName = "/scarecrow";
                String file = req.path.equals(appName) ? "index.html" : req.path;
                req.response.sendFile("webapp/" + file);
            }
        }).requestHandler(rm).listen(8080);

    }


    private void login(IdentityManager identityManager){

        UsernamePasswordCredentials credential = new UsernamePasswordCredentials();
        credential.setUsername("john");
        credential.setPassword(new Password("123"));

        identityManager.validateCredentials(credential);

        System.out.println("Logged in? " + credential.getStatus());

    }
    private void create(IdentityManager identityManager){
        org.picketlink.idm.model.User user = new SimpleUser("john");

        user.setEmail("john@doe.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        /*
         * Note: Password will be encoded in SHA-512 with SecureRandom-1024 salt
         * See http://lists.jboss.org/pipermail/security-dev/2013-January/000650.html for more information
         */
        identityManager.add(user);
        identityManager.updateCredential(user, new Password("123"));

        Role roleDeveloper = new SimpleRole("simple");
        Role roleAdmin = new SimpleRole("admin");

        identityManager.add(roleDeveloper);
        identityManager.add(roleAdmin);

        identityManager.grantRole(user, roleDeveloper);
        identityManager.grantRole(user, roleAdmin);
    }


}
