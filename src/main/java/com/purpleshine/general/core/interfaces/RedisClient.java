package com.purpleshine.general.core.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

public interface RedisClient {
    
    /**
     * 刪除key
     * @param key
     * @return
     */
    public long del(final String key);
    
    /**
     * 刪除多筆key
     * @param key
     * @return
     */
    public long del(final String... keys);
    
    /**
     * 設置存活時間
     * @param key
     * @param seconds
     * @return
     */
    public long expire(final String key, final int seconds);
    
    /**
     * 判斷Redis有無指定key
     * @param key
     * @return
     */
    public boolean exists(final String key);
    
    /**
     * get資料
     * @param key
     * @return
     */
    public String get(final String key);
    
    /**
     * set資料
     * @param key
     * @param value
     */
    public String set(final String key, final String value);
    
    /**
     * Hdel資料
     * @param key
     * @param fields
     * @return
     */
    public long hdel(final String key, final String... fields);
    
    /**
     * Hset資料
     * @param key
     * @param field
     * @param value
     * @return
     */
    public long hset(final String key, final String field, final String value);
    
    /**
     * 設置多個field至Hash
     * @param key
     * @param hash
     * @return
     */
    public String hmset(final String key, final Map<String, String> hash);
    
    /**
     * 取得Hash所有field
     * @param key
     * @return
     */
    public Map<String, String> hgetall(final String key);
    
    /**
     * 
     * @param key
     * @param field
     * @return
     */
    public String hget(final String key, final String field);
    
    /**
     * 取得Hash 指定field
     * 結果會依照取得的順序設置
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(final String key, final String... fields);
    
    /**
     * 判斷Hash內有無該field
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(final String key, final String field);
    
    /**
     * key的scan
     * @param cursor 光標
     * @param match 查詢
     * @param count 數量
     * @return
     */
    public ScanResult<String> scan(final String cursor, String match, final int count);
    
    /**
     * Hash的scan
     * @param key
     * @param cursor
     * @param match
     * @param count
     * @return
     */
    public ScanResult<Entry<String, String>> hscan(final String key, final String cursor, final String match, final int count);
    
    /**
     * Set的scan
     * @param key
     * @param cursor
     * @param match
     * @param count
     * @return
     */
    public ScanResult<String> sscan(final String key, final String cursor, final String match, final int count);
    
    /**
     * Zset的scan
     * @param key
     * @param cursor
     * @param match
     * @param count
     * @return
     */
    public ScanResult<Tuple> zscan(final String key, final String cursor, final String match, final int count);

}
