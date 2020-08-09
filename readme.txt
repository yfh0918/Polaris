0:基于代码安全考量，最新分支将在国内的https://gitee.com/yu_fenghua/Polaris 维护
 
1:install 全体
 
2:提供两种类型的 demo（调用dubbo应用 和 无dubbo应用）
  主要参数可以参考Polaris_demo_web_resteasy的application.properties
  mvn clean package -Dmaven.test.skip=true,可以在target目录下生成zip文件，解压缩后运行 java -jar Polaris_demo_web_resteasy.jar

3:集群部署服务的时候需要注册中心，
  支持nacos（需要自行下载nacos的server）,
  支持eureka(内置robbin负载均衡)，负载均衡策略 根据参数robbin.loadbalancer=com.netflix.loadbalancer.AvailabilityFilteringRule(可以选择其他的rule)
  支持zookeeper注册中心，server版本3.5.5及以上
  每一个服务 需要在自己的配置文件中设置注册中心
  #name.registry.address=127.0.0.1:8848
  需要在自己的服务pom.xml中引入 Polaris_naming_xxx
  
4:配置中心支持多种模式（zookeeper,nacos,apollo的ConfigFile以及本地文件file），需要在自己的配置文件中设置配置中心
  所有的配置服务端软件需自己下载软件，比如nacos,apollo,   
  如果是zookeeper的建议使用ZooViewer（https://github.com/HelloKittyNII/ZooViewer）也可以自行开发zookeeper客户端软件
  #zookeeper需要设置config.zk.root.path默认值【/polaris_conf】
  #config.registry.address=127.0.0.1:8848
  
  需要在自己的服务pom.xml中引入 Polaris_conf_xxx
  引入配置中心后，所有的properties文件都可以放入配置中心（除了application.properties或者application.yaml 或者application.xml 和 log4j2.xml）
  在application.properties中引入需要放入配置中心的配置文件
  #extension files(以下配置文件配置方式  或者也可以采用PolarisConfigurationExt注解或者PolarisConfigurationProperties注解注入)
  #project.extension.properties=main.properties,redis.properties
  #project.extension.properties=main.yaml,redis.yaml
  #project.extension.properties=main.xml,redis.xml
  #global files(以下配置文件配置方式  或者也可以采用PolarisConfigurationGbl注解或者PolarisConfigurationProperties注解注入)
  #project.global.properties=redis.properties,database.properties
  #project.global.properties=redis.yaml,database.yaml
  默认支持Spring注解@Value的自动更新-（nacos,zookeeper等配置中心的推送更新会同步到@Value注解）
  可以用value.auto.update=false来关闭

5:Polaris_extension_workflow是现有的服务（工作流activity内核）
 具体请参考模块的配置,详细参考Polaris_demo目录下的workflow

6:Polaris_container_gateway是现有的api网管，提供api的统一入口服务(基于netty 实现),目前支持http1.0，1.1和websocket协议
  支持之定义过滤器扩展，详细请参考Polaris_demo_gateway

7:支持Sentinel（流量监控类），需要在自己的配置文件中设置如下
  #sentinel
  #csp.sentinel.dashboard.server=127.0.0.1:8858
  #csp.sentinel.heartbeat.interval.ms=5000
  #csp.sentinel.api.port=9008
  需要在自己的服务pom.xml中引入 Polaris_extension_sentinel
  该接口主要用于提供api的servlet

8,如何启动，打开eclipse后启动xxxApplication.java文件（注解@PolarisApplication） 
 总要，为了加快启动速度，请把Polaris_launcher放置到依赖包的最前面

  8.1 pom.xml中提供 tomcat和jetty两种启动模式,并且提供resteasy,springmvc,自定义customize三种组合的方式
      customize模式，支持@WebServlet注解和手动注入两种，详细请参考Polaris_demo_web_customize模块
      具体参考pom.xml
	  <dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_container_launcher</artifactId>  ->用于检测启动类
        </dependency>
	  <dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_container_jetty</artifactId>  ->可修改成Polaris_container_tomcat或者Polaris_container_undertow
        </dependency>
        <dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_container_servlet_springmvc</artifactId> ->可修改成Polaris_container_resteasy或Polaris_container_customize
        </dependency>
	<dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_extension_mvc_config</artifactId> ->帮助你完成了mvc的配置无需自己写代码，如果有扩展请自行写config
        </dependency>
  详情参考Polaris_demo_web_springmvc
  
9，
   9.1 支持整体调用链路的跟踪，需要配置开启logging.trace.enable=true
       遵循opening trace协议：traceId, spanId,parentId,moduleId
       日志采用slf4j的 Logger xLogger = LoggerFactory.getLogger(xxx.class);只需引入Polaris_core包
	另外如果采用线程池的方式，
	需要使用ThreadPoolBuilder.newBuilder().inheritable(true).xxx().xx().build()，或者使用ThreadPoolBuilder.newScheduledBuilder().inheritable(true).xxx().xx().build(),并且这样做的好处是你不用手动释放连接池由容器来释放
	线程池中的traceId信息也会进行传递
	采用dubbo方式 需要映入polaris_container_dubbo模块，帮你做了traceID的传递，
	http方式 采用HttpClientUtil方式，帮你做了traceID的传递，如果对日志发送到第三方监控装置，
	可以自行改造Polaris_logger的ExtendedLogger的getMessage方法
   9.2 支持log4jCallBack回调，需要继承com.polaris.core.log.Log4jCallBack，
       采用SPI扩展，需要注意的是回调接口中不能用logger.xxx会造成死循环，具体参照Polaris_demo_web_springmvc工程DemoLog4jCallBack类
       日志的落库或者和其他系统的对接可在回调函数中完成 
	
10,缓存模块Polaris_extension_cache,
   CacheFactory.getCache(cachename);获取缓存，默认采用EHCache, 
   根据缓存配置参数可以动态切换 RedisSingle和RedisCluster
   参数配置如下(xxx为缓存的名称cachename)
   cache.xxx.type=ehcache
   cache.xxx.type=redis
   cache.xxx.type=rediscluster
   其他参数
   可以采用注解com.polaris.cache.Cacheable
   目前支持的方法参考com.polaris.cache.Cache接口
   缓存 序列化可以自己配置，默认KryoSerializer
   
11，数据库模块Polaris_extension_db
  默认采用mybatis+hikari,支持多数据源
  如果是多数据源请采用
  jdbc.xx.url=xxx 或者 spring.datasource.xx.url=sss 详细请参考Polaris_demo_web_resteasy
  多数据源如果采用注解切换的请自行添加aop,详情请参考DataBaseAop和serviceImpl中各方法的@DataSource注解
   
12，和Springboot的融合
 详细请参考Polaris_demo_springboot（自带websocket不用再引入）

13, webflux
 引入Polaris_container_webflux模块，详细请参考Polaris_demo_webflux

14, 分布式事务，建议采用阿里巴巴开源的seata

15, 新增模块ManagedComponent抽象类，任何继承ManagedComponent类的资源释放都可以在close方法中完成，可以参考Polaris_demo_web_springmvc的DemoLifCycle
