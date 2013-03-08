## vertx-scarecrow

This is a PoC to integrate between Vert.x with PicketLink and the sources are far from perfect. If you have any additions, patches are always welcome!

To start the server simple run the following command:

	mvn vertx:run
	
The server is binding to all interfaces, on your computer/server (localhost).

#### Enroll

	curl -v -H "Accept: application/json" -H "Content-type: application/json" -d '{"username":"john","password":"123"}' -X POST http://localhost:8080/auth/enroll
	
#### Login

	curl -v -H "Accept: application/json" -H "Content-type: application/json" -d '{"username":"john","password":"123"}' -X POST http://localhost:8080/auth/login
	
### TODO

* Implement session management
* Source code refactoring
* Unit tests