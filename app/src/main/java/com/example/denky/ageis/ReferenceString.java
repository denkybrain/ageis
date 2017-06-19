package com.example.denky.ageis;

import java.util.HashMap;

/**
 * Created by denky on 2017-06-08.
 */

public abstract class ReferenceString {
    //unchangable values : viewable in main Activity
    public static final String URL_NORMAL_MODE_HINT ="Search or Input URI";
    public static final String URL_SECURITY_MODE_HINT = "Security Mode";

    //unchangable values : unviewable
    public static final String GOOGLE_SEARCH_URL = "https://www.google.co.kr/search?q=";
    public static final String MAIN_URL = "http://denkybrain.cafe24.com/ageis/main.php";
    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT; //device height
    static final int STORAGE_READ_PERMISSON=100;
    static final int STORAGE_WRITE_PERMISSON=101;
    public static HashMap<String , String> URL_HASHMAP = new HashMap<String , String>();

    // protocols
    private String country[]
            =   {".com",".co.kr", "go.kr"};
    private String fileformat[]
            =   {".php",".html", ".jsp"};
    // **virus check jsp site
    public static final String VIRUST_CHECK_ALGORITHM_URL = "http://150.95.155.101:8080/scan.jsp";

    //functions that words only once
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
    public static String NORMAL_MODE_LAST_VIEW = MAIN_URL;
    public static String SECURITY_MODE_LAST_VIEW = MAIN_URL;
    public static boolean SECURITY_MODE_STATE = false;
}
