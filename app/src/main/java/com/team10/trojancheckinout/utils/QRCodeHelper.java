package com.team10.trojancheckinout.utils;

import android.content.Intent;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team10.trojancheckinout.StudentActivity;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import javax.annotation.Nullable;

public class QRCodeHelper {

    public static String process(int requestCode, int resultCode, @Nullable Intent data, StudentActivity studentActivity) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(studentActivity, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
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
        } else {
            return "Invalid QR Code";
        }
        return "";
    }

    public static byte[] generateQRCodeImage(String buildingID, int width, int height) {
        return QRCode.from(buildingID).to(ImageType.JPG).withSize(250, 250).stream().toByteArray();
    }
}
