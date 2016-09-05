XPlayer
====
[Demo apk下载](http://fir.im/j2af)
<br><br>
[解码库下载](http://pan.baidu.com/share/link?shareid=799230074&uk=3356128450)
use
====
In this App,load phone storage all video files based on [MediaLoader](https://github.com/jiajunhui/MediaLoader)
<br>
<br>
```java
/** 设置解码模式*/
mXPlayer.setDecodeMode(new PlayerMenu().getDecodeMode(decodeMode));
/** 设置渲染视图类型*/
mXPlayer.setViewType(ViewType.SURFACEVIEW);
/** 是否显示播放帧率等信息*/
mXPlayer.showTableLayout();
/** 播放事件监听*/
mXPlayer.setOnPlayerEventListener(this);
/** 播放错误监听*/
mXPlayer.setOnErrorListener(this);
/** 播放指定的资源*/
mXPlayer.setData(url);
/** 启动播放*/
mXPlayer.start();
```
