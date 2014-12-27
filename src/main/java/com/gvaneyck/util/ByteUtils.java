package com.gvaneyck.util;

public class ByteUtils {
    public static String bytesToHex(byte[] data) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            buffer.append(String.format("%02x ", data[i]));
        return buffer.toString();
    }

    public static String bytesToPrettyHex(byte[] data) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            buffer.append(String.format("%02x ", data[i]));

            if (((i + 1) % 16) == 0) {
                buffer.append("| ");

                for (int j = i - 15; j <= i; j++)
                    buffer.append(getPrintableChar((char)data[j]));

                buffer.append(" |\n");
            }
            else if (i + 1 == data.length) {
                for (int j = i + 1; j % 16 != 0; j++)
                    buffer.append("   ");

                buffer.append("| ");

                for (int j = i - (i % 16); j <= i; j++)
                    buffer.append(getPrintableChar((char)data[j]));

                for (int j = i + 1; j % 16 != 0; j++)
                    buffer.append(' ');

                buffer.append(" |\n");
            }
        }
        return buffer.toString();
    }

    public static char getPrintableChar(char c) {
        if (c == '\r' || c == '\n' || c == '\t')
            return ' ';
        else if (c >= ' ' && c <= '~')
            return c;
        else
            return '.';
    }
}
