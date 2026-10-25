package com.mehmetsolak.springmfa.infrastructure.totp;

import com.mehmetsolak.springmfa.application.ports.TotpPort;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Component;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Component
public class TotpAdapter implements TotpPort {

    private static final String ISSUER = "SpringMFA";

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(32);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final CodeVerifier verifier;

    public TotpAdapter() {
        DefaultCodeVerifier v = new DefaultCodeVerifier(
                new DefaultCodeGenerator(HashingAlgorithm.SHA1),
                new SystemTimeProvider());
        v.setTimePeriod(30);
        v.setAllowedTimePeriodDiscrepancy(1);
        this.verifier = v;
    }

    @Override
    public String generateSecret() {
        return secretGenerator.generate();
    }

    @Override
    public String qrDataUri(String secret, String accountName) {
        QrData data = new QrData.Builder()
                .label(accountName)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        try {
            return getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
        } catch (QrGenerationException e) {
            throw new IllegalStateException("QR code cannot created", e);
        }
    }

    @Override
    public boolean verify(String secret, String code) {
        return code != null && code.matches("\\d{6}") && verifier.isValidCode(secret, code);
    }
}
