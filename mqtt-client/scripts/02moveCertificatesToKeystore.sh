#!/bin/bash

set -x

#copy all certificates in pem format to single file
NAME=iot-device-0001
FILES_WITH_CERTIFICATES="$NAME-cert.pem iot-cert.pem"

rm chain-$NAME.pem chain-with-private-key-$NAME.p12 chain-with-private-key-$NAME.jks || echo "Nothing to remove"

cat $FILES_WITH_CERTIFICATES > chain-$NAME.pem

#create certificates chain with private key
openssl pkcs12 \
	-password pass:changeit \
	-export \
	-inkey $NAME-private-key.pem \
	-in chain-$NAME.pem \
	-out chain-with-private-key-$NAME.p12

#if you need it in jks format
keytool -importkeystore \
	-srckeystore chain-with-private-key-$NAME.p12 \
	-destkeystore chain-with-private-key-$NAME.jks \
	-srcstoretype pkcs12  -deststoretype jks \
	-srcstorepass changeit \
	-deststorepass changeit

keytool -list -storepass changeit -keystore chain-with-private-key-$NAME.jks

echo "To display detailed information about keystore type: keytool -list -storepass changeit -keystore chain-with-private-key-$NAME.jks -v"
