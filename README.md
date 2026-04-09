# Ranch Festival

一个基于 **Minecraft 1.20.1 + Forge 47.4.0** 的模组工程。

当前项目的主要目标，是做一个带有**季节条件限制**的“节日维度/场景”原型：
- 通过 `Serene Seasons` 获取当前季节、子季节、季节日数
- 仅在满足配置条件时允许玩家进入节日维度
- 通过测试物品触发传送

---

## 技术栈

- Java 17
- Gradle
- Forge 1.20.1 (`47.4.0`)
- `net.neoforged.moddev.legacyforge`
- 主要联动模组：
  - Serene Seasons
  - Serene Seasons Fix
  - SeasonHUD
  - JEI

版本信息来自：
- `build.gradle`
- `gradle.properties`
- `gradle/libs.versions.toml`

---

## 当前代码结构

```text
src/main/java/com/y271727uy/ranch_festival/
├─ RanchFestivalMod.java           # 模组入口
├─ Config.java                     # 季节访问配置
├─ dimension/
│  └─ DimensionAccessController.java
├─ item/
│  └─ TestItem.java                # 调试/测试传送物品
└─ season/
   ├─ SeasonAccessChecker.java     # 季节访问检查与维度进入拦截
   └─ SeasonApiHelper.java         # Serene Seasons 反射调用封装
```

资源目录：

```text
src/main/resources/
├─ assets/ranch_festival/lang/     # 中英文本地化
└─ data/ranch_festival/
   ├─ dimension/                   # 维度定义
   └─ dimension_type/              # 维度类型定义
```

---

## 核心功能流程

### 1. 模组启动
`RanchFestivalMod` 负责：
- 注册物品
- 注册季节配置 `Config.SEASON_SPEC`

### 2. 季节判定
`SeasonAccessChecker.isTheDimensionAccessAllowed()` 会读取：
- `Config.requiredSeason`
- `Config.requiredSubSeason`
- `Config.requiredDayOfSeason`

并通过 `SeasonApiHelper` / `Serene Seasons` 获取当前季节信息，判断是否允许进入目标维度。

### 3. 维度进入控制
`SeasonAccessChecker` 监听维度传送事件。
当前已统一为对“节日维度”做拦截，覆盖：
- `ranch_festival:the`
- `ranch_festival:da`

### 4. 测试物品
`TestItem` 在服务端使用时会：
- 显示当前季节信息
- 判断当前是否允许进入目标维度
- 条件满足时将玩家传送到 `ranch_festival:da`

---

## 配置说明

当前配置类：`Config.java`

主要配置项：
- `enableTheDimensionSeasonLock`
- `requiredSeason`
- `requiredSubSeason`
- `requiredDayOfSeason`

默认逻辑为：
- 仅当满足指定季节 + 子季节 + 季节日数时，允许进入节日维度

> 说明：配置字段名里仍保留了 `the` 这一历史命名，但当前逻辑上已经被当作“节日维度的季节锁”来使用。

---

## 目前已整理的内容

本次已做的工程整理：

1. 修正了 `build.gradle` 中异常的 `repositories` 嵌套结构
2. 补上了 `Config.SEASON_SPEC` 的注册
3. 去掉了重复的维度事件监听，仅保留一处拦截入口
4. 收敛了维度访问控制，避免 `the` / `da` 命名分裂
5. 整理了 `TestItem`：
   - 替换弃用的 `ResourceLocation` 构造方式
   - 增加服务器空指针保护
   - 增加非空注解
   - 简化物品注册写法
6. 清理了 `Config.java` 里的模板示例配置残留
7. 将 `assets/examplemod/lang/en_us.json` 处理为合法空 JSON，避免模板垃圾内容继续干扰

---

## 仍建议后续继续处理的点

### 高优先级
1. **明确最终节日维度 ID**
   - 现在资源里同时存在 `the` 和 `da`
   - 建议最终只保留一个正式维度 ID

2. **梳理 `SeasonApiHelper`**
   - 当前大量使用 `System.out.println` / `System.err.println`
   - 建议改用模组日志器
   - 反射路径也可以进一步收敛

3. **补全 `mods.toml` 的依赖声明**
   - 当前代码对 `Serene Seasons` 有直接引用
   - 建议明确在模组元数据中声明依赖关系

### 中优先级
4. 把 `TestItem` 改名为更明确的开发物品名，例如：
   - `FestivalDebugItem`
   - `FestivalTicketItem`

5. 为节日维度增加更完整的数据内容：
   - 世界生成
   - 出生点/返回点规则
   - 禁止破坏方块逻辑

6. 增加自动化测试或最少的 GameTest

---

## 本地常用命令

在 Windows PowerShell 中可使用：

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
.\gradlew.bat runData
```

---

## 当前定位

这个工程目前更像是：

- **可运行的原型模组**，而不是完整发布版
- 重点已经有：季节条件、维度入口、测试物品、基础维度数据
- 还缺：正式命名统一、完整玩法规则、数据整理、测试覆盖

如果继续往下整理，下一步最值得做的是：
1. 统一正式维度 ID
2. 把 `TestItem` 转成正式玩法入口物品
3. 实现“节日场景禁止破坏方块（创造除外）”

