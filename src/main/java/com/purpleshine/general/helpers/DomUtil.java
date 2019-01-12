package com.purpleshine.general.helpers;

import org.jsoup.nodes.Document;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;

/**
 * @author user
 * Document操作相關
 */
public final class DomUtil {
	
	/**
	 * 將XML內容轉換為Document
	 * @param content
	 * @param preserveTagCase tag大小寫是否保留原大小
	 * @param preserveAttributeCase 標籤大小寫是否保留原大小
	 * @return
	 */
	static public Document parseDom(String content, boolean preserveTagCase, boolean preserveAttributeCase) {
		final Parser parser = Parser.xmlParser();
		parser.settings(new ParseSettings(preserveTagCase, preserveAttributeCase));
		return parser.parseInput(content, "");
	}
}
