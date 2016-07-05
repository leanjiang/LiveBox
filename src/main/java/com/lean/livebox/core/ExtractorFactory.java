package com.lean.livebox.core;

import com.lean.livebox.core.extractors.DouyuExtractor;
import com.lean.livebox.core.extractors.PandaExtractor;
import com.lean.livebox.core.extractors.ZhanqiExtractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lean on 16/7/5.
 */
public class ExtractorFactory {
    private final static Pattern PLATFORM_PATTERN = Pattern.compile("^http://www\\.(.+)\\.[tv|com]");

    public static Platform determinePlatform(String url) {
        Matcher matcher = PLATFORM_PATTERN.matcher(url);
        if (matcher.find()) {
            String domain = matcher.group(1);
            return Platform.valueOf(domain.toUpperCase());
        }

        return null;
    }

    public static Extractor createExtractor(String url) {
        Platform platform = determinePlatform(url);
        if (platform != null) {
            switch (platform) {
                case DOUYU:
                    return new DouyuExtractor();
                case ZHANQI:
                    return new ZhanqiExtractor();
                case PANDA:
                    return new PandaExtractor();
            }
        }

        return null;
    }

    public static void main(String[] args) {
        Platform platform = determinePlatform("http://www.zhanqi.tv/naigege");
        System.out.println(platform);
    }
}
