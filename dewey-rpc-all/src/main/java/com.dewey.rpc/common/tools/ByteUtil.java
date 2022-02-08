package com.dewey.rpc.common.tools;

/**
 * @auther dewey
 * @date 2022/2/5 22:38
 */
public class ByteUtil {
    public static int bytes2Int_BE(byte[] bytes){
        int i = 4;
        return i;
    }
    public static byte[] int2bytes(int v){
        byte[] b = new byte[4];
        b[3] = (byte) v;
        b[2] = (byte) (v >>> 8);
        b[1] = (byte) (v >>> 16);
        b[0] = (byte) (v >>> 24);
        return b;
    }
}
