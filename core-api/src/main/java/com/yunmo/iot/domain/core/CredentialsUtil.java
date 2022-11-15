package com.yunmo.iot.domain.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.io.CharStreams;
import com.yunmo.domain.common.Problems;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static com.yunmo.iot.domain.core.CredentialFormat.RSA_PEM;

/**
 * 这个里面暂时写死了一些东西
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CredentialsUtil {
    public static final String ID_CLAIM = "id";

    private static final KeyPairGenerator keyGen;

    private static final PrivateKey rootCaPrivateKey;
    private static final PublicKey rootCaPublicKey;

    private static final String BC_PROVIDER = "BC";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";


    static {
        try {
            keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            rootCaPublicKey = getRootCaPublicKey();
            rootCaPrivateKey = getRootCaPrivateKey();
        } catch (NoSuchAlgorithmException e) {
            throw Problems.hint(e.getMessage());
        }
        keyGen.initialize(2048);

    }

    @SneakyThrows
    private static PublicKey getRootCaPublicKey() {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:store/rootCA.crt"))) {
            X509Certificate cert = (X509Certificate) cf.generateCertificate(fileInputStream);
            return cert.getPublicKey();
        }
    }
    @SneakyThrows
    private static PrivateKey getRootCaPrivateKey(){
        File file = ResourceUtils.getFile("classpath:store/rootCA.key");
        try (FileInputStream inputStream = new FileInputStream(file)) {
            try (Reader reader = new InputStreamReader(inputStream)) {
                return PrivateKeyUtil.loadKey(CharStreams.toString(reader));
            }
        }
    }
    /**
     * openssl genpkey -algorithm RSA -out rsa_private.pem -pkeyopt rsa_keygen_bits:2048
     * openssl rsa -in rsa_private.pem -pubout -out rsa_public.pem
     * @return
     */
    @SneakyThrows
    public static Credentials createCredentials() {
        KeyPair kp = keyGen.generateKeyPair();
        String base64PublicKey = Base64.getMimeEncoder().encodeToString( kp.getPublic().getEncoded());
        String publicKeyPem = String.format("-----BEGIN PUBLIC KEY-----%n%s%n-----END PUBLIC KEY-----",
                base64PublicKey);
        return new Credentials(RSA_PEM, publicKeyPem);
    }

    public static Credentials createCredentials(Long hubId) {
        KeyPair kp = keyGen.generateKeyPair();
        String base64PrivateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        String publicKeyPem = String.format("-----BEGIN PRIVATE KEY-----%n%s%n-----END PRIVATE KEY-----",
                base64PrivateKey);
        String crtPem = generateClientCrt(rootCaPrivateKey, rootCaPublicKey, kp, String.valueOf(hubId));
        return new Credentials(RSA_PEM, publicKeyPem, crtPem);
    }

    @SneakyThrows
    private static String generateClientCrt(PrivateKey rootCaPrivateKey,
                                           PublicKey rootCaPublicKey,
                                           KeyPair issuedCertKeyPair,
                                           String commonName){

        Security.addProvider(new BouncyCastleProvider());
        X500Name rootCertIssuer = new X500Name("CN=yunmo");
        X500Name issuedCertSubject = new X500Name("CN=" + commonName);
        BigInteger issuedCertSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        PrivateKey subPrivateKey = issuedCertKeyPair.getPrivate();
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, issuedCertKeyPair.getPublic());
        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);

        // Sign the new KeyPair with the root cert Private Key
        ContentSigner csrContentSigner = csrBuilder.build(rootCaPrivateKey);
        PKCS10CertificationRequest csr = p10Builder.build(csrContentSigner);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.YEAR, 10);
        Date endDate = calendar.getTime();

        // Use the Signed KeyPair and CSR to generate an issued Certificate
        // Here serial number is randomly generated. In general, CAs use
        // a sequence to generate Serial number and avoid collisions
        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(rootCertIssuer, issuedCertSerialNum, startDate, endDate, csr.getSubject(), csr.getSubjectPublicKeyInfo());

        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        X509Certificate issuedCert  = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(issuedCertHolder);

        // Verify the issued cert signature against the root (issuer) cert
        issuedCert.verify(rootCaPublicKey, BC_PROVIDER);

//        writePrivateKeyToFileBase64Encoded(subPrivateKey,"clientBob.key");
//        exportKeyPairToKeystoreFile(issuedCertKeyPair, issuedCert, "clientBob", "clientBob.p12", "PKCS12", "pass");
//        return writeCertToFileBase64Encoded(issuedCert, "clientBob.crt");
        return String.format("-----BEGIN CERTIFICATE-----%n%s%n-----END CERTIFICATE-----",
                org.bouncycastle.util.encoders.Base64.toBase64String(issuedCert.getEncoded()));
    }


    public static String createJwt(DeviceHub hub, Device device) {
        Credentials credentials = Optional.ofNullable(device.getCredentials())
                .orElse(hub.getCredentials());

        return createJwt(buildAlgorithm(credentials), device.getId());
    }

    public static DecodedJWT verifyAndDecodeJwt(DeviceHub hub, Device device, String jwtToken) {
        Credentials credentials = Optional.ofNullable(device.getCredentials())
                .orElse(hub.getCredentials());
        JWTVerifier verifier = JWT.require(buildAlgorithm(credentials))
                .withClaim(ID_CLAIM, device.getId())
                .build();
        return verifier.verify(jwtToken);
    }

    @SneakyThrows
    private static Algorithm buildAlgorithm(Credentials credentials) {
        String secret = credentials.getSecret();
        if(credentials.getFormat().equals(RSA_PEM)) {
            PemReader pemReader = new PemReader(new StringReader(credentials.getSecret()));
            PemObject pem = pemReader.readPemObject();
            secret = new String(pem.getContent());
        }
        return Algorithm.HMAC256(secret);
    }


    public static String createJwt(Algorithm algorithm, Long id) {
        try {
            return JWT.create()
                    .withClaim(ID_CLAIM, id)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            return exception.getMessage();
        }
    }
}
