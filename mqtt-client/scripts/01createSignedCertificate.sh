#!/bin/bash

set -x

SIGNING_CERT_FILE_NAME_PREFIX=iot
SUBJECT="/C=EU/ST=PL/O=device/CN=iotdevice0001"
FILENAME_PREFIX=iot-device-0001
CONFIG_FILE=end-user-config.cnf

#remove them all
rm $FILENAME_PREFIX-private-key.pem $FILENAME_PREFIX-cert-sign-request.pem $FILENAME_PREFIX-cert.pem || echo "Nothing to delete"

#generate private key
openssl genrsa -out $FILENAME_PREFIX-private-key.pem 2048

#create certificate sign request
openssl req -new \
    -key $FILENAME_PREFIX-private-key.pem \
    -out $FILENAME_PREFIX-cert-sign-request.pem \
    -extensions v3_req \
    -subj "$SUBJECT"

#sign cert request
openssl x509 -req \
    -CA $SIGNING_CERT_FILE_NAME_PREFIX-cert.pem \
    -CAkey $SIGNING_CERT_FILE_NAME_PREFIX-private-key.pem \
    -in $FILENAME_PREFIX-cert-sign-request.pem \
    -out $FILENAME_PREFIX-cert.pem \
    -days 730 \
    -extensions v3_req \
    -extfile $CONFIG_FILE \
    -CAcreateserial
    
#describe generated certificate
openssl x509 -in $FILENAME_PREFIX-cert.pem -text
