package com.example.denky.ageis;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by denky on 2017-06-11.
 */

public class ImageDownload extends AsyncTask<String, Void, Void> {
    private String fileName;
    private final String SAVE_FOLDER = "/save_folder";
    public Handler handler;
    public ProcessContext processContext; //processContext 객체에 접근가능함
    public boolean share = false;
    private String savedPath ="";

    private void sendMsg(int msgType){
        Message msg = handler.obtainMessage();
        msg.what = msgType; //다운을 시작한다고 토스트를 띄움
        handler.sendMessage(msg);
    }

    @Override
    protected Void doInBackground(String... params) {
        //다운로드 경로를 지정
        String savePath = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;
        File dir = new File(savePath);
        //상위 디렉토리가 존재하지 않을 경우 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //파일 이름 :날짜_시간

        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        fileName = String.valueOf(sdf.format(day));
        //웹 서버 쪽 파일이 있는 경로
        String fileUrl = params[0];
        //다운로드 폴더에 동일한 파일명이 존재하는지 확인
        String localPath = savePath + "/" + fileName + ".jpg";
        Log.d("widae", "이미지 다운 접근! from : "+fileUrl);
        /*
        if (new File(localPath).exists() == true) { //이미 파일이 존재하면
            Log.d("widae", "이미 파일이 존재한다 게이야! ");
            sendMsg(4);
            processContext.setLastDownloadFile(localPath);
            return null; //백그라운드 작업 종료
        }
        */
        sendMsg(0); //이미지 다운 시작
        try {
            URL imgUrl = new URL(fileUrl);
            //서버와 접속하는 클라이언트 객체 생성
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            int len = conn.getContentLength();
            byte[] tmpByte = new byte[len];
            //입력 스트림을 구한다
            InputStream is = conn.getInputStream();
            File file = new File(localPath);
            //파일 저장 스트림 생성
            FileOutputStream fos = new FileOutputStream(file);
            int read;
            //입력 스트림을 파일로 저장
            for (;;) {
                read = is.read(tmpByte);
                if (read <= 0) {
                    break;
                }
                fos.write(tmpByte, 0, read); //file 생성
            }
            is.close();
            fos.close();
            Log.d("widae", "이미지 다운로드 끝났따 딱기분조타! from : "+localPath);
            //Log.d("widae", "파일 다운 경로 : "+localPath);
            processContext.setLastDownloadFile(localPath);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) { //다운 종료 함수
        //doInBackground 공부 : http://itmining.tistory.com/7
        /* show toast through main-activity hanlder */
        if(share){
            sendMsg(3); //공유하기 알림
        }else {
            sendMsg(1); //다운 완료 알림
        }
        /* show toast through main-activity hanlder */

        super.onPostExecute(result);
        /*
        //저장한 이미지 열기
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String targetDir = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;
        File file = new File(targetDir + "/" + fileName + ".jpg");
        //type 지정 (이미지)
        i.setDataAndType(Uri.fromFile(file), "image/*");
        getApplicationContext().startActivity(i);
        //이미지 스캔해서 갤러리 업데이트
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        */
    }

}