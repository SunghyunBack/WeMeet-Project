package com.bsh.projectwemeet.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {

    public static String hashSha512(String input){
        return CryptoUtil.hashSha512(input, null);
    }

//     1. SHA256 혹은 SHA512는 MessageDigest클래스를 사용하여 생성할수 있다.
//    2. md.update(input.getBytes(StandardCharsets.UTF_8));
//    2에서 input은 해시할 데이터를 뜻하고 getBytes(StandardCharsets.UTF_8)은 해당 문자열을 UTF-8인코딩으로 바이트 배열로 변환하는 메서드이다.
//    update메서드를 사용하여 MessageDigest객체에 추가한다.
//    3. md.digest()를 통해 입력된 데이터의 해시 값을 호출할수 있으며 이를 BigInteger의 양의 정수로 변환한다.
//    4. String.format("%0128x"  -> 이 부분을 통해 양의 정수 128자리 16진수 문자열로 변환한다.
//    5. 예외가 발생하면 (MessageDigest.getInstance에서 지정된 알고리즘을 찾을수 없는 경우) 'fallback'을 반환한다.



    public static String hashSha512(String input, String fallback){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, md.digest()));
        }catch(NoSuchAlgorithmException ignored){
            return fallback;
        }
    }



    private CryptoUtil(){
        super();
    }
}
