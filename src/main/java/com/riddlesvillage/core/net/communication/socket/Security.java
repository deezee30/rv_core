/*
 * rv_core
 * 
 * Created on 12 July 2017 at 3:41 PM.
 */

package com.riddlesvillage.core.net.communication.socket;

import com.riddlesvillage.core.security.AES;
import com.riddlesvillage.core.security.RSA;
import org.apache.commons.lang3.Validate;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PublicKey;

public final class Security {

    // TODO
    public static Level DEFAULT_LEVEL = Level.NONE;

    private final int level; // 0 = no security; 1 = AES encryption (b64 key sent); 2 = AES encryption, rsa handshake (rsa used for sending AES key)
    private final Target target = new Target();
    private final Self self = new Self();

    public Security(final Level level) {
        this.level = Validate.notNull(level).level;
    }

    public Security(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Target getTarget() {
        return target;
    }

    public Self getSelf() {
        return self;
    }

    public class Target {

        private PublicKey rsa;
        private SecretKey aes;

        public PublicKey getRsa() {
            return rsa;
        }

        public void setRsa(PublicKey rsa) {
            this.rsa = rsa;
        }

        public SecretKey getAes() {
            return aes;
        }

        public void setAes(SecretKey aes) {
            this.aes = aes;
        }
    }

    public class Self {

        private KeyPair rsa;
        private SecretKey aes;

        public KeyPair getRsa() {
            return rsa;
        }

        public void setRsa(KeyPair rsa) {
            this.rsa = rsa;
        }

        public SecretKey getAes() {
            return aes;
        }

        public void setAes(SecretKey aes) {
            this.aes = aes;
        }
    }

    public void reset() {
        target.rsa = null;
        target.aes = null;
        self.rsa = RSA.generateKeys();
        self.aes = AES.generateKey();
    }

    public enum Level {

        NONE(0),
        AES(1),
        AES_RSA(2);

        private final int level;

        Level(final int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}