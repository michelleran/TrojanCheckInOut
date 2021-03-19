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
    public static byte[] generateQRCodeImage(String buildingID, int width, int height) {
        return QRCode.from(buildingID).to(ImageType.JPG).withSize(width, height).stream().toByteArray();
    }
}
