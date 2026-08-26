# To-Do List — Minecraft 26.2 Fabric Mod

作者：StaXV

一个纯客户端 Fabric Mod：在游戏里随时按 **M** 键打开待办清单，记录要做的事；也可以把一个小型待办清单固定到游戏界面任意位置。

## 功能

- 默认 **M** 键打开 / 关闭待办清单（可在 选项 → 按键设置 → 待办 中修改）
- 新建、编辑、删除待办；标题 + 多行内容
- 勾选完成标记，未完成事项自动排在最前面
- 清空全部（两次点击确认，防误触）
- 固定待办：在游戏界面上显示一个小型待办清单（成就弹窗样式），位置可手动拖到任意地方
  - 设置 → 位置设置 会打开一个以 实时游戏画面 为背景的界面，迷你待办悬浮在真实位置上，直接拖动即可指定位置
  - 显示条数、宽度均可调整，不会影响正常游玩
- 数据自动保存为 JSON：`config/memo.json`（游戏目录下），关闭游戏不丢失
- 界面全部使用原版 GUI（按钮、复选框、输入框、滚动条均为原版纹理），支持滚动与悬停反馈
- 中英文界面（跟随游戏语言）
- 可选接入 **Mod Menu**：在 Mod Menu 的模组列表中找到「To-Do List」→ 配置，即可打开设置页

## 环境要求

- Minecraft **26.2**（Java Edition）
- Fabric Loader **0.19.3+**
- Fabric API **0.158.0+**（`0.158.0+26.2`）
- Java 25
- （可选）Mod Menu **20.0.x**，用于从模组管理界面打开设置

## 安装

1. 用 [Fabric 安装器](https://fabricmc.net/use/) 给 26.2 安装 Fabric（最新稳定 Loader 0.19.3）
2. 把 `build/libs/To-Do list-1.14.0.jar` 放进 `.minecraft/mods/`
3. 把 Fabric API（`fabric-api-0.158.0+26.2.jar`）也放进 `mods/`
4. （可选）把 Mod Menu 20.0.x 放进 `mods/`，可从模组列表进入设置
5. 启动游戏，进存档后按 **M**

## 使用

- **M**：打开 / 关闭主界面
- 主界面底部 **设置**：启用固定待办、拖动选择位置、调整大小（也可从 Mod Menu 打开）
- 卡片左侧方框：标记完成 / 取消完成
- 卡片中间区域：点击进入编辑
- 卡片右侧 **编辑 / 删除**：操作对应待办（删除需点两次确认）
- 底部 **＋ 新建待办 / 清空全部**：新建或清空
- 编辑页：填写标题与多行内容，**保存 / 取消**，或按 ESC 返回

数据文件位置：`<游戏目录>/config/memo.json`（例如 `.minecraft/config/memo.json`），可直接备份或手动编辑。
设置文件位置：`<游戏目录>/config/memo_settings.json`。

## 开发

```bash
# 需要 Java 25（如 D:\PCL2\java25）
set JAVA_HOME=D:\PCL2\java25
gradlew.bat build
```

产物在 `build/libs/To-Do list-1.14.0.jar`。项目结构：

```
src/main/java     入口与通用代码
src/client/java   客户端界面、数据存储
src/main/resources  fabric.mod.json 与语言文件
src/client/resources 图标与 GUI 纹理
tools/            资源生成脚本（Python + Pillow）
```
