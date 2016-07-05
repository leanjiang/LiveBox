package com.lean.livebox.core.extractors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lean.livebox.core.Extractor;
import com.lean.livebox.core.Live;
import com.lean.livebox.core.Platform;
import com.lean.livebox.utils.NetworkUtils;

/**
 * Created by lean on 16/7/5.
 */
public class PandaExtractor implements Extractor {

    private static final String REQUEST_URL_FORMATTER = "http://www.panda.tv/api_room_v2?roomid=%s&pub_key=&_=%d";
    private static final String HD_URL_FORMATTER = "http://pl3.live.panda.tv/live_panda/%s_mid.flv";
    private static final String SD_URL_FORMATTER = "http://pl3.live.panda.tv/live_panda/%s.flv";


    public Live extract(String url) {
        final String roomId = extractRoomId(url);
        final String liveUrl = generateLiveUrl(roomId);
        try {
            String responseString = NetworkUtils.loadContent(liveUrl);
            JSONObject response = JSON.parseObject(responseString);
            int errorCode = response.getInteger("errno");
            if (errorCode != 0) {
                String errorMessage = response.getString("errmsg");
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
        Live live = new Live(Platform.PANDA);

        JSONObject room = data.getJSONObject("roominfo");
        if (!room.isEmpty()) {
            live.setRoomId(room.getString("id"));
            live.setRoomName(room.getString("name"));

            JSONObject logo = room.getJSONObject("pictures");
            live.setRoomLogo(logo.getString("img"));
            live.setOnlineCount(Long.valueOf(room.getString("person_num")));
        }

        JSONObject owner = data.getJSONObject("hostinfo");
        if (!owner.isEmpty()) {
            live.setOwnerName(owner.getString("name"));
            live.setOwnerAvatar(owner.getString("avatar"));
        }

        JSONObject video = data.getJSONObject("videoinfo");
        if (!video.isEmpty()) {
            boolean online = "2".equals(video.getString("status"));
            live.setOnline(online);
            String roomKey = video.getString("room_key");

            String hdUrl = String.format(HD_URL_FORMATTER, roomKey);
            String sdUrl = String.format(SD_URL_FORMATTER, roomKey);

            live.setHdUrl(hdUrl);
            live.setSdUrl(sdUrl);

        }


        return live;
    }

    private static String extractRoomId(String url) {
        int position = url.lastIndexOf("/") + 1;
        return url.substring(position);
    }

    private static String generateLiveUrl(String roomId) {
        return String.format(REQUEST_URL_FORMATTER, roomId, System.currentTimeMillis());
    }

    public static void main(String[] args) {
        String url = "http://www.panda.tv/45449";
        PandaExtractor extractor = new PandaExtractor();
        Live live = extractor.extract(url);
        System.out.println(live);
    }
}
