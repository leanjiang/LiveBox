# LiveBox
这个工具能够根据URL获取目前的直播平台（战旗、斗鱼、熊猫TV）的直播源。破解思路来自于you-get,本人无聊将其翻译成Java版本的。用法非常简单


```java
## 所有的数据都在Live这个model中
Live live = LiveBox.extractLive("url");
```
