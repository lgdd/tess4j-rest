dev:
	docker-compose up --build

quarkusDev:
	./mvnw quarkus:dev

jar:
	./mvnw package

legacyJar:
	./mvnw package -Dquarkus.package.type=legacy-jar
