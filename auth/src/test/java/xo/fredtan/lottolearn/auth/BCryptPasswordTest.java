package xo.fredtan.lottolearn.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class BCryptPasswordTest {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public BCryptPasswordTest(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Test
    public void generatePassword() {
        String original = "fredtanxo";
        String encoded = bCryptPasswordEncoder.encode(original);
        System.out.println(encoded);
    }
}
