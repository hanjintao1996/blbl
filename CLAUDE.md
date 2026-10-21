# CLAUDE.md

本文件是本仓库的协作约定。改动代码前请先读完，尤其是「产物与构建」一节。

## 产物与构建（重要）

- **不要主动编译打包**：日常改代码只做源码修改，不执行 `assembleDebug` / `assembleRelease`，也不产出、不交付 APK。
- **只有用户明确说「编译」「打包」「出包」「给我 APK / 产物」时，才执行构建并交付产物。**
- 干活过程中需要自检时，最多跑编译级检查（`./gradlew :app:compileOriginalDebugKotlin`）或单元测试，不要跑 `assemble*`。
- 出包时：产物放 `dist/`，命名 `blbl-{flavor}-v{versionName}.apk`，并在回复里说明包名、版本号、大小。
- 版本号通过 Gradle 属性注入（CI 也是这么用的），不要硬编码进 `app/build.gradle.kts`：
  `./gradlew assembleDebug -PversionCode=30 -PversionName="0.1.30-改动简述"`

## 构建环境（本机）

- JDK 17：`/Users/hanjintao/Library/Java/JavaVirtualMachines/jdk-17.0.20.1+1/Contents/Home`
  （本机自带的 JDK 24 超出 Gradle 8.7 支持范围，必须用 17）
- Android SDK：`~/Library/Android/sdk`，含 `platforms;android-36`、`build-tools;36.0.0`、`build-tools;34.0.0`
- 运行 Gradle 前需要：
  `export JAVA_HOME=/Users/hanjintao/Library/Java/JavaVirtualMachines/jdk-17.0.20.1+1/Contents/Home && export ANDROID_HOME=$HOME/Library/Android/sdk`
- 安装 Android SDK 组件必须在沙箱外执行：`sdkmanager` 在沙箱内删临时文件会 `Operation not permitted` 而失败。

## 产品变体

- `original`：applicationId `blbl.cat3399`
- `tao`：applicationId `blbl.cat3399.tao`，应用名 Blbltao，可与原版并行安装

## 已知状态

- 单测 `PlayerAutoNextTitleTest#truncatesTitlesToTwelveCharacters` 属于上游遗留失败
  （实现 `AUTO_NEXT_TITLE_MAX_CHARS = 18`，测试期望 12），与本仓库改动无关。
- release 构建强制引用 `keystore/release.keystore` 及 `RELEASE_*` 环境变量，仓库内没有该密钥，
  未配置前只能出 debug 包。

## Git

- 提交信息统一用中文，说清「为什么改」而不只列文件。
- 不主动 `git commit` / 发版，用户明确要求时再提交。
