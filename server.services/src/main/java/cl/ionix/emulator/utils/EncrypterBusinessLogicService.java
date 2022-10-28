package cl.ionix.emulator.utils;


import javax.annotation.PostConstruct;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import org.bouncycastle.util.encoders.Base64;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.OutputEncryptor;


import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Iterator;

@Component
public class EncrypterBusinessLogicService {

	@PostConstruct
	void initialize() {
		Security.addProvider(new BouncyCastleProvider());
	}

	byte[] encrypt(byte[] data) throws Exception {

		String certificatePEM = UtilCertificado.rsaPublic;

		certificatePEM = certificatePEM.replace("-----BEGIN CERTIFICATE-----", "");
		certificatePEM = certificatePEM.replace("-----END CERTIFICATE-----", "");

		byte[] certificateData = Base64.decode(certificatePEM);

		CertificateFactory cf = CertificateFactory.getInstance("X509");
		X509Certificate certificate = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(certificateData));

		byte[] encryptedData = null;

		if (null != data && null != certificate) {

			CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
			JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(certificate);
			cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
			CMSTypedData msg = new CMSProcessableByteArray(data);
			OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_GCM).setProvider("BC")
					.build();
			CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
			encryptedData = cmsEnvelopedData.getEncoded();
		}

		return encryptedData;
	}

	byte[] decrypt(byte[] data) throws Exception {

		String certificatePEM = UtilCertificado.rsaPublic;

		certificatePEM = certificatePEM.replace("-----BEGIN CERTIFICATE-----", "");
		certificatePEM = certificatePEM.replace("-----END CERTIFICATE-----", "");

		byte[] certificateData = Base64.decode(certificatePEM);

		CertificateFactory cf = CertificateFactory.getInstance("X509");
		X509Certificate certificate = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(certificateData));

		String privateKeyPem = UtilCertificado.rsaPrivate;

		privateKeyPem = privateKeyPem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
		privateKeyPem = privateKeyPem.replace("-----END RSA PRIVATE KEY-----", "");

		byte[] pkcs8EncodedBytes = Base64.decode(privateKeyPem);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = kf.generatePrivate(spec);

		byte[] decrytedText = null;

		CMSEnvelopedData envelopedData = new CMSEnvelopedData(data);
		RecipientInformationStore recipients = envelopedData.getRecipientInfos();
		Collection<RecipientInformation> c = recipients.getRecipients(new JceKeyTransRecipientId(certificate));
		Iterator<RecipientInformation> it = c.iterator();
		if (it.hasNext()) {
			RecipientInformation recipient = (RecipientInformation) it.next();
			decrytedText = recipient.getContent(new JceKeyTransEnvelopedRecipient(privateKey).setProvider("BC"));
		}

		return decrytedText;
	}

}
