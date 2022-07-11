package cifras;

import interfaces.CryptoSystemInterface;

public class Padrao implements CryptoSystemInterface {
    @Override
    public String encrypt(String x){
        return x;
    }

    @Override
    public String decrypt(String x){
        return x;
    }
}
