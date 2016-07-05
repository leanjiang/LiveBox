package com.lean.livebox.core.extractors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lean.livebox.core.Extractor;
import com.lean.livebox.core.Live;
import com.lean.livebox.core.Platform;
import com.lean.livebox.utils.MD5Utils;
import com.lean.livebox.utils.NetworkUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by lean on 16/7/5.
 */
public class DouyuExtractor implements Extractor {
    private static final String SUFFIX_FORMATTER = "room/%s?aid=android&client_sys=android&time=%d";
    private static final String REQUEST_URL_FORMATTER = "http://www.douyu.com/api/v1/%s&auth=%s";
    private static final String MIXED_CODE = "1231";

    public Live extract(String url) {
        try {
            final String roomId = extractRoomId(url);
            final String suffix = generateSuffix(roomId);
            final String sign = generateSign(suffix);
            final String requestUrl = generateLiveUrl(suffix, sign);
            String json = NetworkUtils.loadContent(requestUrl);
            JSONObject response = JSON.parseObject(json);
            int errorCode = response.getInteger("error");
            if (errorCode > 0) {
                String errorMessage = response.getString("response");
                throw new RuntimeException(errorMessage);
            }

            JSONObject data = response.getJSONObject("data");
            return createLive(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Live createLive(JSONObject data) {
        Live live = new Live(Platform.DOUYU);
        live.setRoomId(data.getString("room_id"));
        live.setRoomName(data.getString("room_name"));
        live.setRoomLogo(data.getString("room_src"));
        live.setOnlineCount(data.getLong("online"));
        live.setOwnerName(data.getString("nickname"));
        live.setOwnerAvatar(data.getString("owner_avatar"));
        boolean online = "1".equals(data.getString("show_status"));
        live.setOnline(online);

        if (online) {
            String rtmpUrl = data.getString("rtmp_url");
            String rtmpNormalLive = data.getString("rtmp_live");
            live.setNormalUrl(rtmpUrl + "/" + rtmpNormalLive);

            JSONObject multiBitrate = data.getJSONObject("rtmp_multi_bitrate");
            if (!multiBitrate.isEmpty()) {
                String rtmpHDLive = multiBitrate.getString("middle");
                String rtmpSDLive = multiBitrate.getString("middle2");
                live.setHdUrl(rtmpUrl + "/" + rtmpHDLive);
                live.setSdUrl(rtmpUrl + "/" + rtmpSDLive);
            }

            live.setM3u8Url(data.getString("hls_url"));
        }

        return live;
    }

    private static String extractRoomId(String url) {
        if (StringUtils.isEmpty(url)) return null;
        int position = url.lastIndexOf("/") + 1;
        return url.substring(position);
    }

    private static String generateSuffix(String roomId) {
        return String.format(SUFFIX_FORMATTER, roomId, System.currentTimeMillis() / 1000);
    }

    private static String generateSign(String suffix) throws UnsupportedEncodingException {
        String mixedString = suffix + MIXED_CODE;
        byte[] sources = mixedString.getBytes("us-ascii");
        return MD5Utils.toMD5Hex(sources);
    }

    private static String generateLiveUrl(String suffix, String sign) {
        return String.format(REQUEST_URL_FORMATTER, suffix, sign);
    }

    public static void main(String[] args) throws Exception {
        String url = "http://www.douyu.com/67554";
        DouyuExtractor extractor = new DouyuExtractor();
        Live live = extractor.extract(url);
        System.out.println(live);

    }
}
