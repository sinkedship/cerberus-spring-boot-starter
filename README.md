# Spring boot starter for cerberus

Spring Boot autoconfigure and starter module for Cerberus.

It contains Spring Boot starter for both server and client side, which is:

```cerberus-server-bootstrap-spring-boot-starter``` and ```cerberus-client-proxy-spring-boot-starter```, respectively.

## How to use Cerberus Spring Boot Starter

### Server side

Add dependency in Maven:

```maven
<dependency>
    <groupId>com.sinkedship.cerberus</groupId>
    <artifactId>cerberus-server-bootstrap-spring-boot-starter</artifactId>
    <version>0.2.2-SNAPSHOT</version>
</dependency>
```

Create your server Spring Boot application:

```java
@SpringBootApplication
public class SampleStarterServer implements CommandLineRunner {

    @Autowired
    private CerberusServerBootstrap.Builder builder;

    public static void main(String[] args) {
        SpringApplication.run(SampleStarterServer.class, args);
    }

    @Override
    public void run(String... args) {
        A_ServiceImpl a = new A_ServiceImpl();
        B_ServiceImpl b = new B_ServiceImpl();
        // ...
        X_ServiceImpl x = new X_ServiceImpl();

        builder.withService(a)
                .withService(b)
                // .withService(...)
                .withService(x)
                .build().boot();
    }
}
```

```cerberus-server-bootstrap-spring-boot-starter``` configurates all the configurations from ```application.properties``` file specified by you or uses default values for which are not provided.

All you need to do is ```Autowired``` a ```CerberusServerBootstrap.Builder```, build any Thrift services with this builder with your business logic and boot it up!

#### All available server configurations by now

Config key|Category|Type|Default value|Remark
---|---|---|---:|:---
cerberus.server.bootstrap.data-center|common|string(enum)|local|Data center used by Cerberus
cerberus.server.bootstrap.register-host|common|string|an available inet IPv4 address|Host address(an external or internal address) that registered to data center
cerberus.server.bootstrap.bind-host|common|string|an available inet IPv4 address|Binding host for server
cerberus.server.bootstrap.bind-port|common|number|an available arbitrary port number|Binding port for server
cerberus.server.bootstrap.accept-backlog|common|nubmer|1024|Underlying netty accept backlog
cerberus.server.bootstrap.request-timeout|common|number|60000|Request time-out for any Thrift RPC call, measured in ***millisecond***
cerberus.server.bootstrap.io-thread-count|common|number|3|Thread counts used by underlying netty I/O event loop group
cerberus.server.bootstrap.worker-thread-count|common|number|available processors * 2|Thread counts used by underlying netty worker event loop group
cerberus.data-center.zookeeper.connect-string|zookeeper|string|localhost:2181|Connection string used to connect to Zookeeper: ***$HOST_1:$PORT_1,...,$HOST_X:$PORT_X***
cerberus.data-center.zookeeper.base-path|zookeeper|string|cerberus|Path used as 'ROOT' path in Zookeeper
cerberus.data-center.zookeeper.session-timeout|zookeeper|number|15000|Zookeeper session timeout, measured in ***millisecond***
cerberus.data-center.consul.host|consul|string|localhost|Connecting host to Consul client agent
cerberus.data-center.consul.port|consul|number|8500|Connecting port to Consul client agent
cerberus.data-center.etcd.end-points|etcd|string|http://localhost:2379|Endpoint urls to Etcd client
cerberus.data-center.etcd.key-prefix|etcd|string|cerberus/services|Keys' prefix used to register service
cerberus.data-center.etcd.service-ttl|etcd|number|5000|Service time to live in Etcd, measure in ***millisecond***
cerberus.data-center.etcd.service-keep-interval|etcd|number|3000|Interval to keep service alive which should be smaller than ttl, measure in ***millisecond***

### Client side

Add dependency in Maven:

```maven
<dependency>
    <groupId>com.sinkedship.cerberus</groupId>
    <artifactId>cerberus-client-proxy-spring-boot-starter</artifactId>
    <version>0.2.2-SNAPSHOT</version>
</dependency>
```

Create your own client Spring Boot application:

* Provides all your business services to Spring using Java config style

```java
@Configuration
public class ServiceConfiguration {

    @Bean
    public A_Service.Async asyncAService(CerberusServiceFactory factory) {
        return factory.newService(A_Service.Async.class);
    }

    @Bean
    public A_Service aService(CerberusServiceFactory factory) {
        return factory.newService(A_Service.class);
    }

    @Bean
    public B_Service.Async asyncBService(CerberusServiceFactory factory) {
        return factory.newService(B_Service.Async.class);
    }

    @Bean
    public B_Service bService(CerberusServiceFactory factory) {
        return factory.newService(B_Service.class);
    }

    // And so on
}
```

```cerberus-client-proxy-spring-boot-start``` creates the ```CerberusServiceFactory``` internally and injected to Spring container if it's not appeared in Spring context. And of course, you can still provide your own ```CerberusServiceFactory``` to Spring manually without the autowired one as above.

* Create your client Spring Boot application

```java
@SpringBootApplication
public class SampleStarterClient implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(SampleStarterClient.class);

    // Autowired any services you have already defined
    @Autowired
    private A_Service aService;

    // @Autowired
    // private B_Service.Async bAsyncService;

    public static void main(String[] args) {
        SpringApplication.run(SampleStarterClient.class, args);
    }

    @Override
    public void run(String... args) {
        // aService.do_something(...);
    }
}
```

All you need to do is defining your own Thrift services and let ```CerberusServiceFactory``` creates them for you and put them into Spring container.

#### All available client configurations by now

Config key|Category|Type|Default value|Remark
---|---|---|---:|:---
cerberus.client.proxy.data-center|common|string(enum)|local|Data center used by Cerberus
cerberus.client.proxy.connect-timeout|common|number|500|Connection timeout, measured in ***millisecond***
cerberus.client.proxy.request-timeout|common|number|60000|Request timeout for a Thrift RPC call, measured in ***millisecond***
cerberus.data-center.zookeeper.connect-string|zookeeper|string|localhost:2181|Connection string used to connect to Zookeeper: ***$HOST_1:$PORT_1,...,$HOST_X:$PORT_X***
cerberus.data-center.zookeeper.base-path|zookeeper|string|cerberus|Path used as 'ROOT' path in Zookeeper
cerberus.data-center.zookeeper.session-timeout|zookeeper|number|15000|Zookeeper session timeout, measured in ***millisecond***
cerberus.data-center.local.connect-host|local|string|localhost|Host used to connect to remote server in Local data center mode
cerberus.data-center.local.connect-port|local|number|0|Port used to connect to remove server in Local data center mode
cerberus.data-center.consul.host|consul|string|localhost|Connecting host to Consul client agent
cerberus.data-center.consul.port|consul|number|8500|Connecting port to Consul client agent
cerberus.data-center.etcd.end-points|etcd|string|http://localhost:2379|Endpoint urls to Etcd client
cerberus.data-center.etcd.key-prefix|etcd|string|cerberus/services|Keys' prefix used to register service
cerberus.data-center.k8s.api-server-host|k8s|string|System ENV: KUBERNETES_SERVICE_HOST|K8S API server host
cerberus.data-center.k8s.api-server-port|k8s|number|System ENV: KUBERNETES_SERVICE_PORT|K8S API server port
cerberus.data-center.k8s.auth-token|k8s|string|Token file from ```/var/run/secrets/kubernetes.io/serviceaccount/token``` in pod|Auth token to create API client
cerberus.data-center.k8s.namespace|k8s|string|default|Namespace in kubernetes
cerberus.data-center.k8s.verify-ssl|k8s|boolean|true|Whether to verify certificate and hostname when making https requests to Kubernetes API server
cerberus.data-center.k8s.svc-refresh-interval|k8s|number|30000|Interval used to refresh cache in K8s service discoverer, measured in ***millisecond***
cerberus.data-center.k8s.svc-cache-size|k8s|number|100|Size of service discoverer uses to cache for any resolved k8s service.  
