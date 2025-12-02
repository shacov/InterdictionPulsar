# InterdictionPulsar
Interdiction Pulsar is a Minecraft mod that adds a block that can repel or attract creatures. It is highly configurable, allowing players to customize the range, strength, target creature types, etc.

# 脉冲阻拦器模组 (Interdiction Pulsar Mod)

## 项目概述
脉冲阻拦器是一个为Minecraft 1.21.1 NeoForge设计的增强游戏体验模组。它添加了两个核心功能：可配置的脉冲阻拦器方块和实用的天使方块，为玩家的生存、建筑和防御提供强大工具。

## 核心特性

### 🚀 脉冲阻拦器 (Interdiction Pulsar)
一个高度可配置的物理场发生器，能够排斥或吸引生物和玩家。

#### 主要功能：
- **多模式操作**：
  - 推开模式：将生物推离作用原点
  - 吸引模式：将生物拉向作用原点
  - 水平/垂直模式：可独立控制水平和垂直方向的力

#### 详细配置选项：
- **范围控制**：
  - 水平范围：1-64 格
  - 垂直范围：1-64 格
  - 工作间隔：1-100 tick

- **力度调节**：
  - 推力强度：0.1 - 5.0 可调
  - 支持浮点数精确控制

- **目标筛选**：
  - 可独立选择影响敌对生物、被动生物或玩家
  - 玩家潜行时免疫移动效果

- **红石控制**：
  - 忽略红石：始终保持工作
  - 需要红石：仅在收到红石信号时工作
  - 红石停止：收到红石信号时停止工作

- **高级功能**：
  - 自定义作用原点：可设置远程作用点（支持负坐标）
  - 全方位3D移动：水平+垂直模式实现真正的3D力场

### 👼 天使方块 (Angel Block)
一个空中建筑辅助工具，让建筑更加轻松。

#### 特点：
- **空中放置**：可在视线范围内的任意位置放置
- **智能回收**：破坏后自动归还到背包（生存模式）
- **无碰撞体积**：不会阻挡视线，方便精确定位

## 使用方法

### 📱 GUI界面
右键点击脉冲阻拦器打开配置界面：
- 直观的滑块控制范围和强度
- 实时显示当前配置值
- 一键切换开关按钮
- 支持直接输入数值

### ⌨️ 命令系统
管理员可以使用丰富的命令配置：

```
/pulsar set <x> <y> <z> <参数> <值>    # 设置指定参数
/pulsar get <x> <y> <z>                 # 查看当前配置
```

可用参数：
- `mode` - 工作模式 (0=推开, 1=吸引)
- `horizontalRange` - 水平范围 (1-64)
- `verticalRange` - 垂直范围 (1-64)
- `strength` - 推力强度 (1-50)
- `horizontalMode` - 水平模式 (0=关, 1=开)
- `verticalMode` - 垂直模式 (0=关, 1=开)
- `originX/Y/Z` - 自定义坐标 (-30M ~ +30M)
- 以及所有其他配置选项

## 🎮 游戏内应用

### 防御系统
- 制作怪物屏障：推开模式+仅敌对生物
- 创建安全区域：吸引模式+仅敌对生物，将怪物聚集到陷阱
- 玩家权限管理：推开模式+仅玩家，保护私人区域

### 自动化农场
- 生物运输系统：吸引模式引导生物到特定位置
- 物品分类：利用力场分离不同生物

### 建筑与创造
- 垂直升降机：垂直模式+吸引模式
- 空中平台：天使方块辅助建筑
- 谜题设计：自定义原点创造复杂移动模式

### 创意玩法
- 全向黑洞：水平+垂直+吸引模式
- 防空炮塔：推开模式击退飞行生物
- 浮空花园：垂直模式制造悬浮效果

## 🔧 技术特色

### 性能优化
- 智能检测范围，避免不必要的计算
- 可调节的工作间隔，平衡性能与效果
- 服务端/客户端同步优化

### 用户体验
- 完整的中文本地化
- 直观的GUI设计
- 详细的错误提示
- 流畅的动画效果

### 兼容性
- 专为Minecraft 1.21.1设计
- NeoForge模组加载器
- 与其他模组良好兼容
- 支持多人游戏

## 📁 文件结构
```
InterdictionPulsar/
├── src/main/java/com/azurewrath/interdictionpulsar/
│   ├── InterdictionPulsar.java      # 核心逻辑
│   ├── InterdictionPulsarScreen.java # GUI界面
│   ├── InterdictionPulsarMenu.java   # 菜单逻辑
│   ├── InterdictionPulsarMod.java    # 主类
│   ├── PulsarConfigPacket.java       # 网络通信
│   ├── ClientEventHandler.java       # 客户端事件
│   ├── BlockandItemRegistry.java     # 注册管理
│   ├── MenuTypeRegistry.java         # 菜单注册
│   ├── AngelBlock.java              # 天使方块
│   └── PulsarCommand.java           # 命令系统
└── assets/
    └── interdictionpulsar/
        ├── lang/                    # 本地化文件
        └── textures/gui/            # 纹理文件
```

## 🚀 安装指南

### 基本要求
- Minecraft 1.21.1
- NeoForge
- Java 17+

### 安装步骤
1. 下载最新版本的模组jar文件
2. 放入Minecraft实例的`mods`文件夹
3. 启动游戏
4. 在游戏中通过合成获得物品

### 合成配方（示例）
```
脉冲阻拦器：  
[铁锭] [红石] [铁锭]  
[红石] [活塞] [红石]  
[铁锭] [红石] [铁锭]

天使方块：
[玻璃] [玻璃] [玻璃]
[玻璃] [羽毛] [玻璃]
[玻璃] [玻璃] [玻璃]
```

## 🤝 贡献指南

欢迎提交问题和改进建议！
1. Fork本仓库
2. 创建功能分支
3. 提交更改
4. 发起Pull Request

## 📝 许可证

本项目采用MIT许可证 - 详见LICENSE文件

## 🙏 致谢

- 感谢Mojang提供如此精彩的游戏
- 感谢NeoForge团队提供强大的模组平台
- 感谢所有测试者和贡献者的支持
- 特别感谢社区提供的宝贵反馈

## 🔮 未来计划

- [ ] 添加能量系统支持（RF/FE）
- [ ] 增加更多生物过滤选项
- [ ] 添加粒子效果和音效
- [ ] 支持数据包自定义配置
- [ ] 多语言支持扩展
- [ ] 集成JEI/REI显示

---

**版本：** 1.0.0  
**最后更新：** 2024年  
**Minecraft版本：** 1.21.1  
**模组加载器：** NeoForge  
**作者：** azurewrath

---

💡 *提示：脉冲阻拦器的力量不仅限于防御，想象力和创造力将决定它的真正用途！*
