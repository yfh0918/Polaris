package com.polaris.demo.rest.controller;

import com.polaris.extension.feign.FeignClient;

public class FeignConsumer {
    public static void main(String[] args) throws Exception {

        User param = new User();
        param.setUsername("scott");

        //走注册中心
        //String url = NamingClient.getRealIpUrl("http://Polaris_demo_web_springmvc/demospringmvc");
        
        //固定url
        String url = "http://localhost:9045/demospringmvc";

        //RemoteService service = FeignClient.target(RemoteService.class, url);
//        RemoteService service = Feign.builder()
//                .encoder(new JacksonEncoder())
//                .decoder(new JacksonDecoder())
//                .target(RemoteService.class, url);        
        for (int i = 1; i <= 10; i++) {
            User result = FeignClient.target(RemoteService.class, url).getOwner(param);
            System.out.println(result.getId() + "," + result.getUsername());
        }
        
        //以下时 feign + robbin
//        sample-client.properties
//        sample-client.ribbon.MaxAutoRetries=1
//
//        sample-client.ribbon.MaxAutoRetriesNextServer=1
//
//        sample-client.ribbon.OkToRetryOnAllOperations=true
//        sample-client.ribbon.ServerListRefreshInterval=2000
//
//        sample-client.ribbon.ConnectTimeout=3000
//
//        sample-client.ribbon.ReadTimeout=3000
//
//        sample-client.ribbon.listOfServers=127.0.0.1:8080,127.0.0.1:8085
//
//        sample-client.ribbon.EnablePrimeConnections=false
//        ConfigurationManager.loadPropertiesFromResources("sample-client.properties");
//        RibbonClient client = RibbonClient.builder().lbClientFactory(new LBClientFactory() {            
//            @Override
//            public LBClient create(String clientName) {
//                IClientConfig config = ClientFactory.getNamedConfig(clientName);
//                ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
//                ZoneAwareLoadBalancer zb = (ZoneAwareLoadBalancer) lb;
//                zb.setRule(new RandomRule());                
//                return LBClient.create(lb, config);
//            }
//        }).build();
//
//        RemoteService service2 = Feign.builder()
//                .client(RibbonClient.create())
//                .encoder(new JacksonEncoder())
//                .decoder(new JacksonDecoder())
//                .target(RemoteService.class, "http://Polaris_demo_web_springmvc/demospringmvc");        
//        for (int i = 1; i <= 10; i++) {
//            User result = service.getOwner(param);
//            System.out.println(result.getId() + "," + result.getUsername());
//        }
    }

}
