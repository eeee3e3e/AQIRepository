package org.apache.cordova;

/**
* A phonegap plugin that converts a Base64 String to a PNG file.
*
* @author mcaesar
* @lincese MIT.
*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.os.Environment;
import java.io.*;
import org.json.JSONException;
import org.json.JSONObject;
import util.Base64;

public class Base64ToPNG extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {

        if (!action.equals("saveImage")) {
            return new PluginResult(PluginResult.Status.INVALID_ACTION);
        }

        try {

            String b64String = "";
            if (b64String.startsWith("data:image")) {
                b64String = args.getString(0).substring(21);
            } else {
                b64String = args.getString(0);
            }
            JSONObject params = args.getJSONObject(1);
            //Optional parameter
            String filename1 = params.has("filename")
                    ? params.getString("filename")
                    : "b64Image_" + System.currentTimeMillis() + ".jpg";
            
            String filename=filename1.split("&")[1].toString(); //getFileName
            String subFolder=filename1.split("&")[0].toString(); //getFolderName
                    
            String folder = params.has("folder")
                    ? params.getString("folder")
                    : Environment.getExternalStorageDirectory() + "/AQIData/"+subFolder;

            Boolean overwrite = params.has("overwrite") 
                    ? params.getBoolean("overwrite") 
                    : false;

            return this.saveImage(b64String, filename, folder, overwrite, callbackId);

        } catch (JSONException e) {

            e.printStackTrace();
            return new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage());

        } catch (InterruptedException e) {
            e.printStackTrace();
            return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        }

    }

    private PluginResult saveImage(String b64String, String fileName, String dirName, Boolean overwrite, String callbackId) throws InterruptedException, JSONException {

        try {
        	//b64String="QUJDRA==";
            //Directory and File
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dirName, fileName);

            //Avoid overwriting a file
            if (!overwrite && file.exists()) {
                return new PluginResult(PluginResult.Status.OK, "File already exists!");
            }

            //Decode Base64 back to Binary format
            byte[] decodedBytes = Base64.decode(b64String.getBytes());

            //Save Binary file to phone
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(decodedBytes);
            fOut.close();


            return new PluginResult(PluginResult.Status.OK, "Saved successfully!");

        } catch (FileNotFoundException e) {
            return new PluginResult(PluginResult.Status.ERROR, "File not Found!");
        } catch (IOException e) {
            return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        }

    }
}