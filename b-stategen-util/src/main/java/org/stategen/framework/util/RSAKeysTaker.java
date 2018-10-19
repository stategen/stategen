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

/**
 * The Class RSAKeysTaker.
 */
public class RSAKeysTaker {

    private String              rsaKeyName            = null;

    public RSAKeysTaker(String rsaKeyName) {
        AssertUtil.mustNotNull(rsaKeyName);
        this.rsaKeyName = rsaKeyName;
    }

    private String buildRedisKeyName(String keyProperty) {
        return new StringBuffer().append(keyProperty).append('_').append(rsaKeyName).toString();
    }

    public String getRsaPublicKey() {
        return genAndSaveRsaKeyPairToRedis(true);
    }

    private String getRsaPrivateKey() {
        return genAndSaveRsaKeyPairToRedis(false);
    }

    public String getCleanPassword(Boolean passwordEncoded, String password) {
        if (passwordEncoded != null && passwordEncoded) {
            String rsaPrivateKey = getRsaPrivateKey();
            password = RSAUtil.decrypt(rsaPrivateKey, password);
        }
        return password;
    }

    public String decrypt(String base64String) throws Exception {
        String privateKeyString = getRsaPrivateKey();
        return RSAUtil.decrypt(privateKeyString, base64String);
    }

    public String genAndSaveRsaKeyPairToRedis(boolean isPublic) {
        String rsaKey = isPublic ? RSAUtil.RSA_KEYPAIR_PUBLIC : RSAUtil.RSA_KEYPAIR_PRIVATE;
        String rsaValue = RedisTemplateUtil.get(buildRedisKeyName(rsaKey));

        if (StringUtil.isEmpty(rsaValue)) {
            RSAKeyPair rsaKeypair = RSAUtil.generateKeyPair();
            String publicKeyString = rsaKeypair.getPublicKeyString();
            String privateKeyString = rsaKeypair.getPrivateKeyString();
            RedisTemplateUtil.put(buildRedisKeyName(RSAUtil.RSA_KEYPAIR_PUBLIC), publicKeyString, RSAUtil.RSA_KEYPAIR_SECONDS);
            RedisTemplateUtil.put(buildRedisKeyName(RSAUtil.RSA_KEYPAIR_PRIVATE), privateKeyString, RSAUtil.RSA_KEYPAIR_SECONDS);
            rsaValue = isPublic ? publicKeyString : privateKeyString;
        }
        return rsaValue;
    }
    
    
    

}
