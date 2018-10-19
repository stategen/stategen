/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * The Class RsaKeypair.
 *
 * @author Xia Zhengsheng
 */
public class RSAKeyPair {
    private KeyPair keyPair;

    public RSAKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    /**
     * 公钥.
     *
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    /**
     *  私钥.
     *
     * @return the private key
     */
    public PrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    /**
     * 得到公钥字符串
     */
    public String getPublicKeyString() {
        return Base64Util.encodeKey(getPublicKey());

    }

    /**
     * 得到私钥字符串
     */
    public String getPrivateKeyString() {
        return Base64Util.encodeKey(getPrivateKey());
    }
}
