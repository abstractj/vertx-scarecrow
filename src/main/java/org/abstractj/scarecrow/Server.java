package org.abstractj.scarecrow;

import org.jboss.aerogear.security.model.AeroGearUser;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.deploy.Verticle;

public class Server extends Verticle {

    public void start() {

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
                        AeroGearUser user = (AeroGearUser) Json.decodeValue(buffer.toString(), AeroGearUser.class);
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
}
