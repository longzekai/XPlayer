use
====
```java
/** 设置解码模式*/
mVideoPlayer.setDecodeMode(DecodeMode.HARD);
/** 设置渲染视图类型*/
mVideoPlayer.setViewType(ViewType.SURFACEVIEW);
/** 是否使用默认的播放控制器*/
mVideoPlayer.useDefaultPlayControl(true);
/** 是否显示播放帧率等信息*/
mVideoPlayer.showTableLayout();
/** 是否使用默认的加载样式*/
mVideoPlayer.setUseDefaultLoadingStyle(true);
/** 播放指定的资源*/
mVideoPlayer.play(url);
```
<br>
```java
/** 设置准备完成的监听器*/
mVideoPlayer.setOnPreparedListener(this);
/** 设置播放信息监听器*/
mVideoPlayer.setOnPlayerInfoListener(this);
/** 设置错误信息监听器*/
mVideoPlayer.setOnErrorListener(this);
/** 设置定位完成的监听器*/
mVideoPlayer.setOnSeekCompleteListener(this);
/** 设置播放完成的监听器*/
mVideoPlayer.setOnCompletionListener(this);
/** 设置滑动手势监听器*/
mVideoPlayer.setOnSlideHandleListener(this);
```
