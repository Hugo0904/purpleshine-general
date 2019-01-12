package com.purpleshine.general.plugin;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;

/**
 * @author yueh
 * 建立Get query的請求資料
 */
public final class QueryEncoder {
    
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final char QP_SEP_A = '&';
    private static final int RADIX = 16;
    private static final BitSet UNRESERVED = new BitSet(256);

    static {
        for (int i = 'a'; i <= 'z'; i++) {
            UNRESERVED.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            UNRESERVED.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            UNRESERVED.set(i);
        }
        UNRESERVED.set('_');
        UNRESERVED.set('-');
        UNRESERVED.set('.');
        UNRESERVED.set('*');
    }
    
    static public QueryEncoder build(final Charset charset) {
        return new QueryEncoder(charset);
    }
    
    private final List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
    private final Charset charset;
    private boolean lowerCase;
    private boolean useQuestionMark = true;
    
    private QueryEncoder(final Charset charset) {
        this.charset = charset == null ? Consts.UTF_8 : charset;
    }

    /**
     * 設置參數
     * @param param
     * @param value
     * @return
     */
    public QueryEncoder addParameter(final String param, final String value) {
        this.queryParams.add(new BasicNameValuePair(param, value));
        return this;
    }
    
    /**
     * 透過Map設置多個參數
     * @param params
     * @return
     */
    public QueryEncoder addParameters(final Map<String, String> params) {
        Objects.requireNonNull(params, "params").entrySet().forEach(i -> addParameter(i.getKey(), i.getValue()));
        return this;
    }
    
    /**
     * encode是否轉換為小寫
     * @param lowerCase
     * @return
     */
    public QueryEncoder setEcondeLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
        return this;
    }

    /**
     * 設置輸出時是否在前面加入?
     * @param lowerCase
     * @return
     */
    public QueryEncoder setUseQuestionMark(boolean useQuestionMark) {
        this.useQuestionMark = useQuestionMark;
        return this;
    }
    
    @Override
    public String toString() {
        Args.notNull(queryParams, "Parameters");
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : queryParams) {
            final String encodedName = urlEncode(parameter.getName());
            final String encodedValue = urlEncode(parameter.getValue());
            if (result.length() > 0) {
                result.append(QP_SEP_A);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return (useQuestionMark ? "?" : "") + result.toString();
    }

    private String urlEncode( final String content) {
        if (content == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final ByteBuffer bb = charset.encode(content);
        while (bb.hasRemaining()) {
            final int b = bb.get() & 0xff;
            if (UNRESERVED.get(b)) {
                buf.append((char) b);
            } else if (b == ' ') {
                buf.append('+');
            } else {
                buf.append("%");
                final char hex1 = Character.forDigit((b >> 4) & 0xF, RADIX);
                final char hex2 = Character.forDigit(b & 0xF, RADIX);
                buf.append(lowerCase ? Character.toLowerCase(hex1) : Character.toUpperCase(hex1));
                buf.append(lowerCase ? Character.toLowerCase(hex2) : Character.toUpperCase(hex2));
            }
        }
        return buf.toString();
    }
}
