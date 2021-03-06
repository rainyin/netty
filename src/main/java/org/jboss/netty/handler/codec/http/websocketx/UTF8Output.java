/*
 * Adaptation of http://bjoern.hoehrmann.de/utf-8/decoder/dfa/
 *
 * Copyright (c) 2008-2009 Bjoern Hoehrmann <bjoern@hoehrmann.de>
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *     documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
 *     the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
 *     to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all copies or substantial portions 
 *     of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 *     THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 *     CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 *     IN THE SOFTWARE.
 */
package org.jboss.netty.handler.codec.http.websocketx;

/**
 * Checks UTF8 bytes for validity before converting it into a string
 * 
 * @author Bjoern Hoehrmann
 * @author https://github.com/joewalnes/webbit
 * @author <a href="http://www.veebsbraindump.com/">Vibul Imtarnasan</a>
 */
public class UTF8Output {
    private static final int UTF8_ACCEPT = 0;
    private static final int UTF8_REJECT = 12;

    private static final byte[] TYPES = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 };

    private static final byte[] STATES = { 0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12,
            12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };

    private int state = UTF8_ACCEPT;
    private int codep = 0;

    private final StringBuilder stringBuilder;

    public UTF8Output(byte[] bytes) {
        stringBuilder = new StringBuilder(bytes.length);
        write(bytes);
    }

    public void write(byte[] bytes) {
        for (byte b : bytes) {
            write(b);
        }
    }

    public void write(int b) {
        byte type = TYPES[b & 0xFF];

        codep = (state != UTF8_ACCEPT) ? (b & 0x3f) | (codep << 6) : (0xff >> type) & (b);

        state = STATES[state + type];

        if (state == UTF8_ACCEPT) {
            stringBuilder.append((char) codep);
        } else if (state == UTF8_REJECT) {
            throw new UTF8Exception("bytes are not UTF-8");
        }
    }

    public String toString() {
        if (state != UTF8_ACCEPT) {
            throw new UTF8Exception("bytes are not UTF-8");
        }
        return stringBuilder.toString();
    }
}
