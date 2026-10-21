# blbl-android（个人自用修改版）

> **⚠️ 关于本仓库（务必先读）**
>
> 本仓库是开源项目 **[cat3399/blbl](https://github.com/cat3399/blbl)** 的 **个人自用 / 学习用改版（fork）**。
>
> - **不是官方版本，也不属于上游项目**。所有改动仅出于个人学习 Android 开发与自用需求，不追求通用性，也不保证稳定性。
> - 上游与本改版无任何关联；作者未参与、也未认可这里的修改。
> - 需要稳定、可长期使用的版本，请前往 [上游 Releases](https://github.com/cat3399/blbl/releases) 下载。
> - 代码仅用于学习交流，请遵守上游 README 中的免责声明，不要宣传、不要用于任何商业或盈利用途。
>
> **改动基线**：上游 `main` / tag `v0.1.29`（commit `cd09bd9`）。
>
> **维护约定**：每次提交涉及功能变化，都会同步更新下面的「相对上游的新特性」与「改动记录」。

---

## 相对上游的新特性

以下内容为本仓库在上游基础上的新增或修改，按模块归类。

### 📺 播放器：一级按钮栏直接点赞 / 投币 / 收藏 + 长按一键三连

- **三个按钮提到一级控制栏**：点赞、投币、收藏不再是「进详情面板才能点」，直接出现在播放器底部一级按钮栏，默认可见。
  仍可在 `设置 - OSD 按钮显示` 中单独关闭。
- **长按 1 秒触发一键三连**：按住点赞 / 投币 / 收藏中的**任意一个**约 1 秒松手，即完成「点赞 + 投币 + 收藏」三连。
  触摸长按与遥控器确认键长按都支持，按住期间按钮边缘显示进度环，能看出还差多久。
- **短按仍是单项操作**：点赞为切换、投币为投 1 币、收藏为选择收藏夹，不会误触发三连。

### 🚫 全站黑名单屏蔽

- 新增 `设置 - 全站拉黑` 开关（默认关闭）。开启后，B 站账号黑名单中的 UP 主视频会从 **推荐、动态、热点** 等列表统一过滤掉。
- 黑名单通过 `/x/relation/blacks` 分页拉取（每页 50，最多 100 页兜底），本地缓存复用；启动时若有登录态则后台自动刷新。
- 关闭开关时会提示「已拉黑 UP 需在其主页取消拉黑」，避免误以为能一键还原。

### 🌶️ UP 主主页拉黑按钮

- UP 主主页新增「拉黑 / 取消拉黑」按钮，直接调 `modifyRelation`（act=5/6），无需跳到 B 站客户端。
- 拉黑成功会同步取消关注；拉黑提示 🌶️、关注提示 ❤️。
- 自己不能拉黑自己；未登录时引导扫码登录。

### 🔥 动态页「热点」筛选

- 关注列表顶部新增「热点」tab：从全站动态流中筛选 **播放量 ≥ 10 万 或 弹幕 ≥ 500** 的视频，快速找到当下热门内容。
- 进入动态页时即后台预加载（最多 10 页 / 24 条），点击「热点」时立即消费缓存，不必等全量拉取完成。
- 缓存尚未就绪时会短暂轮询（每 200ms，最多 2 秒）并提示「热点加载中…」，避免空白页。

### ⚡ 动态页性能优化

- 关注列表与动态流改为**并行加载**，减少首屏等待。
- 下拉刷新改为延迟清空（`appendOrSubmit`），避免刷新瞬间列表闪白。
- 视频卡片列表由 `notifyDataSetChanged` 改为 **DiffUtil 增量更新**，并调大 item view 缓存。

### 📦 双产品变体，可并行安装

- 新增 `original` 与 `tao` 两个变体：
  - `original`：`blbl.cat3399`，与上游一致
  - `tao`：`blbl.cat3399.tao`，独立启动图标，可与原版**同时安装**，方便新旧版本对比

### 🔧 构建相关

- 本仓库**不包含 release 签名密钥**（`keystore/release.keystore`），因此只能构建 debug 包；需要 release 包请自行准备密钥。
- 版本号仍走上游的 `-P` 注入方式，未硬编码：

```
./gradlew assembleDebug -PversionCode=30 -PversionName="0.1.30-一级按钮三连"
```

---

## 改动记录

| 构建版本 | 内容 |
|---|---|
| `0.1.30-一级按钮三连` | 点赞/投币/收藏提到一级按钮栏，长按任一按钮 1 秒一键三连 |
| `0.1.29-移植版` | 全站黑名单屏蔽、UP 主页拉黑、动态「热点」筛选、动态页并行加载 + DiffUtil、original/tao 双变体 |

---

## 上游原始 README（原文，未改动）

### blbl-android

一个第三方哔哩哔哩安卓 App，支持触摸、遥控，以及安卓5，适用于平板、TV、车机等设备。


### 界面预览

**推荐页**
![推荐页](./example-pic/推荐页.png)

**分类页**
![分类页](./example-pic/分类页.png)

**动态页**
![动态页](./example-pic/动态页.png)

**直播页**
![直播页](./example-pic/直播页.png)

**我的页**
![我的页](./example-pic/我的页.png)

**搜索页**
![搜索页](./example-pic/搜索页.png)

**追番**
![追番](./example-pic/追番.png)

**视频播放页**
![视频播放页](./example-pic/视频播放页.png)

### 功能概览

- 侧边栏导航：搜索 / 推荐 / 分类 / 动态 / 直播 / 我的
- 扫码登录入口
- 视频播放：Media3(ExoPlayer)，支持分辨率/编码/倍速/字幕/弹幕等设置
- 设置页：播放与弹幕偏好等

### 技术栈

- Kotlin + AndroidX + ViewBinding
- Media3(ExoPlayer)/[Ijkplayer](https://github.com/cat3399/ijkplayer)
- OkHttp
- Protobuf-lite
- Material / RecyclerView / ViewPager2

### 构建

环境要求：JDK 17，Android SDK（compileSdk 36）。

调试包：

```
./gradlew assembleDebug
```

发布包（已开启 R8 混淆 + 资源压缩）：

```
./gradlew assembleRelease
```

可选版本参数（本地或 CI）：

```
./gradlew assembleRelease -PversionName=0.1.1 -PversionCode=2
```

### 临时更新方案
**目前在代码中内置了国内环境可直接访问的直链,用于在测试阶段方便的覆盖更新,待后续稳定之后将会移除**,介意者请从release中下载action编译的安装包

### GitHub Actions

仓库包含两套手动触发的工作流：

- Android Debug：手动输入 `version_name`
- Android Release：同上，额外需要签名 Secrets

需要在仓库 Secrets 中配置：

- `RELEASE_KEYSTORE_BASE64`
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

### 感谢

- https://github.com/SocialSisterYi/bilibili-API-collect B站API收集整理
- https://github.com/xiaye13579/BBLL 优秀的页面设计和操作逻辑，本项目绝大部分页面和操作逻辑都是抄袭BBLL🥰
- https://github.com/bggRGjQaUbCoE/PiliPlus 部分关键功能参考了Piliplus的逻辑
- https://github.com/debugly/ijkplayer 感谢debugly大佬移植的ijkplayer
- 开源第三方B站客户端
- 群友们的详细测试与反馈

### 免责声明

> 不得利用本项目进行任何非法活动。 不得干扰B站的正常运营。 不得传播恶意软件或病毒。 此外，为降低法律风险

1. 🚫禁止在官方平台（b站）及官方账号区域（如b站微博评论区）宣传本项目
2. 🚫禁止在微信公众号平台宣传本项目
3. 🚫禁止利用本项目牟利，本项目无任何盈利行为，第三方盈利与本项目无关

代码都是codex写的，如有问题请联系https://openai.com/ 😤
