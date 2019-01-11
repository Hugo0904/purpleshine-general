package com.falseapple.general.plugin;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthState;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.LineParser;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;


public final class HttpClientUtil {
    
    // 一般 GET
    static public final Header[] DATA_GET_HEADERS = {
        new BasicHeader("Accept", "text/plain, */*; q=0.01")
        ,new BasicHeader("Accept-Encoding", "gzip, deflate")
        ,new BasicHeader("Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4")
        ,new BasicHeader("Connection", "keep-alive")
        ,new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        ,new BasicHeader("X-Requested-With", "XMLHttpRequest")
        ,new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
   };
        
    // 一般 POST
    static public final Header[] DATA_POST_HEADERS = {
        new BasicHeader("Accept", "text/plain, */*; q=0.01")
        ,new BasicHeader("Accept-Encoding", "gzip, deflate")
        ,new BasicHeader("Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4")
        ,new BasicHeader("Connection", "keep-alive")
        ,new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        ,new BasicHeader("X-Requested-With", "XMLHttpRequest")
        ,new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
    };
    
    // 取得資料用 JSON
    static public final Header[] DATA_JSON_HEADERS = {
        new BasicHeader("Accept", "text/plain, */*; q=0.01")
        ,new BasicHeader("Accept-Encoding", "gzip, deflate, sdch")
        ,new BasicHeader("Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4")
        ,new BasicHeader("Connection", "keep-alive")
        ,new BasicHeader("Content-Type", "application/json; charset=utf8")
        ,new BasicHeader("X-Requested-With", "XMLHttpRequest")
        ,new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
    };
    
    private final HttpClientConfig config;
    
    private CloseableHttpClient httpclient;
    private RequestConfig defaultRequestConfig;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    
    private Consumer<Entry<Throwable, String>> onException;
    
    public HttpClientUtil() {
        this(new HttpClientConfig());
    }
    
    public HttpClientUtil(HttpClientConfig config) {
        this.config = config;
    }
    
    /**
     * 設置若發生Exception時, 所觸發的事件
     * @param onException
     */
    public void setOnException(Consumer<Entry<Throwable, String>> onException) {
        this.onException = onException;
    }

    /**
     * 打開Http
     */
    public void create() {
        // Use custom message parser / writer to customize the way HTTP
        // messages are parsed from and written out to the data stream.
//        HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {
//
//            @Override
//            public HttpMessageParser<HttpResponse> create(
//                SessionInputBuffer buffer, MessageConstraints constraints) {
//                LineParser lineParser = new BasicLineParser() {
//
//                    @Override
//                    public Header parseHeader(final CharArrayBuffer buffer) {
//                        try {
////                          System.out.println(buffer.toString());
//                            return super.parseHeader(buffer);
//                        } catch (ParseException ex) {
//                            return new BasicHeader(buffer.toString(), null);
//                        }
//                    }
//
//                };
//                return new DefaultHttpResponseParser(
//                    buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints) {
//
//                    @Override
//                    protected boolean reject(final CharArrayBuffer line, int count) {
//                        // try to ignore all garbage preceding a status line infinitely
//                        return false;
//                    }
//
//                };
//            }
//        };
//        
//        final HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();
        
        // Use a custom connection factory to customize the process of
        // initialization of outgoing HTTP connections. Beside standard connection
        // configuration parameters HTTP connection factory can define message
        // parser / writer routines to be employed by individual connections.
//        final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(requestWriterFactory, responseParserFactory);

        //HttpConnectionFactory:配置写请求/解析响应处理器
        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                DefaultHttpRequestWriterFactory.INSTANCE,
                DefaultHttpResponseParserFactory.INSTANCE
        );
        
        // Client HTTP connection objects when fully initialized can be bound to
        // an arbitrary network socket. The process of network socket initialization,
        // its connection to a remote address and binding to a local one is controlled
        // by a connection socket factory.

        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", SSLConnectionSocketFactory.getSocketFactory())
            .build();

        // Use custom DNS resolver to override the system DNS resolution.
        final DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

            @Override
            public InetAddress[] resolve(final String host) throws UnknownHostException {
                if (host.equalsIgnoreCase("myhost")) {
                    return new InetAddress[] { InetAddress.getByAddress(new byte[] {127, 0, 0, 1}) };
                } else {
                    return super.resolve(host);
                }
            }
        };

        final int conExpire = 30;// 长连接闲置过期时间
        // Create a connection manager with custom configuration.
        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry,
                connFactory,
                null,
                dnsResolver,
                conExpire,
                TimeUnit.SECONDS);
        
        // Create socket configuration
        final SocketConfig socketConfig = SocketConfig.custom()
            .setSoKeepAlive(true)
            .setTcpNoDelay(true)
            .setSoTimeout(config.getMaxSoTimeout())
            .build();
        
        // Configure the connection manager to use socket configuration either
        // by default or for a specific host.
        connManager.setDefaultSocketConfig(socketConfig);
        connManager.setSocketConfig(new HttpHost("somehost", 80), socketConfig);
        
        // Validate connections after 1 sec of inactivity
        connManager.setValidateAfterInactivity(5000);

        // Create message constraints
        final MessageConstraints messageConstraints = MessageConstraints.custom()
            .setMaxHeaderCount(200)
            .setMaxLineLength(5000)
            .build();
        
        // Create connection configuration
        final ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(Consts.UTF_8)
            .setMessageConstraints(messageConstraints)
            .build();
        
        // Configure the connection manager to use connection configuration either
        // by default or for a specific host.
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);

        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.setMaxTotal(config.getDefaultMaxTotal());
        connManager.setDefaultMaxPerRoute(config.getDefaultMaxPreTotal());
//        connManager.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);

        // Use custom cookie store if necessary.
        cookieStore = new BasicCookieStore();
        // Use custom credentials provider if necessary.
        credentialsProvider = new BasicCredentialsProvider();
        // Create global request configuration
        defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(config.getMaxConnectTimeout()) // Https超時
            .setConnectTimeout(config.getMaxConnectTimeout()) // 連線超時
            .setConnectionRequestTimeout(config.getMaxSoTimeout()) // 請求超時
            .setCookieSpec(CookieSpecs.DEFAULT)
            .setExpectContinueEnabled(true)
            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
            .build();
        
        // 請求失敗嘗試
        final HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= config.getMaxRetry()) { // 對大重試次數
                    return false;
                }
                if (exception instanceof NoHttpResponseException) { // 如果服務器丟失回應
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {   // SSL安全級別不滿足
                    return false;
                }
                if (exception instanceof InterruptedIOException) {  // 超時
                    return false;
                }
                if (exception instanceof UnknownHostException) {    // 目標服務器不可連
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) { // 連接被拒絕
                    return false;
                }
                if (exception instanceof SSLException) {    // SSL異常
                    return false;
                }

                final HttpClientContext clientContext = HttpClientContext.adapt(context);
                final HttpRequest request = clientContext.getRequest();
                
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        
        // Create an HttpClient with the given custom dependencies and configuration.
        httpclient = HttpClients.custom()
            .setConnectionManager(connManager)
            .setConnectionManagerShared(false) //连接池不是共享模式，这个共享是指与其它httpClient是否共享
            .evictIdleConnections(conExpire, TimeUnit.SECONDS)//定期回收空闲连接
            .evictExpiredConnections()//回收过期连接
//            .setConnectionTimeToLive(60, TimeUnit.SECONDS)//连接存活时间，如果不设置，则根据长连接信息决定
            .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)//连接重用策略，即是否能keepAlive
            .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)//长连接配置，即获取长连接生产多长时间
            .setDefaultCookieStore(cookieStore)
            .setDefaultCredentialsProvider(credentialsProvider)
//            .setProxy(new HttpHost("", 8080))
            .setDefaultRequestConfig(defaultRequestConfig)
            .setRetryHandler(httpRequestRetryHandler)
            .build();
        
        /**
         *JVM停止或重启时，关闭连接池释放掉连接
         */
        Runtime.getRuntime().addShutdownHook(new Thread("HttpClientUtil-shutdown-thread") {
            @Override
            public void run() {
                try {
                    if (Objects.nonNull(httpclient)) {
                        httpclient.close();    
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 關閉Http
     * @throws IOException 
     */
    public void close() throws IOException {
        try {
            Objects.requireNonNull(httpclient, "HttpClient尚未打開").close();
        } catch (IOException e) {
            throw e;
        }
    }
    
    public HttpClientConfig getConfig() {
        return config;
    }

    /**
     * Get
     * @param url
     * @param headers
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public ResponseData get(String url, Header[] headers) throws IOException, URISyntaxException {
        return get(url, null, headers);
    }
    
    /**
     * Get
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public ResponseData get(String url, Map<String, String> params, Header[] headers) throws IOException, URISyntaxException {
        final HttpGet httpGet = new HttpGet(url);
        // 帶入參數
        httpGet.setURI(buildGetQuery(httpGet.getURI(), params));
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        return execute(httpGet, headers);
    }
    
    /**
     * Get
     * @param url
     * @param params
     * @param headers
     * @param builder
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public ResponseData get(String url, Map<String, String> params, Header[] headers, Builder builder) throws URISyntaxException, IOException {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setURI(buildGetQuery(httpGet.getURI(), params));
        return execute(httpGet, headers, builder);
    }
    
    /**
     * Post
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData post(final String url, final Map<String, String> params, final Header[] headers) throws ParseException, IOException {
         final HttpPost httpPost = new HttpPost(url);
         final List <NameValuePair> nvps = new ArrayList<>();
         for (Entry<String, String> param : params.entrySet()) {
                nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
         }
         httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
         return execute(httpPost, headers);
    }
    
    /**
     * Post
     * @param url
     * @param params
     * @param headers
     * @param builder
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData post(final String url, final Map<String, String> params, final Header[] headers, Builder builder) throws ParseException, IOException {
        final HttpPost httpPost = new HttpPost(url);
        final List <NameValuePair> nvps = new ArrayList<>();
        for (Entry<String, String> param : params.entrySet()) {
               nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        return execute(httpPost, headers, builder);
    }
    
    /**
     * Post
     * @param url
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData post(final String url, final Header[] headers) throws ParseException, IOException {
        final HttpPost httpPost = new HttpPost(url);
        return execute(httpPost, headers);
    }
    
    /**
     * Post Json字串
     * @param url
     * @param json
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData postJson(final String url, final String json, final Header[] headers) throws ParseException, IOException {
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(json, Consts.UTF_8));
        return execute(httpPost, headers);
    }
    
    /**
     * Post Json字串
     * @param url
     * @param json
     * @param headers
     * @param builder
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData postJson(final String url, final String json, final Header[] headers, Builder builder) throws ParseException, IOException {
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(json, Consts.UTF_8));
        return execute(httpPost, headers, builder);
    }
    
    /**
     * Put
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData put(final String url, final Map<String, String> params, final Header[] headers) throws ParseException, IOException {
        final HttpPut httpPut = new HttpPut(url);
        final List <NameValuePair> nvps = new ArrayList<>();
        for (Entry<String, String> param : params.entrySet()) {
            nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        httpPut.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        return execute(httpPut, headers);
    }
    
    /**
     * Put
     * @param url
     * @param params
     * @param headers
     * @param builder
     * @return
     * @throws IOException
     */
    public ResponseData put(final String url, final Map<String, String> params, final Header[] headers, Builder builder) throws IOException {
        final HttpPut httpPut = new HttpPut(url);
        final List <NameValuePair> nvps = new ArrayList<>();
        for (Entry<String, String> param : params.entrySet()) {
            nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }
        httpPut.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        return execute(httpPut, headers, builder);
    }
    
    /**
     * Put Json字串
     * @param url
     * @param json
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData putJson(final String url, final String json, final Header[] headers) throws ParseException, IOException {
        final HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(json, Consts.UTF_8));
        return execute(httpPut, headers);
    }
    
    /**
     * Put Json字串
     * @param url
     * @param json
     * @param headers
     * @param builder
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public ResponseData putJson(final String url, final String json, final Header[] headers, Builder builder) throws ParseException, IOException {
        final HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(json, Consts.UTF_8));
        return execute(httpPut, headers, builder);
    }
    
    /**
     * 執行http 請求
     * @param httpType
     * @param headers
     * @return
     * @throws IOException
     */
    private ResponseData execute(final HttpRequestBase httpType, final Header[] headers) throws IOException {
        if (httpclient == null) throw new IOException("client 未打開");
        return execute(httpType, headers, getBaseRequestBuilder()); 
    }
    
    /**
     * 執行http 請求
     * @param httpType
     * @param headers
     * @param builder
     * @return
     * @throws IOException
     */
    private ResponseData execute(final HttpRequestBase httpType, final Header[] headers, final Builder builder) throws IOException {
        try {
            httpType.setConfig(builder.build());
            if (Objects.nonNull(headers)) httpType.setHeaders(headers);
             // Execution context can be customized locally.
            final HttpClientContext context = HttpClientContext.create();
            // Contextual attributes set the local context level will take
            // precedence over those set at the client level.
            context.setCredentialsProvider(credentialsProvider);
            final Instant start = Instant.now();
            try (CloseableHttpResponse response = httpclient.execute(httpType, context)) {
                // Once the request has been executed the local context can
                // be used to examine updated state and various objects affected
                // by the request execution.
                final HttpEntity entity = response.getEntity();
                try {
                    final String responseText = EntityUtils.toString(entity, Consts.UTF_8);
                    return new ResponseData(httpType.getURI().toString(), context, response.getStatusLine(), responseText, Duration.between(start, Instant.now()));
                } finally {
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            if (Objects.nonNull(onException)) onException.accept(new AbstractMap.SimpleEntry<Throwable, String>(e, httpType.getURI().toString()));
            throw e;
        } finally {
            httpType.releaseConnection();
        }
    }
    
    /**
     * 取得Http request配置建立器
     * @return
     */
    public Builder getBaseRequestBuilder() {
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        return RequestConfig.copy(defaultRequestConfig)
                .setSocketTimeout(config.getMaxConnectTimeout()) // Https超時
                .setConnectTimeout(config.getMaxConnectTimeout()) // 連線超時
                .setConnectionRequestTimeout(config.getMaxSoTimeout()); // 請求超時
    }
    
    /**
     * 生成Get Query
     * @param uri
     * @param params
     * @return
     * @throws URISyntaxException
     */
    static public URI buildGetQuery(final URI uri, final Map<String, String> params) throws URISyntaxException {
        if (params == null) return uri;
        final URIBuilder builder = new URIBuilder(uri);
        for (Entry<String, String> param : params.entrySet()) {
         builder.addParameter(param.getKey(), param.getValue());
        }
        return builder.build();
    }
    
    /**
     * 生成Get Query
     * @param params
     * @return
     */
    static public String buildGetQuery(final Map<String, String> params) {
        final URIBuilder builder = new URIBuilder();
        for (Entry<String, String> param : params.entrySet()) {
             builder.addParameter(param.getKey(), param.getValue());
        }
        return builder.toString();
    }
    
    /**
     * 生成Query(無?)
     * @param params
     * @return
     */
    static public String buildQuery(final Map<String, String> params) {
        return params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));
    }
    

    /**
     * HttpClient建立設定配置
     * @author yueh
     *
     */
    static public final class HttpClientConfig {
        private int maxConnectTimeout = 30_000; 
        private int maxSoTimeout = 20_000;
        private int defaultMaxTotal = 300;
        private int defaultMaxPreTotal = 200;
        private int maxRetry = 3;
        
        /**
         * 取得最大重試次數
         * @return
         */
        public int getMaxRetry() {
            return maxRetry;
        }

        /**
         * 設置最大重試次數
         * @param maxRetry
         */
        public void setMaxRetry(int maxRetry) {
            if (maxRetry < 1) maxConnectTimeout = 1; 
            this.maxRetry = maxRetry;
        }

        /**
         * 取得最大與伺服器連線時間
         * @return
         */
        public int getMaxConnectTimeout() {
            return maxConnectTimeout;
        }
        
        /**
         * 設置最大與伺服器連線時間
         * @param maxConnectTimeout
         */
        public void setMaxConnectTimeout(int maxConnectTimeout) {
            if (maxConnectTimeout < 5) maxConnectTimeout = 5; 
            this.maxConnectTimeout = maxConnectTimeout;
        }
        
        /**
         * 取得最大等待回應時間
         * @return
         */
        public int getMaxSoTimeout() {
            return maxSoTimeout;
        }
        
        /**
         * 設置 最大等待回應時間
         * @param maxSoTimeout
         */
        public void setMaxSoTimeout(int maxSoTimeout) {
            if (maxConnectTimeout < 3) maxConnectTimeout = 3; 
            this.maxSoTimeout = maxSoTimeout;
        }
        
        /**
         * 取得連線池最大同時請求數量
         * @return
         */
        public int getDefaultMaxTotal() {
            return defaultMaxTotal;
        }
        
        /**
         * 設置連線池最大同時請求數量
         * @param defaultMaxTotal
         */
        public void setDefaultMaxTotal(int defaultMaxTotal) {
            if (maxConnectTimeout < 10) maxConnectTimeout = 10; 
            this.defaultMaxTotal = defaultMaxTotal;
        }
        
        /**
         * 取得單一伺服器最大同時請求數量
         * @return
         */
        public int getDefaultMaxPreTotal() {
            return defaultMaxPreTotal;
        }
        
        /**
         * 設置單一伺服器最大同時請求數量
         * @param defaultMaxPreTotal
         */
        public void setDefaultMaxPreTotal(int defaultMaxPreTotal) {
            if (maxConnectTimeout < 5) maxConnectTimeout = 5; 
            this.defaultMaxPreTotal = defaultMaxPreTotal;
        }
    }
    
    static public final class ResponseData {
        private final String requestUri;
        private final StatusLine statusLine;
        private final HttpRequest httpRequest;
        private final RouteInfo httpRoute;
        private final AuthState targetAuthState;
        private final AuthState proxyAuthState;
        private final CookieOrigin cookieOrigin;
        private final CookieSpec cookieSpec;
        private final Object usetToken;
        private final String httpResponse;
        private final Duration requestTime;

        public ResponseData(String response) {
            this.httpResponse = response;
            this.requestUri = null;
            this.httpRequest = null;
            this.httpRoute = null;
            this.targetAuthState = null;
            this.proxyAuthState = null;
            this.cookieOrigin = null;
            this.cookieSpec = null;
            this.usetToken = null;
            this.statusLine = null;
            this.requestTime = null;
        }
        
        public ResponseData(String requestUri, HttpClientContext context, StatusLine statusLine, String response, Duration requestTime) {
            this.requestUri = requestUri;
            this.httpRequest = context.getRequest();
            this.httpRoute = context.getHttpRoute();
            this.targetAuthState = context.getTargetAuthState();
            this.proxyAuthState = context.getProxyAuthState();
            this.cookieOrigin = context.getCookieOrigin();
            this.cookieSpec = context.getCookieSpec();
            this.usetToken = context.getUserToken();
            this.statusLine = statusLine;
            this.httpResponse = response;
            this.requestTime = requestTime;
        }
        
        public String getRequestUri() {
            return requestUri;
        }

        /**
         * http request status
         * @return
         */
        public StatusLine getStatusLine() {
            return statusLine;
        }
        
        /**
         * Last executed request
         * @return
         */
        public HttpRequest getHttpRequest() {
            return httpRequest;
        }

        /**
         * Execution route
         * @return
         */
        public RouteInfo getHttpRoute() {
            return httpRoute;
        }

        /**
         * Target auth state
         * @return
         */
        public AuthState getTargetAuthState() {
            return targetAuthState;
        }

        /**
         * Proxy auth state
         * @return
         */
        public AuthState getProxyAuthState() {
            return proxyAuthState;
        }

        /**
         * Cookie origin
         * @return
         */
        public CookieOrigin getCookieOrigin() {
            return cookieOrigin;
        }

        /**
         * Cookie spec used
         * @return
         */
        public CookieSpec getCookieSpec() {
            return cookieSpec;
        }

        /**
         * User security token
         * @return
         */
        public Object getUsetToken() {
            return usetToken;
        }

        /**
         * response
         * @return
         */
        public String getHttpResponse() {
            return httpResponse;
        }

        /**
         * 請求耗時
         * @return
         */
        public Duration getRequestTime() {
            return requestTime;
        }
    }
}