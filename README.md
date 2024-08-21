# App List
> 这是一个用户辅助开发者、测试、运营、变现的工具。
> 主要功能有：
> 
> * 检索设备本地的应用
> * 展示应用的名称、图标、包名信息（主要场景在于将包名与应用名关联起来）
> * 快速打开应用设置页面
> * 快速复制包名信息
> * 快速打开应用
> * 整理应用包的内置SDK信息
> 
> [下载地址](http://192.168.100.240:2025/files/AppList/)


<!-- TOC -->
* [App List](#app-list)
  * [手机版功能说明](#手机版功能说明)
    * [搜索](#搜索)
    * [悬浮按钮](#悬浮按钮)
    * [列表](#列表)
    * [SDK列表](#sdk列表)
      * [筛选](#筛选)
      * [关键词](#关键词)
      * [保存](#保存)
  * [桌面版功能说明](#桌面版功能说明)
    * [解析](#解析)
    * [模式切换](#模式切换)
    * [切换应用](#切换应用)
    * [SDK列表](#sdk列表-1)
    * [源码检索](#源码检索)
<!-- TOC -->

## 手机版功能说明
### 搜索

<div align="left">
<img src="doc/app_list_search.png" width="30%" />
<img src="doc/app_list_search_result.png" width="30%" />
</div>

### 悬浮按钮
<div align="left">
<img src="doc/app_list_fab.png" width="30%" />
<img src="doc/app_list_fab_menu.png" width="30%" />
</div>

### 列表
<div align="left">
<img src="doc/app_list_item.png" width="30%" />
<img src="doc/app_list_item_menu.png" width="30%" />
</div>


### SDK列表
<div align="left">
<img src="doc/app_info.png" width="30%" />
</div>

#### 筛选
<div align="left">
<img src="doc/app_info_filter_option.png" width="30%" />
<img src="doc/app_info_filter.png" width="30%" />
</div>

#### 关键词
<div align="left">
<img src="doc/app_info_hint_action.png" width="30%" />
<img src="doc/app_sdk_list.png" width="30%" />
</div>


#### 保存
<div align="left">
<img src="doc/app_info_save_option.png" width="30%" />
</div>


## 桌面版功能说明

桌面版与手机版的基本功能相同，但是实现方案不一致，因此可以做一些额外分析，比如源码层面的SDK解析。

### 解析

为了符合桌面端的操作习惯，我们这里提供的是拖拽的方式。

将APK，AAB，Jar等文件拖拽到应用窗口，即可解析出对应的SDK信息。

也可以一次拖拽多个应用，来同时完成解析，但是可能会受到电脑本身的限制，会占用过多的内存与CPU算力。

<div align="left">
<img src="doc/desktop_default.png" width="30%" />
<img src="doc/desktop_drop.png" width="30%" />
<img src="doc/desktop_info.png" width="30%" />
</div>

### 模式切换
<div align="left">
<img src="doc/desktop_mode.png" width="40%" />
</div>

### 切换应用
<div align="left">
<img src="doc/desktop_info_list.png" width="40%" />
<img src="doc/desktop_list.png" width="40%" />
</div>

### SDK列表
<div align="left">
<img src="doc/desktop_default_hint.png" width="40%" />
<img src="doc/desktop_hint.png" width="40%" />
</div>

### 源码检索
<div align="left">
<img src="doc/desktop_info_source.png" width="40%" />
<img src="doc/desktop_source.png" width="40%" />
</div>

