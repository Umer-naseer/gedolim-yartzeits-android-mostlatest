package com.saqibdb.YahrtzeitsOfGedolim.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileWriter {
    private static final String TAG = FileWriter.class.getSimpleName();
    private static final String FILE_NAME = "Dates.json";

    public void writeContentToFile(Context context, String content) {
        try {
            OutputStream outputStream = new FileOutputStream(context.getFilesDir() + "/" + FILE_NAME);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            String message = e.getMessage();
            Log.d(TAG, "[ERROR]: " + message);
        }
    }

}
