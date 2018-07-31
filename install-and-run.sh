#!/usr/bin/env bash

function print() {
    echo
    echo "--> $1"
    echo
}

if [ $(basename $PWD) != "assessment-betvictor" ]; then
    echo "Current directory must be assessment-betvictor!"
    exit 1
fi

print "installing Action Monitor"
(cd application &&
 ./gradlew clean build --refresh-dependencies &&
 docker build -t betvictor-action-monitor .)

print "installing Client"
(cd client &&
 ./gradlew clean build --refresh-dependencies &&
 docker build -t betvictor-client .)

print "starting up services"
USER_HOSTNAME=$(hostname)
export HOSTNAME=${USER_HOSTNAME:-localhost}
docker-compose -f files/docker-compose.yml -f files/action-monitor-compose.yml down -d
docker-compose -f files/docker-compose.yml -f files/action-monitor-compose.yml up -d

print "applications are up and running at"
echo "Redis Commander: http://${HOSTNAME}:8081"
echo "Action Monitor: http://${HOSTNAME}:9000/actuator/health"
echo "Client: http://${HOSTNAME}:8080/action-monitor"