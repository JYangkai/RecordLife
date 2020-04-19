package com.yk.media.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderCodeUtils {
    private static final String MAIN_FUNC_NAME = "void main(){";

    public static String generateDefaultVertexCode(Context context) {
        return generateShaderCode(context, "DefaultVertex.glsl");
    }

    public static String generateDefaultFragCode(Context context) {
        return generateShaderCode(context, "DefaultFrag.glsl");
    }

    public static String insert(String shaderCode, String value) {
        if (TextUtils.isEmpty(shaderCode) || TextUtils.isEmpty(value)) {
            return null;
        }
        int index = shaderCode.indexOf(MAIN_FUNC_NAME);
        if (index == -1) {
            return null;
        }
        String beforeStr = shaderCode.substring(0, index);
        String afterStr = shaderCode.substring(index);
        StringBuffer sb = new StringBuffer();
        sb.append(beforeStr).append("\n")
                .append(value).append("\n")
                .append(afterStr);
        return sb.toString();
    }

    public static String replaceMainFunc(String shaderCode, String mainCode) {
        if (TextUtils.isEmpty(shaderCode) || TextUtils.isEmpty(mainCode)) {
            return null;
        }
        if (!MAIN_FUNC_NAME.contains(mainCode)) {
            return null;
        }
        int index = shaderCode.indexOf(MAIN_FUNC_NAME);
        if (index == -1) {
            return null;
        }
        String beforeStr = shaderCode.substring(0, index);
        StringBuffer sb = new StringBuffer();
        return sb.append(beforeStr).append("\n")
                .append(mainCode).toString();
    }

    public static String generateShaderCode(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
