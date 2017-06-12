package com.example.denky.ageis;

import java.io.File;

/**
 * Created by Windows10 on 2017-06-05.
 */
import java.io.*;
import java.util.Random;

public class FileEncryptor {

    //this class is never instantiate
    private FileEncryptor(){}


    //Buffer for stream
    //Default buffer size is 2^10=1024byte
    private static final int bufferSize=(int)Math.pow(2, 10);
    private static byte buffer[]=new byte[bufferSize];

    public static void setBufferSize(int bufferSize) throws BufferSizeSettingException{
        if(bufferSize<1 || bufferSize>Integer.BYTES){
            throw new BufferSizeSettingException();
        }
        buffer=new byte[bufferSize];
    }

    //File Encrypt
    //Encrypting Success is true, fail is false
    //key value generally have System.currentTimemillis()'s return value
    public static boolean encrypt(File file, long key){
        //System.out.println("Original File length: "+file.length());
        if(file.exists()==false || file.isDirectory()==true){
            return false;
        }else{
            InputStream inputStream=null;
            OutputStream fileMaker=null;
            DataOutputStream temp=null;
            try {
                inputStream=new FileInputStream(file);

                //make stream of encrypted file
                String fileName=file.getName();
                String filePath=null;
                filePath=file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf(fileName));
                String resultFileName=fileName+"_encrypt";

                File encryptFile=new File(filePath+resultFileName);

                if(encryptFile.exists()==true){
                    //Already file having same file name is existing
                    inputStream.close();
                    return false;
                }

                fileMaker=new FileOutputStream(encryptFile);

                //read data from file
                //and write data after encrypting
                long fileSize=file.length();
                long encryptSize=fileSize;

                Random ranVal=new Random(key);
                byte encryptValue=(byte) ranVal.nextInt(Byte.MAX_VALUE);
                while(true){
                    if(encryptValue==0){
                        ranVal=new Random(key+1);
                        encryptValue=(byte) ranVal.nextInt(Byte.MAX_VALUE);
                    }else{
                        break;
                    }
                }


                int readSize=0;
                while(true){
                    readSize=inputStream.read(buffer, 0, buffer.length);

                    if(readSize<=0){
                        break;
                    }
                    if(encryptSize>0){
                        boolean tempp=false;
                        for(int i=0; i<readSize; i++){
                            if(tempp){
                                buffer[i]=(byte) (buffer[i]+encryptValue);
                                tempp=false;
                            }else{
                                buffer[i]=(byte) (buffer[i]-encryptValue);
                                tempp=true;
                            }
                        }
                        encryptSize-=readSize;

                        fileMaker.write(buffer, 0, readSize);
                    }
                }
                //End Encrypting

                //Add Garbage value and Save Key value
                temp=new DataOutputStream(fileMaker);
                Random ran=new Random();
                //The size of garbage value is (2^10)*100*8
                for(int i=0; i<(int)(Math.pow(2, 10))*100; i++){
                    temp.writeLong(ran.nextLong());
                }
                //Save Key Value
                temp.writeLong(key);
                temp.close();

            } catch (Exception e) {
                //Error Log print area
                return false;
            }finally{
                try{
                    if(inputStream!=null){
                        inputStream.close();
                    }
                    if(fileMaker!=null){
                        fileMaker.close();
                    }
                    if(temp!=null){
                        temp.close();
                    }

                }catch(Exception e){
                    //Stream can't be closed.
                }
            }

            //System.out.println("Success Encrypting!!");
            return true;
        }
    }

    //File Decrypt
    //Encrypting Success is true, fail is false
    //key value generally have System.currentTimemillis()'s return value
    public static boolean decrypt(File file, long key){
        if(file.exists()==false || file.isDirectory()==true){
            //Not exist file or not file but directory.
            //System.out.println("");
            return false;
        }

        RandomAccessFile fileReader=null;
        OutputStream out=null;
        try {
            fileReader=new RandomAccessFile(file, "rw");
            fileReader.seek(file.length()-Long.BYTES);

            long readKey=fileReader.readLong();

            if(readKey!=key){
                //Error Log print area
                //This file is not encrypted or Key value is invalid.
                fileReader.close();
                return false;
            }

            Random ranVal=new Random(key);
            byte encryptValue=(byte) ranVal.nextInt(Byte.MAX_VALUE);
            while(true){
                if(encryptValue==0){
                    ranVal=new Random(key+1);
                    encryptValue=(byte) ranVal.nextInt(Byte.MAX_VALUE);
                }else{
                    break;
                }
            }

            fileReader.seek(0);

            String fileName=file.getName();
            fileName=fileName.substring(0, fileName.indexOf("_encrypt"));
            String filePath=null;
            filePath=file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf(fileName));
            String resultFileName="decrypt_"+fileName;

            int garbageValueSize=(int) ((Math.pow(2, 10))*100*Long.BYTES);

            long resultFileLength=file.length()-garbageValueSize-Long.BYTES;

            fileReader.setLength(resultFileLength);

            //System.out.println("decrypt File length: "+resultFileLength);

            File resultFile=new File(filePath+resultFileName);
            if(resultFile.exists()==true){
                //Already file having same file name is existing
                fileReader.close();
                return false;
            }
            //Start Decrypt.
            out=new FileOutputStream(resultFile);

            long encryptSize=resultFileLength;

            fileReader.seek(0);
            int readSize=0;
            while(true){
                readSize=fileReader.read(buffer, 0, buffer.length);
                if(readSize<=0){
                    break;
                }

                if(encryptSize>0){
                    boolean temp=false;
                    for(int i=0; i<readSize; i++){
                        if(temp){
                            buffer[i]=(byte) (buffer[i]-encryptValue);
                            temp=false;
                        }else{
                            buffer[i]=(byte) (buffer[i]+encryptValue);
                            temp=true;
                        }
                    }
                    encryptSize-=readSize;
                }
                out.write(buffer, 0, readSize);
            }

            fileReader.close();
            out.close();

        } catch (Exception e) {
            //Error Log print area
            //System.out.println("Fail to decrypt");
            return false;
        }finally{
            try{
                if(fileReader!=null){
                    fileReader.close();
                }
                if(out!=null){
                    out.close();
                }
            }catch(Exception e){
                //Stream can't be closed
            }
        }

        //After Decrypting, Delete encrypted file.
        file.delete();

        //System.out.println("Success Decrypt");
        return true;
    }

}
