package com.vandenrobotics.saga.tools;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class ExternalStorageTools {

    private static final File BASE_DIR = Environment.getExternalStorageDirectory();
    private static final File DATA_DIR = Environment.getDataDirectory();
    private static final String TAG = ExternalStorageTools.class.getSimpleName();


    public static void writeDatabaseToES(){
        try {
            Log.d(TAG, "Starting saving database");


            if (isExternalStorageWritable()) {
                String currentDBPath = "//data//com.vandenrobotics.saga2018//databases//ScoutingData.db";
                String backupDBPath = "ScoutingData.db";
                File currentDB = new File(DATA_DIR, currentDBPath);
                File backupDB = new File(BASE_DIR, backupDBPath);
                Log.d(TAG, "Can Save database");


                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    Log.d(TAG, "Saving database");
                    src.close();
                    dst.close();
                }
            }
            else
            {
                Log.d(TAG, "Can not write database");

            }
        } catch (Exception e) {
        }
    }

    public static void writeImage(Bitmap image, int team_number) {
        if (isExternalStorageWritable()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(createFile("ScoutData/Images", team_number + ".png"));
                image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File readImage(int team_number) {
        return createFile("ScoutData/Images", team_number + ".png");
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public static File createDirectory(String dir){
        File f = new File(BASE_DIR.getAbsolutePath() + "/" + dir);
        if(!f.exists())
            f.mkdirs();
        return f;
    }

    public static File createFile(String dir, String filename){
        File path = createDirectory(dir);
        File f = new File(path, filename);
        if(!f.exists())
            try {
                f.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        return f;
    }

    public static void deleteFiles(String dir){
        deleteDirectory(new File(BASE_DIR.getAbsolutePath()+"/ScoutData/"+dir));
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    private static String getDeviceString(int device){
        return ((device<4) ? "Red"+device : "Blue"+(device-3));
    }
}
