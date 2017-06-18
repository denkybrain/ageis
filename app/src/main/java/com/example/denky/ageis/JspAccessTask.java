package com.example.denky.ageis;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.example.denky.ageis.ReferenceString.VIRUST_CHECK_ALGORITHM_URL;

/**
 * Created by denky on 2017-06-12.
 */

class JspAccessTask extends AsyncTask<String, Void, String> {
    String sendMsg, receiveMsg;
    String getUrl ;

    @Override
    protected String doInBackground(String... strings) {
        //source from : http://blog.naver.com/PostView.nhn?blogId=rain483&logNo=220814116681&redirect=Dlog&widgetTypeCall=true
        try {
            String str;
            String ASSEMBLED_URL = VIRUST_CHECK_ALGORITHM_URL +"?site="+ getUrl;
            URL url = new URL(ASSEMBLED_URL);
           // Log.d("widae", "do in Background url : "+ASSEMBLED_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");//데이터를 POST 방식으로 전송합니다.
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.flush();
            //jsp와 통신이 정상적으로 되었을 때 할 코드들입니다.
            if(conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                return receiveMsg;
            } else {
                return "0";
            }
        } catch (Exception e){
            Log.d("widae", "서버 접속 불가");
            return "0";
        }
        //jsp로부터 받은 리턴 값입니다.
    }
}