
# 简介

> 这是一个功能丰富的菜单插件，专为提升服主体验而设计。
  菜单支持自定义标题、大小和权限，确保只有授权用户才能访问。
  点击按钮时，可根据用户行为和配置执行多种操作，如发放奖励、发送消息或打开子菜单。
  菜单还支持动态变量PAPI和多种点击类型，以适应不同场景。
  此外，菜单还支持多版本兼容，已经再1.12.2-1.20.4做了详细测试, 可在不同版本的服务器之间无缝切换。
  同时，菜单支持混合端服务端，比如carserver,sponge(计划)，都能提供一致的体验。
  此外，菜单还支持解析Hex颜色代码，让您可以自定义文本的绚丽颜色。
  最后，菜单还支持JavaScript脚本，让您可以根据需要自定义更复杂的逻辑和行为。

## 目录

- [简单菜单]
  - [简介](#简介)
  - [菜单定义](#菜单定义)
    - [标题](#title)
    - [大小](#size)
    - [权限](#permission)
    - [打开声音](#opensound)
    - [关闭声音](#closesound)
    - [打开指令](#opencommands)
    - [打开物品](#openitem)
    - [按钮](#buttons)
  - [按钮定义](#按钮定义)
    - [显示位置](#slots)
    - [显示物品](#icon)
    - [查看权限](#view_requirement)
    - [优先度](#priority)
    - [刷新延迟](#refreshdelay)
    - [点击动作](#action)
  - [需求类型](#需求类型)
    - [权限需求](#haspermission)
    - [金钱需求](#hasmoney)
    - [点券需求](#haspoints)
    - [物品需求](#hasitem)
    - [ny货币需求](#hasnyeconomy)
    - [字符串对比](#stringequals)
    - [字符串包含](#stringcontains)
    - [数字对比](#numberequals)
    - [背包空位](#hasemptyslot)
    - [javaScript](#javascript)
  - [动作类型](#动作类型)
    - [给予物品](#giveitem)
    - [移除物品](#removeitem)
    - [给予金钱](#givemoney)
    - [扣除金钱](#takemoney)
    - [控制台指令](#consolecommands)
    - [玩家指令](#playercommands)
    - [给予点券](#givepoints)
    - [扣除点券](#takepoints)
    - [给予ny货币](#givenyeconomy)
    - [扣除ny货币](#takenyeconomy)
    - [发送消息](#message)
    - [广播消息](#broadcast)
    - [播放声音](#playsound)
    - [发送Title](#sendtitle)
    - [发送json文本](#sendjson)
    - [广播json文本](#broadcastjson)
    - [打开菜单](#openmenu)
    - [关闭菜单](#closemenu)
    - [javaScript](#script)
    - [传送到指定服务器](#server)
    - [输入捕获](#catchexecutor)
  - [点击类型](#点击类型)
  - [变量](#变量)

## 菜单定义

### title

- **描述**：菜单的标题。
- **类型**：字符串
- **示例**：

  ```yaml
  title: 菜单标题
  ```

### size

- **描述**：菜单的大小
- **类型**：整数(必须是9的倍数,不能大于54)
- **示例**：

  ```yaml
  size: 27
  ```

### permission

- **描述**：查看菜单所需的权限
- **类型**：字符串。
- **示例**：

  ```yaml
  permission: example.menu.view
  ```

### openSound

- **描述**：打开菜单时播放的声音效果。
- **类型**: 键值对
- **示例**：

  ```yaml
  openSound:
    type: 'minecraft:block.chest.open' # 声音的全限定名称
    volume: 1 # 音量
    pitch: 1 # 音调
   ```

### closeSound

- **描述**：关闭菜单时播放的声音效果。
- **类型**: 键值对
- **示例**：

  ```yaml
  closeSound:
    type: 'minecraft:block.chest.close' # 声音的全限定名称
    volume: 1 # 音量
    pitch: 1 # 音调
  ```

### openCommands

- **描述**：打开菜单的指令
- **类型**: 字符串数组
- **示例**：

  ```yaml
  openCommands:
  - "cmd"
  ```

### openItem

- **描述**：打开菜单的物品
- **类型**: 键值对,详情请看acion的物品需求介绍
- **示例**：

  ```yaml
  openItem:
    type: "WATCH"
    name: "&6示例菜单"
    lore:
      - "&7右键点击打开"
  ```

### buttons

- **描述**：菜单中显示的按钮。
- **类型**: 键值对
- **示例**：

  ```yaml
  BT1:
    # 按钮位置 值为数组
    SLOTS: [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 27, 28, 29, 30, 31, 32, 33, 34, 35 ]
    #按钮的图标 值为键值对 
    ICON:
      type: "DIAMOND_BLOCK"
      name: "&f<r:0.5:loop>欢迎使用混合服务器专用菜单"
      lore:
        - "&f&l&k<r:0.6:loop>================================"
    # 查看按钮的需求 值为数组
    VIEW_REQUIREMENTS:
      - 'hasPermission: example.permission'
    # 按钮优先级 值为整数
    PRIORITY: 10
    # 按钮刷新频率 值为整数 这里的值为游戏中的tick
    REFRESHDELAY: 20
    # 按钮按下的动作 值为属性组
    ACTION:
      #动作类型 可选 default,left,right,middle,shift_left,shift_right
      default:
        # 执行按钮action的需求
        requirements:
        - "hasPermission: example.permission" #表示需要有example.permission权限
        # 满足需求执行的action
        actions:
          - "closeMenu: true" # 表示关闭菜单
          # 表示发送title消息给玩家
          - "title: title:<r:0.9>Hello World, subtitle:<r:1.1>Welcome to the world, fadein:20, stay:80, fadeout:40"
        # 不满足需求执行的action
        denyActions:
          - 'closeMenu: true'
          - "message: 你没有权限" # 表示发送消息给玩家
  ```

## 按钮定义

### SLOTS

- **描述**：按钮位置。
- **类型**: 数组
- **示例**:

  ```yaml
  SLOTS: [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 27, 28, 29, 30, 31, 32, 33, 34, 35 ] #当按钮只在一个位置显示时,可以写为[0],这种形式
  ```

### ICON

- **描述**：按钮显示的图标
- **类型**: 键值对
- **示例**:

  ```yaml
  ICON:
    type: DIAMOND #图标材质,你可以写Bukkit枚举材质名称,也可以写minecraft:diamond这种全限定名称,也支持直接写模组的材质比如pixlemon:pixlemon_sprite
    #damage: 0 #子id(如果存在可以写上)
    name: '&b&lDiamond' # 按钮显示的名称
    lore: # 按钮显示的描述
      - '&7This is a diamond.'
    amount: 1 #图标物品的数量
    # nbt数据,你可以使用指令/hsm save 物品名称 来保存你手中的物品到item文件夹,来查看物品的具体nbt
    nbt: '{display:{Name:"{\"text\":\"Diamond\"}"}}'
  ```

### VIEW_REQUIREMENT

- **描述**：查看按钮所需的需求,支持需求类型里面的所有类型。
- **类型**：数组
- **示例**:

  ```yaml
  VIEW_REQUIREMENTS:
    - 'hasPermission: example.permission'
  ```

### PRIORITY

- **描述**：按钮优先级,数字越大优先级越高
- **类型**: 整数
- **示例**:

  ```yaml
  PRIORITY: 100
  ```

### REFRESHDELAY

- **描述**：按钮刷新间隔,单位tick(1tick=0.05秒)
- **类型**: 整数
- **示例**:

  ```yaml
  REFRESHDELAY: 20 # 每20tick刷新一次,也就是1秒
  ```

### ACTION

- **描述**：按钮点击后的动作
- **类型**: 键值对
- **示例**:

  ```yaml
  ACTION:
      #动作类型 可选 default,left,right,middle,shift_left,shift_right
      default:
        # 执行按钮action的需求
        requirements:
        - "hasPermission: example.permission" #表示需要有example.permission权限
        # 满足需求执行的action
        actions:
          - "closeMenu: true" # 表示关闭菜单
          # 表示发送title消息给玩家
          - "title: title:<r:0.9>Hello World, subtitle:<r:1.1>Welcome to the world, fadein:20, stay:80, fadeout:40"
        # 不满足需求执行的action
        denyActions:
          - 'closeMenu: true'
          - "message: 你没有权限" # 表示发送消息给玩家
  ```

## 需求类型

### hasPermission

- **描述**：检查玩家是否拥有指定权限
- **类型**：字符串
- **格式**: `hasPermission: <权限>`
- **示例**:

  ```yaml
  requirements:
    - "hasPermission: example.permission"
  ```

### hasMoney

- **描述**：检查玩家是否拥有指定金额的金钱
- **类型**：字符串
- **格式**: `hasMoney: <金额>`
- **示例**:

  ```yaml
  requirements:
    - "hasMoney: 100"
  ```

### hasPoints

- **描述**：检查玩家是否拥有指定数量的点数
- **类型**：字符串
- **格式**: `hasPoints: <点数>`
- **示例**:

  ```yaml
  requirements:
    - "hasPoints: 100"
  ```

### hasItem

- **描述**：检查玩家是否拥有指定数量的指定物品
- **类型**：字符串
- **格式**: `hasItem: type:<物品类型, damage:<子id>, amount:<数量>, name:<自定义名称>, lore:<[描述,支持多行使用|分割]>, nbt:<nbtjson>`
- **示例**:

  ```yaml
  requirements:
    - 'hasItem: type:minecraft:wool, damage:1, amount:10, name:&a&l钻石, lore:[&7右键点击打开|&7点击给予玩家&a&l钻石], nbt:{tag:{ench:[{lvl:1s,id:16s},{lvl:1s,id:20s}]}}'
  ```

### hasNyEconomy

- **描述**：检查玩家是否拥有指定数量的nyeconomy货币
- **类型**：字符串
- **格式**: `hasNyEconomy: <货币名称>, <金额>`
- **示例**:

  ```yaml
  requirements:
    - "hasNyEconomy: 积分, 100"
  ```

### stringEquals

- **描述**：检查字符串是否相等
- **类型**：字符串
- **格式**: `stringEquals: <是否忽略大小写,true忽略false不忽略>, <被对比的字符串,开头写!表示取反>, <要对比的字符串>`
- **示例**

  ```yaml
  requirements:
    - "stringEquals: true, !hello, world"
  ```

### stringContains

- **描述**：检查字符串是否包含
- **类型**：字符串
- **格式**: `stringContains: <被包含的字符串,开头写!表示取反>, <要对比的字符串>`
- **示例**

  ```yaml
  requirements:
    - 'stringContains: 花朵, 我是祖国的花朵'
  ```

### numberEquals

- **描述**：对数字进行判断
- **类型**：字符串
- **格式**: `numberEquals: <对比类型>, <被对比的数字>, <要对比的数字>'
- **参数说明**：`第一个参数表示对比类型,可选: equal,greater,less,greaterOrEqual,lessOrEqual,notEqual(相等,大于,小于,大于等于,小于等于,不等于),第二个参数表示要被比较的数字,第三个参数表示要比较的数字`
- **示例**

  ```yaml
  requirements:
    - 'numberEquals: greater, 10, 20'
  ```

### hasEmptySlot

- **描述**：检查玩家是否拥有空格子
- **类型**：字符串
- **格式**: `hasEmptySlot: <空格子数量>`
- **示例**

  ```yaml
  requirements:
    - 'hasEmptySlot: 1'
  ```

### javaScript

- **描述**：执行JavaScript代码
- **类型**：字符串
- **格式**: `javaScript: <JavaScript代码>`
- **参数说明**：`JavaScript代码最后一句必须返回一个boolean,否则永远都是false`
- **示例**

  ```yaml
  requirements:
    - 'javaScript: player.hasPermission("example.permission");'
  ```

## 动作类型

### giveItem

- **描述**：给玩家物品
- **类型**：字符串
- **格式**: `giveItem: type:<物品类型>, damage:<子id>, amount:<数量>, name:<自定义名称>, lore:<[描述,支持多行使用|分割]>, nbt:<nbtjson>`
- **示例**

  ```yaml
  actions:
    - 'giveItem: type:minecraft:wool, damage:1, amount:10, name:&a&l钻石, lore:[&7右键点击打开|&7点击给予玩家&a&l钻石], nbt:{tag:{ench:[{lvl:1s,id:16s},{lvl:1s,id:20s}]}}'
  ```

### removeItem

- **描述**：移除玩家物品
- **类型**：字符串
- **格式**: `removeItem: type:<物品类型>, damage:<子id>, amount:<数量>, name:<自定义名称>, lore:<[描述,支持多行使用|分割]>, nbt:<nbtjson>`
- **示例**

  ```yaml
  actions:
    - 'removeItem: type:minecraft:wool, damage:1, amount:10, name:&a&l钻石, lore:[&7右键点击打开|&7点击给予玩家&a&l钻石], nbt:{tag:{ench:[{lvl:1s,id:16s},{lvl:1s,id:20s}]}}'
  ```

### giveMoney

- **描述**：给玩家钱
- **类型**：字符串
- **格式**: `giveMoney: <金额>`
- **示例**

  ```yaml
  actions:
    - 'giveMoney: 10000'
  ```

### takeMoney

- **描述**：移除玩家钱
- **类型**：字符串
- **格式**: `takeMoney: <金额>`
- **示例**

  ```yaml
  actions:
    - 'takeMoney: 10'
  ```

### consoleCommands

- **描述**：执行控制台命令
- **类型**：字符串
- **格式**: `consoleCommands: <[命令,支持多个指令,使用|分割]>`
- **示例**

  ```yaml
  actions:
    - "consoleCommands: [say 你好世界|say 第二条指令]"
  ```

### playerCommands

- **描述**：执行玩家指令
- **类型**：字符串
- **格式**: `playerCommands: <[指令,支持多个指令,使用|分割]>`
- **示例**

  ```yaml
  actions:
    - "playerCommands: [say 你好世界|say 第二条指令]"
  ```

### givePoints

- **描述**：给予玩家点券
- **类型**：字符串
- **格式**: `givePoints: <数量>`
- **示例**

  ```yaml
  actions:
    - 'givePoints: 100'
  ```

### takePoints

- **描述**：移除玩家点券
- **类型**：字符串
- **格式**: `takePoints: <数量>`
- **示例**

  ```yaml
  actions:
    - 'takePoints: 100'
  ```

### giveNyEconomy

- **描述**：给予玩家nyeconomy货币
- **类型**：字符串
- **格式**: `giveNyEconomy: <货币名称>, <金额>`
- **示例**

  ```yaml
  actions:
    - 'giveNyEconomy: 钻石, 100'
  ```

### takeNyEconomy

- **描述**：移除玩家nyeconomy货币
- **类型**：字符串
- **格式**: `takeNyEconomy: <货币名称>, <金额>`
- **示例**

  ```yaml
  actions:
    - 'takeNyEconomy: 钻石, 100'
  ```

### message

- **描述**： 给玩家发送一条消息
- **类型**：字符串
- **格式**: `message: <消息>`
- **示例**

  ```yaml
  actions:
    - 'message: 你好世界'
  ```

### broadcast

- **描述**： 给所有玩家发送一条消息
- **类型**：字符串
- **格式**: `broadcast: <消息>`
- **示例**

  ```yaml
  actions:
    - 'broadcast: 你好世界'
  ```

### playSound

- **描述**： 播放声音
- **类型**：字符串
- **格式**: `playSound: type:<声音全限定名称>, volume:<音量>, pitch:<音调>`
- **示例**

  ```yaml
  actions:
    - 'playSound: type:minecraft:block.note_block.pling, volume:1, pitch:1'
  ```

### sendTitle

- **描述**： 发送大标题
- **类型**：字符串
- **格式**: `sendTitle: title:<标题>, subtitle:<副标题>, fadeIn:<淡入时间>, stay:<停留时间>, fadeOut:<淡出时间>`
- **参数说明**：这里可以title或subtitle留空，留空则不显示
- **示例**

  ```yaml
  actions:
    - 'sendTitle: title:<r:0.9>Hello World, subtitle:<r:1.1>Welcome to the world, fadein:20, stay:80, fadeout:40'
  ```

### sendJson

- **描述**： 发送JSON富文本消息
- **类型**：字符串
- **格式**: `sendJson: <JSON富文本>`
- **示例**

  ```yaml
  actions:
    - 'sendJson: {"text":"<r:0.6>json可以五颜六色","clickEvent":{"action":"RUN_COMMAND","value":"/say HybridServerAPI"},"hoverEvent":{"action":"SHOW_TEXT","value":["<r:0.3>你好minecraft"]}}'
  ```

### broadcastJson

- **描述**： 给所有玩家发送JSON富文本消息
- **类型**：字符串
- **格式**: `broadcastJson: <JSON富文本>`
- **示例**

  ```yaml
  actions:
    - 'broadcastJson: {"text":"<r:0.6>json可以五颜六色","clickEvent":{"action":"RUN_COMMAND","value":"/say HybridServerAPI"},"hoverEvent":{"action":"SHOW_TEXT","value":["<r:0.3>你好minecraft","再见"]}}'

### openMenu

- **描述**： 打开子菜单
- **类型**：字符串
- **格式**: `openMenu: <子菜单名称>, [菜单传递的参数]`
- **示例**

  ```yaml
  actions:
    - 'openMenu: menu1'
    - 'openMenu: menu1, 你好'
  ```

### closeMenu

- **描述**： 关闭菜单
- **类型**：字符串
- **格式**: `closeMenu <true/false>`
- **示例**

  ```yaml
  actions:
    - 'closeMenu: true'
  ```

### Script

- **描述**： 执行脚本
- **类型**：字符串
- **格式**: `javaScript: <脚本>`
- **示例**

  ```yaml
  actions:
    - 'javaScript: Bukkit.broadcastMessage("§4欢迎来到地球在线服务器!!!");'
  ```

### server

- **描述**： 传送到指定服务器需要bungeed支持
- **类型**：字符串
- **格式**: `server: <服务器名称>`
- **示例**

  ```yaml
  actions:
    - 'server: 地球在线服务器'
  ```

### catchExecutor

- **描述**： 捕获玩家输入内容,然后执行提供的action
- **类型**：字符串
- **格式**: `catchExecutor: text:<给玩家的提示>:, actions:<动作组,请看示例>`
- **示例**

  ```yaml
  actions:
    - "catchExecutor: text:&6请输入文字:, actions:{consoleCommands: [say %catchArg%]$playerCommands: [say %player_name% &aHello World]}"
  ```

## 点击类型

- default (任意点击都可以)
- left（左键点击）
- right（右键点击）
- middle（中键点击）
- shift_left（shift+左键点击）
- shift_right（shift+右键点击）

## 延迟执行与几率执行

每个动作都可以延迟执行或几率执行

具体看示例

  ```yaml
  actions:
    - 'broadcast: <g:#F20D0D:#C129B8>欢迎来到地球在线服务器!!!, <chance=0.5>' # 50%几率执行
    - 'takeMoney: 100, <delay=60>' # 延迟60tick执行
  ```

## 变量

- **%arg%** `表示菜单传递的参数`
- **%catchArg%** `表示捕获的玩家输入的内容`

## 颜色使用

- **&6** `表示传统minecraft颜色,支持0-9,a-f, k l o m n等`
- **#F20D0D** `表示16进制颜色,只有1.16+版本才能使用`
- **<g:#F20D0D:#C129B8>** `表示渐变色,只有1.16+版本才能使用`
- **<r:0.3>** `表示彩虹色,只有1.16+版本才能使用`