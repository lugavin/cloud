package com.gavin.cloud.common.base.util;

import static java.nio.charset.StandardCharsets.US_ASCII;

public abstract class Base64 {

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final byte[] MAP = new byte[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '+', '/'
    };

    public static String encode(byte[] bytes) {
        int length = (bytes.length + 2) * 4 / 3;
        byte[] out = new byte[length];
        int index = 0, end = bytes.length - bytes.length % 3;
        for (int i = 0; i < end; i += 3) {
            out[index++] = MAP[(bytes[i] & 0xff) >> 2];
            out[index++] = MAP[((bytes[i] & 0x03) << 4) | ((bytes[i + 1] & 0xff) >> 4)];
            out[index++] = MAP[((bytes[i + 1] & 0x0f) << 2) | ((bytes[i + 2] & 0xff) >> 6)];
            out[index++] = MAP[(bytes[i + 2] & 0x3f)];
        }
        switch (bytes.length % 3) {
            case 1:
                out[index++] = MAP[(bytes[end] & 0xff) >> 2];
                out[index++] = MAP[(bytes[end] & 0x03) << 4];
                out[index++] = '=';
                out[index++] = '=';
                break;
            case 2:
                out[index++] = MAP[(bytes[end] & 0xff) >> 2];
                out[index++] = MAP[((bytes[end] & 0x03) << 4) | ((bytes[end + 1] & 0xff) >> 4)];
                out[index++] = MAP[((bytes[end + 1] & 0x0f) << 2)];
                out[index++] = '=';
                break;
        }
        return new String(out, 0, index, US_ASCII);
    }

    public static byte[] decode(byte[] bytes) {
        return decode(bytes, bytes.length);
    }

    public static byte[] decode(byte[] bytes, int len) {
        // approximate output length
        int length = len / 4 * 3;
        // return an empty array on empty or short input without padding
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        // temporary array
        byte[] out = new byte[length];
        // number of padding characters ('=')
        int pad = 0;
        byte chr;
        // compute the number of the padding characters
        // and adjust the length of the input
        for (; ; len--) {
            chr = bytes[len - 1];
            // skip the neutral characters
            if ((chr == '\n') || (chr == '\r') || (chr == ' ') || (chr == '\t')) {
                continue;
            }
            if (chr == '=') {
                pad++;
            } else {
                break;
            }
        }
        // index in the output array
        int outIndex = 0;
        // index in the input array
        int inIndex = 0;
        // holds the value of the input character
        int bits = 0;
        // holds the value of the input quantum
        int quantum = 0;
        for (int i = 0; i < len; i++) {
            chr = bytes[i];
            // skip the neutral characters
            if ((chr == '\n') || (chr == '\r') || (chr == ' ') || (chr == '\t')) {
                continue;
            }
            if ((chr >= 'A') && (chr <= 'Z')) {
                // char ASCII value
                // A 65 0
                // Z 90 25 (ASCII - 65)
                bits = chr - 65;
            } else if ((chr >= 'a') && (chr <= 'z')) {
                // char ASCII value
                // a 97 26
                // z 122 51 (ASCII - 71)
                bits = chr - 71;
            } else if ((chr >= '0') && (chr <= '9')) {
                // char ASCII value
                // 0 48 52
                // 9 57 61 (ASCII + 4)
                bits = chr + 4;
            } else if (chr == '+') {
                bits = 62;
            } else if (chr == '/') {
                bits = 63;
            } else {
                return null;
            }
            // append the value to the quantum
            quantum = (quantum << 6) | (byte) bits;
            if (inIndex % 4 == 3) {
                // 4 characters were read, so make the output:
                out[outIndex++] = (byte) (quantum >> 16);
                out[outIndex++] = (byte) (quantum >> 8);
                out[outIndex++] = (byte) quantum;
            }
            inIndex++;
        }
        if (pad > 0) {
            // adjust the quantum value according to the padding
            quantum = quantum << (6 * pad);
            // make output
            out[outIndex++] = (byte) (quantum >> 16);
            if (pad == 1) {
                out[outIndex++] = (byte) (quantum >> 8);
            }
        }
        // create the resulting array
        byte[] result = new byte[outIndex];
        System.arraycopy(out, 0, result, 0, outIndex);
        return result;
    }

}