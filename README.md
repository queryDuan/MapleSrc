# MapleStorySrc

## 前言
### 这是干啥的？
这是一份MXD游戏服务端源码。


2011年接触了MXD私服，也进行过源码的编写，却没有细致的分析过每一步的实现原理。所以这个rep是`从这源码角度来分析构建游戏服务需要考虑到的地方`。我会坚持每日一更 (哈哈但愿如此。

#### 启动
启动服务会启动
- 世界服务：`net.sf.odinms.net.world.WorldServer`
- 频道服务：`net.sf.odinms.net.channel.ChannelServer `
- 登录服务：`net.sf.odinms.net.login.LoginServer`

##### WorldServer 
- 注册`WorldRegistry`到RMI
- 获取配置，创建DB链接，获取玩家总数量。


引用到的包：`java.rmi`远程方法调用
##### ChannelServer
- 更新玩家数据(异常在线、商店等业务数据)
- 通过RMI拿到`WorldRegistry`
- <b>创建频道实例,新启动线程</b>：
 	- 获取`MapleMapFactory`地图数据 
 	- 新建`ChannelWorldInterface`与`WorldServer`进行通信
 	- 在`worldRegistry`中注册Channel
 	- 从配置中获取基本参数(经验倍数、副本索引、服务器信息等等)
 	- 引用`MINA`注册`SocketAcceptor`，用`MapleCodecFactory`来进行filter的decode/encode
 	- Acceptor绑定端口，创建`MapleServerHandler`来监听事件。
 	- 延迟处理业务层逻辑：地图刷新、封号名单刷新、释放服务器资源；活动脚本初始化`EventScriptManager`。
- 通过`JMX`注册命令处理器`CommandProcessor`

##### LoginServer



## 最后
  这份源码是冒险岛079国际版本源码，为网上下载，仅供学习，`不负责版权问题`。
