package com.hprof.bitmap;

import com.squareup.haha.perflib.ArrayInstance;
import com.squareup.haha.perflib.ClassInstance;
import com.squareup.haha.perflib.Instance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HahaHelper {

    public static <T> T fieldValue(List<ClassInstance.FieldValue> fieldValues, String fieldName) {
        for (ClassInstance.FieldValue fieldValue : fieldValues) {

            if (fieldValue.getField().getName().equals(fieldName)) {
                return (T) fieldValue.getValue();
            }
        }

        throw new IllegalArgumentException("Filed " + fieldName + " does not exists");
    }

    public static String MD5(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes, 0, bytes.length);
            return byteArrayToHex(md5.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static void getStackInfo(Instance instance) {
        while (instance.getNextInstanceToGcRoot() != null) {
            System.out.println(instance.getNextInstanceToGcRoot());
            instance = instance.getNextInstanceToGcRoot();
        }
    }

    public static String getMd5(Instance bitmapInstance) {
        ArrayInstance instance = HahaHelper.fieldValue(((ClassInstance) bitmapInstance).getValues(), "mBuffer");
        byte[] bytes = instance.asRawByteArray(0, instance.getLength());
        return HahaHelper.MD5(bytes);
    }
}

