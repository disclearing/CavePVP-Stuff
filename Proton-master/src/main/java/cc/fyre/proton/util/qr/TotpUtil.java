package cc.fyre.proton.util.qr;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * @author xanderume@gmail (JavaProject)
 */
@UtilityClass
public class TotpUtil {

    public static final int DEFAULT_TIME_STEP_SECONDS = 30;

    private static int NUM_DIGITS_OUTPUT = 6;
    private static final String blockOfZeros;

    public static String generateBase32Secret() {
        return generateBase32Secret(16);
    }

    public static String generateBase32Secret(int length) {

        final Random random = new SecureRandom();

        final StringBuilder stringBuilder = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {

            int val = random.nextInt(32);

            if (val < 26) {
                stringBuilder.append((char)(65 + val));
            } else {
                stringBuilder.append((char)(50 + (val - 26)));
            }

        }

        return stringBuilder.toString();
    }

    public static boolean validateCurrentNumber(String base32Secret, int authNumber, int windowMillis) throws GeneralSecurityException {
        return validateCurrentNumber(base32Secret, authNumber, windowMillis, System.currentTimeMillis(), 30);
    }

    public static boolean validateCurrentNumber(String base32Secret, int authNumber, int windowMillis, long timeMillis, int timeStepSeconds) throws GeneralSecurityException {
        long from = timeMillis;

        long to = timeMillis;

        if (windowMillis > 0) {
            from = timeMillis - (long)windowMillis;
            to = timeMillis + (long)windowMillis;
        }

        long timeStepMillis = (long)(timeStepSeconds * 1000);

        for(long millis = from; millis <= to; millis += timeStepMillis) {

            final long compare = generateNumber(base32Secret, millis, timeStepSeconds);

            if (compare == (long)authNumber) {
                return true;
            }

        }

        return false;
    }

    public static String generateCurrentNumberString(String base32Secret) throws GeneralSecurityException {
        return generateNumberString(base32Secret, System.currentTimeMillis(), 30);
    }

    public static String generateNumberString(String base32Secret, long timeMillis, int timeStepSeconds) throws GeneralSecurityException {

        final long number = generateNumber(base32Secret, timeMillis, timeStepSeconds);

        return zeroPrepend(number, NUM_DIGITS_OUTPUT);
    }

    public static long generateCurrentNumber(String base32Secret) throws GeneralSecurityException {
        return generateNumber(base32Secret, System.currentTimeMillis(), 30);
    }

    public static long generateNumber(String base32Secret, long timeMillis, int timeStepSeconds) throws GeneralSecurityException {

        final byte[] key = decodeBase32(base32Secret);
        final byte[] data = new byte[8];

        long value = timeMillis / 1000L / (long)timeStepSeconds;

        for(int i = 7; value > 0L; --i) {
            data[i] = (byte)((int)(value & 255L));
            value >>= 8;
        }

        final SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        final Mac mac = Mac.getInstance("HmacSHA1");

        mac.init(signKey);

        final byte[] hash = mac.doFinal(data);
        final int offset = hash[hash.length - 1] & 15;

        long truncatedHash = 0L;

        for(int i = offset; i < offset + 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (long)(hash[i] & 255);
        }

        truncatedHash &= 2147483647L;
        truncatedHash %= 1000000L;

        return truncatedHash;
    }

    public static String qrImageUrl(String keyId, String secret) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=200x200&chld=M|0&cht=qr&chl=");
        addOtpAuthPart(keyId, secret, sb);
        return sb.toString();
    }

    public static String generateOtpAuthUrl(String keyId, String secret) {
        StringBuilder sb = new StringBuilder(64);
        addOtpAuthPart(keyId, secret, sb);
        return sb.toString();
    }

    private static void addOtpAuthPart(String keyId, String secret, StringBuilder sb) {
        sb.append("otpauth://totp/").append(keyId).append("%3Fsecret%3D").append(secret);
    }

    static String zeroPrepend(long num, int digits) {
        final String numStr = Long.toString(num);

        if (numStr.length() >= digits) {
            return numStr;
        } else {
            StringBuilder sb = new StringBuilder(digits);
            int zeroCount = digits - numStr.length();
            sb.append(blockOfZeros, 0, zeroCount);
            sb.append(numStr);
            return sb.toString();
        }
    }

    static byte[] decodeBase32(String str) {
        int numBytes = (str.length() * 5 + 7) / 8;
        byte[] result = new byte[numBytes];
        int resultIndex = 0;
        int which = 0;
        int working = 0;

        for(int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            int val;
            if (ch >= 'a' && ch <= 'z') {
                val = ch - 97;
            } else if (ch >= 'A' && ch <= 'Z') {
                val = ch - 65;
            } else {
                if (ch < '2' || ch > '7') {
                    if (ch != '=') {
                        throw new IllegalArgumentException("Invalid base-32 character: " + ch);
                    }

                    which = 0;
                    break;
                }

                val = 26 + (ch - 50);
            }

            switch(which) {
                case 0:
                    working = (val & 31) << 3;
                    which = 1;
                    break;
                case 1:
                    working |= (val & 28) >> 2;
                    result[resultIndex++] = (byte)working;
                    working = (val & 3) << 6;
                    which = 2;
                    break;
                case 2:
                    working |= (val & 31) << 1;
                    which = 3;
                    break;
                case 3:
                    working |= (val & 16) >> 4;
                    result[resultIndex++] = (byte)working;
                    working = (val & 15) << 4;
                    which = 4;
                    break;
                case 4:
                    working |= (val & 30) >> 1;
                    result[resultIndex++] = (byte)working;
                    working = (val & 1) << 7;
                    which = 5;
                    break;
                case 5:
                    working |= (val & 31) << 2;
                    which = 6;
                    break;
                case 6:
                    working |= (val & 24) >> 3;
                    result[resultIndex++] = (byte)working;
                    working = (val & 7) << 5;
                    which = 7;
                    break;
                case 7:
                    working |= val & 31;
                    result[resultIndex++] = (byte)working;
                    which = 0;
            }
        }

        if (which != 0) {
            result[resultIndex++] = (byte)working;
        }

        if (resultIndex != result.length) {
            result = Arrays.copyOf(result, resultIndex);
        }

        return result;
    }

    static {

        final char[] chars = new char[NUM_DIGITS_OUTPUT];

        for(int i = 0; i < chars.length; ++i) {
            chars[i] = '0';
        }

        blockOfZeros = new String(chars);
    }


}
