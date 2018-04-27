#!/usr/bin/env bash

if ! command -v microservice ; then
    wget http://resources.cumulocity.com/examples/microservice
    chmod +x microservice
    PATH=$PATH:.
fi

microservice pack -n hello-microservice

rm image.tar