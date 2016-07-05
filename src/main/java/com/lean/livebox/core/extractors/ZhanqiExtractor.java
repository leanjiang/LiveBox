package com.lean.livebox.core.extractors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lean.livebox.core.Extractor;
import com.lean.livebox.core.Live;
import com.lean.livebox.core.Platform;
import com.lean.livebox.utils.MD5Utils;
import com.lean.livebox.utils.NetworkUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lean on 16/7/5.
 */
public class ZhanqiExtractor implements Extractor {
    private static final String LIVE_URL_FORMATTER = "rtmp://dlrtmp.cdn.zhanqi.tv/zqlive/%s?k=%s&t=%s";
    private static final CharSequence KEY_MASK = "#{&..?!(";
    private static final Pattern DATA_PATTERN = Pattern.compile("window\\.oPageConfig\\.oRoom[\\s*]=[\\s*](\\{.+\\});");
    private static final Pattern AK2_PATTERN = Pattern.compile("\\d-([^|]+)");
    private static final String VIDEO_TYPE_LIVE = "LIVE";
    private static final String VIDEO_TYPE_VOD = "VOD";

    public Live extract(String url) {
        try {
            final String content = NetworkUtils.loadContent(url);
            final String json = fetchRoomData(content);
            if (StringUtils.isNotEmpty(json)) {
                JSONObject data = JSON.parseObject(json);
                return createLive(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Live createLive(JSONObject data) throws UnsupportedEncodingException {
        Live live = new Live(Platform.ZHANQI);

        live.setRoomName(data.getString("title"));
        live.setRoomLogo(data.getString("bpic"));
        live.setOwnerName(data.getString("nickname"));
        live.setOwnerAvatar(data.getString("avatar"));

        JSONObject video = data.getJSONObject("flashvars");
        if (!video.isEmpty()) {
            boolean online = video.getInteger("Status") == 4;
            live.setOnline(online);
            live.setRoomId(String.valueOf(video.getLongValue("RoomId")));
            if (online) {
                live.setOnlineCount(video.getLong("Online"));
            }

            String videoType = video.getString("VideoType");
            if (VIDEO_TYPE_LIVE.equals(videoType)) {
                String cdns = video.getString("cdns");
                JSONObject cdnsData = getCdnsData(cdns);

                final String videoId = data.getString("videoId");
                String timeHex = getTimeHex();

                String cdn1 = getCdn(cdnsData.getString("ak"));
                String key1 = generateKey(cdn1, videoId, timeHex);
                String url1 = generateLiveUrl(videoId, key1, timeHex);
                live.setNormalUrl(url1);

                String cdn2 = getCdn(cdnsData.getString("ak2"));
                String key2 = generateKey(cdn2, videoId, timeHex);
                String url2 = generateLiveUrl(videoId, key2, timeHex);
                live.setHdUrl(url2);
            }
        }

        return live;
    }

    private static String getCdn(String ak) throws UnsupportedEncodingException {
        Matcher matcher = AK2_PATTERN.matcher(ak);
        if (matcher.find()) {
            String firstCdn = matcher.group(1);
            return decodeBase64(firstCdn);
        }

        return null;
    }

    private static String generateLiveUrl(String videoId, String key, String timeHex) {
        return String.format(LIVE_URL_FORMATTER, videoId, key, timeHex);
    }

    private static String decodeBase64(String source) throws UnsupportedEncodingException {
        return new String(Base64.decodeBase64(source), "UTF-8");
    }

    private static JSONObject getCdnsData(String cdns) throws UnsupportedEncodingException {
        String json = decodeBase64(cdns);
        return JSON.parseObject(json);
    }

    private static String generateKey(String cdn, String videoId, String timeHex) throws UnsupportedEncodingException {
        String key = "";
        for (int i = 0, len = cdn.length(); i < len; i++) {
            char c = cdn.charAt(i);
            char maskChar = KEY_MASK.charAt(i % 8);
            key += c ^ maskChar;
        }

        String maskedKey = key + "/zqlive/" + videoId + timeHex;
        return MD5Utils.toMD5Hex(maskedKey.getBytes("UTF-8"));
    }

    private static String getTimeHex() {
        return Long.toHexString(System.currentTimeMillis() / 1000);
    }

    private static String fetchRoomData(String content) {
        Matcher matcher = DATA_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String url = "http://www.zhanqi.tv/naigege";
        ZhanqiExtractor extractor = new ZhanqiExtractor();
        Live live = extractor.extract(url);
        System.out.println(live);
    }
}
