package cifras;

import interfaces.CryptoSystemInterface;

public class RC4 implements CryptoSystemInterface{
    private char[] S, T;
    private String key;

    public RC4(String key){
        this.key = key;
        S = new char[256];
        T = new char[256];
        int keyLength = this.key.length();
        for (char i = 0; i < 256; i++)
        {
            S[i] = i;
            T[i] = key.charAt(i % keyLength);
        }
        int j = 0;
        for (char i = 0; i < 256; i++)
        {
            j = (j + S[i] + T[i]) % 256;
            S[j] = swap(S[i], S[j] = S[i]);
        }
    }

    public char swap(char a, int dummy){
        return a;
    }

    @Override
    public String encrypt(String M){
        StringBuilder sb = new StringBuilder();
        // prga
        int i, j, t;
        i = j = 0;
        for (int k = 0; k < M.length(); k++)
        {
            i = (i + 1) % 256;
            j = (j + S[i]) % 256;
            S[j] = swap(S[i], S[j] = S[i]);
            t = (S[i] + S[j]) % 256;
            sb.append((char)(S[t] ^ M.charAt(k)));
        }
        return sb.toString();
    }

    @Override
    public String decrypt(String M){
        return encrypt(M);
    }
}