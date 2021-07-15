package cn.philip.common.utils;

import java.io.*;

/**
 * Base64加密工具类.
 * 
 * @author chainsmart
 * @date 2016-09-07
 */
public class Base64Util {
  
  protected static final byte[] BASE_64_CHARS = { 65, 66, 67, 68, 69, 70, 71, 72,
      73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
      97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
      112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51,
      52, 53, 54, 55, 56, 57, 43, 47 };

  protected static final byte[] REVERSE_BASE_64_CHARS = new byte['ÿ'];

  static {
    for (int i = 0; i < REVERSE_BASE_64_CHARS.length; i++) {
      REVERSE_BASE_64_CHARS[i] = -1;
    }

    for (byte i = 0; i < BASE_64_CHARS.length; i = (byte) (i + 1)) {
      REVERSE_BASE_64_CHARS[BASE_64_CHARS[i]] = i;
    }
  }

  public static String encode(String string) {
    return new String(encode(string.getBytes()));
  }

  public static String encode(String string, String enc)
      throws UnsupportedEncodingException {
    return new String(encode(string.getBytes(enc)), enc);
  }

  public static byte[] encode(byte[] bytes) {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);

    int length = bytes.length;
    int mod;
    if ((mod = length % 3) != 0) {
      length += 3 - mod;
    }
    length = length * 4 / 3;
    ByteArrayOutputStream out = new ByteArrayOutputStream(length);
    try {
      encode(in, out, false);
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    return out.toByteArray();
  }

  public static void encode(InputStream in, OutputStream out, boolean lineBreaks)
      throws IOException {
    int[] inBuffer = new int[3];
    int lineCount = 0;

    boolean done = false;
    while ((!done) && ((inBuffer[0] = in.read()) != -1)) {
      inBuffer[1] = in.read();
      inBuffer[2] = in.read();

      out.write(BASE_64_CHARS[(inBuffer[0] >> 2)]);
      if (inBuffer[1] != -1) {
        out.write(BASE_64_CHARS[(inBuffer[0] << 4 & 0x30 | inBuffer[1] >> 4)]);
        if (inBuffer[2] != -1) {
          out.write(BASE_64_CHARS[(inBuffer[1] << 2 & 0x3C | inBuffer[2] >> 6)]);

          out.write(BASE_64_CHARS[(inBuffer[2] & 0x3F)]);
        } else {
          out.write(BASE_64_CHARS[(inBuffer[1] << 2 & 0x3C)]);

          out.write(61);
          done = true;
        }
      } else {
        out.write(BASE_64_CHARS[(inBuffer[0] << 4 & 0x30)]);

        out.write(61);
        out.write(61);
        done = true;
      }
      lineCount += 4;
      if ((lineBreaks) && (lineCount >= 76)) {
        out.write(10);
        lineCount = 0;
      }
    }
  }

  public static String decode(String string) {
    return new String(decode(string.getBytes()));
  }

  public static String decode(String string, String enc)
      throws UnsupportedEncodingException {
    return new String(decode(string.getBytes(enc)), enc);
  }

  public static byte[] decode(byte[] bytes) {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);

    int length = bytes.length;
    int mod;
    if ((mod = length % 4) != 0) {
      length += 4 - mod;
    }
    length = length * 3 / 4;
    ByteArrayOutputStream out = new ByteArrayOutputStream(length);
    try {
      decode(in, out, false);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return out.toByteArray();
  }
  
  public static void decode(InputStream in, OutputStream out,
                            boolean throwExceptions) throws IOException {
    int[] inBuffer = new int[4];

    boolean done = false;
    while ((!done) && ((inBuffer[0] = readBase64(in, throwExceptions)) != -1)
        && ((inBuffer[1] = readBase64(in, throwExceptions)) != -1)) {
      inBuffer[2] = readBase64(in, throwExceptions);
      inBuffer[3] = readBase64(in, throwExceptions);

      out.write(inBuffer[0] << 2 | inBuffer[1] >> 4);
      if (inBuffer[2] != -1) {
        out.write(inBuffer[1] << 4 | inBuffer[2] >> 2);
        if (inBuffer[3] != -1) {
          out.write(inBuffer[2] << 6 | inBuffer[3]);
        } else {
          done = true;
        }
      } else {
        done = true;
      }
    }
  }

  private static final int readBase64(InputStream in, boolean throwExceptions)
      throws IOException {
    int read;
    do {
      read = in.read();
      if (read == -1) {
        return -1;
      }
      if ((throwExceptions) && (REVERSE_BASE_64_CHARS[(byte) read] == -1)
          && (read != 32) && (read != 10) && (read != 13) && (read != 9)
          && (read != 12) && (read != 61)) {
        throw new IOException("Unexpected Base64 character: " + read);
      }
      read = REVERSE_BASE_64_CHARS[(byte) read];
    } while (read == -1);
    return read;
  }
}