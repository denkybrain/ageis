package com.example.denky.ageis;

import java.util.HashMap;

/**
 * Created by denky on 2017-06-08.
 */

public abstract class ReferenceString {
    //unchangable values : viewable in main Activity

    public static final String URL_NORMAL_MODE_HINT ="Search or Input URI";
    public static final String URL_SECURITY_MODE_HINT = "Security Mode";
    // : unviewable
    public static final String MAIN_URL = "http://denkybrain.cafe24.com/ageis/main.php";
    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;
    public static HashMap<String , String> URL_HASHMAP = new HashMap<String , String>();


    private String country[]
            =   {".com",".co.kr", "go.kr"};
    private String fileformat[]
            =   {".php",".html", ".jsp"};

    // **virus check jsp site
    public static final String VIRUST_CHECK_ALGORITHM_URL = "http://175.198.248.51:8080/scan/scan.jsp";

    public static void initializeHashMap(){
        URL_HASHMAP.put("", MAIN_URL);
        URL_HASHMAP.put("네이버", "http://naver.com");
        URL_HASHMAP.put("다음", "http://daum.net");
        URL_HASHMAP.put("구글", "http://google.co.kr");
        URL_HASHMAP.put("일베", "http://ilbe.com");
        URL_HASHMAP.put("건국대학교", "http://www.konkuk.ac.kr/");
        URL_HASHMAP.put("컴응", "http://cafe.daum.net/cris.lecture/");
    }
     //changable values

    public static boolean SECURITY_MODE_STATE = false;
}
