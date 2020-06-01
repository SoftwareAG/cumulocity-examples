#!/bin/bash

set -x

SUBJECT="/C=EU/ST=PL/O=Iot Device Factory/CN=IotDevFactory"
FILENAME_PREFIX=iot

# delete them all
rm $FILENAME_PREFIX-private-key.pem $FILENAME_PREFIX-cert-sign-request.pem $FILENAME_PREFIX-cert.pem || echo "nothing to delete, continue"

#generate private key
openssl genrsa -out $FILENAME_PREFIX-private-key.pem 2048

#create certificate sign request
openssl req -new \
    -key $FILENAME_PREFIX-private-key.pem \
    -out $FILENAME_PREFIX-cert-sign-request.pem \
    -extensions v3_req \
    -subj "$SUBJECT"
    
#sign request
openssl x509 -in $FILENAME_PREFIX-cert-sign-request.pem \
    -out $FILENAME_PREFIX-cert.pem \
    -req -signkey $FILENAME_PREFIX-private-key.pem \
    -extensions v3_req \
    -extfile intermediate-config.cnf \
    -days 3650

#describe generated certificate
openssl x509 -in $FILENAME_PREFIX-cert.pem -text
