package com.example.denky.ageis;

import java.io.File;

/**
 * Created by Windows10 on 2017-06-05.
 */

public class FileEncryptor {

    //this class is never instantiation.
    private FileEncryptor(){}

    //서버로부터 키값을 가져오고, 없다면 새로 만들어 서버에 넣는다.
    //키 값은 구글 계정을 기준으로, 1인 1키를 원칙으로 한다.
    private static int getKey(){
        return 0;
    }

    //파일 암호화
    //성공시 true, 실패시 false 리턴
    static boolean encrypt(File file){
        return false;
    }
}
