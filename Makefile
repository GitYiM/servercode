default: build
build:
	mvn -DskipTests clean package
image: build
	docker build -f Dockerfile -t unnetdisk-server:latest .
run: build
	java -jar target/unnetdisk-server-1.0.0.jar