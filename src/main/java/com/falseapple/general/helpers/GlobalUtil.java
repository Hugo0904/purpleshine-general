package com.falseapple.general.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GlobalUtil {
    
    static private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    /**
     * 將布林值顛倒
     * @param booleanValue
     * @return
     */
    static public boolean reverseBool(boolean booleanValue) {
        return !booleanValue;
    }
    
    /**
     * 產生指令數量的亂數隨機字母數字
     * @param count
     * @return
     */
    static public String randomAlphaNumeric(int count) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, Math.min(count, 32));
    }
    
    /**
     * Gzip壓縮字串
     * @param data - 要壓縮的字串
     * @return - 壓縮後的byte[]
     * @throws IOException
     */
    static public byte[] gzipCompress(String data) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(data.length())) {
            try (GZIPOutputStream gos = new GZIPOutputStream(os)) {
                gos.write(data.getBytes("UTF-8"));
            }
            return os.toByteArray();
        }
    }
    
    /**
     * Gzip壓縮字串
     * @param data - 要壓縮的byte[]
     * @return - 壓縮後的byte[]
     * @throws IOException
     */
    static public byte[] gzipCompress(byte[] data) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gos = new GZIPOutputStream(os)) {
                gos.write(data);
            }
             return os.toByteArray();
        }
    }

    /**
     * 解壓縮Gzip字串
     * @param compressed 壓縮後的Gzip
     * @return 解壓縮後的字串
     * @throws IOException
     */
    static public String gzipDecompress(byte[] compressed) throws IOException {
        if (isGzipCompressed(compressed)) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
                GZIPInputStream gis = new GZIPInputStream(bis);
                BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } else {
            return new String(compressed);
        }
    }
    
    /**
     * 判斷該是否為Gzip
     * @param compressed - 判斷byte[]
     * @return - ture if is gzip file.
     */
    static public boolean isGzipCompressed(byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
    
    /** 
     * 取得指定class所有的field 包括父類別
     * @param mainclass - class名稱
     * @param filtList - 要過濾的 field組
     * @return - 返回所有field, 不包括過濾的
     */
//  static public LinkedList<Field> getClassFields(Class<?> mainclass, List<String> filtList) {
//      LinkedList<Field> fieldList = new LinkedList<>();
//      LinkedList<Class<?>> classes = new LinkedList<>(); 
//      classes.add(mainclass);
//      
//      // 找到所有類別,包括父類別
//      Class<?> current = mainclass;
//      while (true) {
//          current = current.getSuperclass();
//          if (current != null) {
//              classes.add(current);
//          } else {
//              break;
//          }
//      }
//      for(Class<?> _class : Lists.reverse(classes)) {
//          for (Field _field : _class.getDeclaredFields()) {
//              if (filtList == null || ! filtList.contains(_field.getName()))
//                  fieldList.add(_field);
//          }
//      }
//      return fieldList;
//  }
    
    /** 
     * 取得指定class所有的field 包括父類別
     * @param mainclass - class名稱
     * @param filtList - 要過濾的 field組
     * @return - 返回所有field, 不包括過濾的
     */
    static public LinkedList<Field> getClassFields(Class<?> mainclass, List<String> filtList) {
        LinkedList<Field> fieldList = new LinkedList<>();
        
        // 找到所有類別,包括父類別
        if (mainclass.getSuperclass() != null) {
            getClassFields(mainclass.getSuperclass(), filtList).stream()
                .filter(i -> !fieldList.contains(i))
                .forEach(fieldList::add);
        }
        
        Stream.of(mainclass.getDeclaredFields())
            .filter(i -> filtList == null || ! filtList.contains(i.getName()))
            .forEach(fieldList::add);
        
        return fieldList;
    }
    
    /**
     * 將ByteBuffer解析成文字
     * @param buff - buff容器
     * @return - 解析後的文字
     * @throws CharacterCodingException - 出現無法解析的位元
     */
    static public String getCharsetDecoder(ByteBuffer buff) throws CharacterCodingException {
        return decoder.decode(buff).toString();
    }

    static public boolean isIP(String addr)  
    {  
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))  
        {  
            return false;  
        }  
        /** 
         * 判断IP格式和范围 
         */  
        String rexp = "^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))$";  

        Pattern pat = Pattern.compile(rexp);    

        Matcher mat = pat.matcher(addr);    

        boolean ipAddress = mat.find();  

        return ipAddress;  
    }
}