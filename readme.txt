1:install or deploy 全体(在之前先install或者deploy Polaris_parent)
 Polaris_assembly插件可以install和deploy但是，jekins构建会存在问题
 （插件目前只能从  http://repository.apache.org/snapshots/com/polaris/Polaris_assembly/1.0.0-SNAPSHOT/maven-metadata.xml拉取）
 需要本地Polaris_assembly包手动copy到jekins的maven仓库

2:提供两种类型的 demo（调用dubbo应用 和 无dubbo应用）
  主要参数可以参考Polaris_demo_web_nodubbo的application.properties
  mvn clean package -Dmaven.test.skip=true,可以在target目录下生成zip文件，解压缩后运行 java -jar Polaris_demo_web_nodubbo.jar

3:集群部署服务的时候需要注册中心，
  支持nacos（需要自行下载nacos的server）,
  支持eureka(内置robbin负载均衡)，负载均衡策略 根据参数robbin.loadbalancer=com.netflix.loadbalancer.AvailabilityFilteringRule(可以选择其他的rule)
  后续版本支持zookeeper注册中心(web这一块)
  每一个服务 需要在自己的配置文件中设置注册中心
  #name.registry.address=127.0.0.1:8848
  需要在自己的服务pom.xml中引入 Polaris_naming_nacos
  
4:配置中心支持多种模式（zookeeper,nacos,apollo的ConfigFile以及本地文件file），需要在自己的配置文件中设置配置中心
  所有的配置种类，需自己下载软件，比如nacos,apollo,zookeeper的建议使用ZooViewer（https://github.com/HelloKittyNII/ZooViewer）
  #zookeeper需要设置config.zk.root.path默认值【/polaris_conf】
  #config.registry.address=127.0.0.1:8848
  
  需要在自己的服务pom.xml中引入 Polaris_conf_nacos
  引入配置中心后，所有的properties文件都可以放入nacos（除了application.properties或者application.yaml 和 log4j2.xml）
  并且在application.properties中引入需要放入配置中心的配置文件
  #extension files
  #project.extension.properties=main.properties,redis.properties
  #project.extension.properties=main.yaml,redis.yaml
  可以引入全局配置,比如关于redis集群配置，数据库的整体配置等等
  #global files
  #project.global.properties=redis.properties,database.properties
  #project.global.properties=redis.yaml,database.yaml
  
  默认支持Spring注解@Value的自动更新-（nacos,zookeeper的推送更新会同步到@Value注解）
  可以用value.auto.update=false来关闭，频繁更新配置会影响性能（配置更新采用文件形式）

5:Polaris_workflow是现有的服务（工作流activity内核）
  提供dubbo接口和http接口两种方式，没有画面，具体请参考模块的配置

6:Polaris_container_gateway是现有的api网管，提供api的统一入口服务(基于netty http实现)
  具体的api代理请参考config\upstream.txt,其中static:开头的代理的存静态文件会跳过所有的filter
  另外支持静态文件配置，可以在config\static.txt增增加静态文件路径
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

  8.1 pom.xml中提供 tomcat和jetty两种启动模式,并且提供resteasy和springmvc组合的方式
      具体参考pom.xml
	  <dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_container_launcher</artifactId>  ->用于检测启动类
        </dependency>
	  <dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_container_jetty</artifactId>  ->可修改成Polaris_container_tomcat
        </dependency>
        <dependency>
            <groupId>com.polaris</groupId>
            <artifactId>Polaris_container_servlet_springmvc</artifactId> ->可修改成Polaris_container_resteasy
        </dependency>
  参考Polaris_demo_web_springmvc和Polaris_demo_web_nodubbo
  
9，支持整体调用链路的跟踪，比如traceId, moduleId, parentId, 
    日志采用slf4j的 Logger xLogger = LoggerFactory.getLogger(xxx.class);只需引入Polaris_core包
	另外如果采用线程池的方式，需要InheritableThreadLocalExecutor和InheritablePolarisThreadLocal搭配方式使用，线程池中的traceId信息也会进行传递
	采用dubbo方式 需要映入polaris_container_dubbo模块，帮你做了traceID的传递，
	http方式 采用HttpClientUtil方式，帮你做了traceID的传递
	
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
   
12，和Springboot的融合
 详细请参考Polaris_demo_springboot

   
