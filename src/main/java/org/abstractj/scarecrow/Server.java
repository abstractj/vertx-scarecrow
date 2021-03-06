/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.abstractj.scarecrow;

import org.abstractj.scarecrow.model.User;
import org.abstractj.scarecrow.service.SecurityService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.deploy.Verticle;

public class Server extends Verticle {

    public void start() {

        final SecurityService securityService = new SecurityService();

        securityService.init();

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
                        securityService.login(user);
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
            public void handle(final HttpServerRequest req) {
                req.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        User user = (User) Json.decodeValue(buffer.toString(), User.class);
                        securityService.create(user);
                        req.response.end(user.getUsername());
                    }
                });
            }
        });

        // static files:
        rm.getWithRegEx(".*", new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                if (req.uri.matches("/")) {
                    req.response.sendFile("webapp/index.html");
                } else {
                    // meh...
                    req.response.sendFile("webapp/" + req.path);
                }
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
