package com.team10.trojancheckinout.utils;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.team10.trojancheckinout.R;
import com.team10.trojancheckinout.StudentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.annotation.Nullable;

public class QRCodeHelper {

    public static String process(int requestCode, int resultCode, @Nullable Intent data, StudentActivity studentActivity){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(studentActivity, "Result Not Found", Toast.LENGTH_LONG).show();
            }
            else {
                //if qr contains data
                return result.getContents();
//                //code to process QR code if it return a JSON
//                try {
//                    //converting the data to json
//                    JSONObject obj = new JSONObject(result.getContents());
//                    //setting values to textviews
//                    return "lel";
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    //if control comes here then the encoded format doesn't match
//                    //in this case display whatever data is available on the qrcode to a toast
//                    Toast.makeText(studentActivity, "lel", Toast.LENGTH_LONG).show();
//                }
            }
        }
        else{
            return "Invalid QR Code";
        }
        return "";
    }

    public static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

}
