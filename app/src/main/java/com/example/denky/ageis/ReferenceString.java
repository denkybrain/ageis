package com.example.denky.ageis;

import java.util.HashMap;

/**
 * Created by denky on 2017-06-08.
 */

public abstract class ReferenceString {
    //This class is for reference within all calsses
    //unchangable values : viewable in main Activity
    public static final String URL_NORMAL_MODE_HINT ="Search or Input URL";
    public static final String URL_SECURITY_MODE_HINT = "Security Mode";

    //unchangable values : unviewable

    public static final String GOOGLE_SEARCH_URL = "https://www.google.co.kr/search?q=";
    public static final String MAIN_URL = "http://denkybrain.cafe24.com/ageis/main.php";
    public static final String HOME_PAGE = "http://denkybrain.cafe24.com/ageis/homepage/index.html"; //private
    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT; //device height, for screenshot function
    public static final int STORAGE_READ_PERMISSON=100;
    public static final int STORAGE_WRITE_PERMISSON=101;
    public static final int TIME_OF_ANIMATION = 500;
    public static final String SAVE_FOLDER = "/Ageis_screenshot";
    public static HashMap<String , String> URL_HASHMAP = new HashMap<String , String>();
    //Hashmap saving the directive url


    // **virus check jsp site
    public static final String VIRUST_CHECK_ALGORITHM_URL = "http://150.95.155.101:8080/scan.jsp";

    // protocols
    private String country[]
            =   {".com",".co.kr", "go.kr"};
    private String fileformat[]
            =   {".php",".html", ".jsp"};

    public static void initializeHashMap(){
        URL_HASHMAP.put("", MAIN_URL);
        URL_HASHMAP.put("네이버", "http://www.naver.com");
        URL_HASHMAP.put("다음", "http://www.daum.net");
        URL_HASHMAP.put("구글", "http://www.google.co.kr");
        URL_HASHMAP.put("일베", "http://www.ilbe.com");
        URL_HASHMAP.put("건국대학교", "http://www.konkuk.ac.kr/");
        URL_HASHMAP.put("컴응", "http://www.cafe.daum.net/cris.lecture/");
    }
    //changable values
    public static boolean ANIMATION_DONE = true;
}
