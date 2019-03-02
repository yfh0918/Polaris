1:先install Polaris_parent

2:在install 全体

3:提供两种类型的 demo（调用dubbo应用 和 无dubbo应用）

4:集群部署服务的时候需要注册中心，目前仅支持nacos（需要自行下载nacos的server）
  每一个服务 需要在自己的配置文件中设置注册中心
  #name.registry.address=127.0.0.1:8848
  需要在自己的服务pom.xml中引入 Polaris_naming
  

5:配置中心支持两种模式（zookeeper和nacos），需要在自己的配置文件中设置配置中心
  #config.registry.address=127.0.0.1:8848
  需要在自己的服务pom.xml中引入 Polaris_conf_nacos
  引入配置中心后，所有的properties文件都可以放入nacos（除了application.properties 和 log4j.properties）
  并且在application.properties中引入需要放入配置中心的配置文件
  #extension files
  #project.extension.properties=main.properties,redis.properties

6:Polaris_timer和Polaris_workflow是现有的两个服务（定时器 和 工作流activity内核）

7:Polaris_log是现有的日志采集服务，需要自行配置mongodb以及自行开发界面

8:Polaris_gateway是现有的api网管，提供api的统一入口服务(基于netty http实现)

9:支持Sentinel（流量监控类），需要在自己的配置文件中设置如下
  #sentinel
  #csp.sentinel.dashboard.server=127.0.0.1:8858
  #csp.sentinel.heartbeat.interval.ms=5000
  #csp.sentinel.api.port=9008
  需要在自己的服务pom.xml中引入 Polaris_sentinel

...