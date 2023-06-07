package com.saqibdb.YahrtzeitsOfGedolim.helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileReader {
    private static final String TAG = FileReader.class.getSimpleName();
    private static final String FILE_NAME = "Dates.json";

    public String readContentFromFile(Context context) {
        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            reader.close();
            inputStream.close();
            return content.toString();
        } catch (IOException e) {
            String message = e.getMessage();
            Log.d(TAG, "[ERROR]: " + message);
        }

        return null;
    }
}
