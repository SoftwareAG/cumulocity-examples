#!/usr/bin/env bash

if ! command -v microservice ; then
    wget http://resources.cumulocity.com/examples/microservice
    chmod +x microservice
    PATH=$PATH:.
fi

microservice pack -n sample-microservice

rm image.tar