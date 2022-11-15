openssl req -new -newkey rsa:4096 -keyout iot-core.key  -out iot-core.csr
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in iot-core.csr -out iot-core.crt -days 365 -CAcreateserial -extfile iot-core.ext
openssl pkcs12 -export -out iot-core.p12 -name "iot-core" -inkey iot-core.key -in iot-core.crt
keytool -importkeystore -srckeystore iot-core.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype JKS