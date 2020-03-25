package com.purpleshine.general.helpers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;

public final class QueryUtil {

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
	 * 將字串經過A-Z排序, 並轉換成query
	 * @param collection
	 * @return
	 */
	static public String buildSordQuery(final Map<String, ?> collection) {
		if (collection.isEmpty()) return "";
		final List<Entry<String, ?>> list = collection.entrySet().stream()
				.sorted(Comparator.comparing(e -> e.getKey()))
				.collect(Collectors.toList());
		final StringBuilder sb = new StringBuilder();
		for (Entry<String, ?> entry : list) {
			sb.append("&" + entry.getKey() + "=" + entry.getValue().toString());
		}
		return sb.substring(1);
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
     * Recodes a URL-encoded string to ensure that all hex digits in the
     * percent codes that are not decimal digits are expressed in lowercase.
     */
    static public String encodeLowerCase(String urlString) {
        final StringBuilder sb = new StringBuilder();
        final Matcher m = Pattern.compile("%[0-9A-Fa-f]{2}").matcher(urlString);

        while (m.find()) {
            m.appendReplacement(sb, m.group().toLowerCase());
        }
        m.appendTail(sb);

        return sb.toString();
    }
    
    /**
     * Recodes a URL-encoded string to ensure that all hex digits in the
     * percent codes that are not decimal digits are expressed in lowercase.
     */
    static public String encodeUpperCase(String urlString) {
        final StringBuilder sb = new StringBuilder();
        final Matcher m = Pattern.compile("%[0-9A-Fa-f]{2}").matcher(urlString);

        while (m.find()) {
            m.appendReplacement(sb, m.group().toUpperCase());
        }
        m.appendTail(sb);

        return sb.toString();
    }
}
