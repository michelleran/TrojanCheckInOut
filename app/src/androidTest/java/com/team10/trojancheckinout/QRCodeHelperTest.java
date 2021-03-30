package com.team10.trojancheckinout;

import com.team10.trojancheckinout.utils.QRCodeHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class QRCodeHelperTest {

    @Test
    public void checkUniqueQRCode(){
        // Get codes QR codes in byte array form for two different buildings
        byte[] firstCode = QRCodeHelper.generateQRCodeImage("4C55IS3bqEJzTLobc1AX", 250, 250);
        byte[] secondCode = QRCodeHelper.generateQRCodeImage("FGhcVP2KLHA6VgA1ceFr", 250, 250);
        // Check if they are equal
        assertFalse(Arrays.equals(firstCode, secondCode));
    }
}
