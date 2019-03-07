REM create keystore file
REM keypass and storepass must be the same
REM keytool -list -rfc -keystore cert.jks -storepass P@ssw0rd
keytool -genkey -alias gavin -keypass P@ssw0rd -keyalg RSA -keysize 1024 -validity 365 -keystore cert.jks -storepass P@ssw0rd -dname "CN=*.lugavin.io, OU=Gavin Software, O=Gavin Ltd., L=ZH, ST=GD, C=CN"

REM export cert file 
keytool -export -alias gavin -file cert.cer -keystore cert.jks -storepass P@ssw0rd