- ### 截图  


<p float="left">
<img src="https://github.com/stategen/docs/blob/master/javaCodeDemo.png" width="600" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/typescriptCodeDemo.png" width="600" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/flutterCodeDemo.png" width="600" alt="drawing" />
</p>
<p float="left">
<img src="https://github.com/stategen/docs/blob/master/category.png" width="400" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/homeApis.png" width="400" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/category_json.png" width="400" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/dva_react_model.png" width="400" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/flutter_goods_provider.png" width="400" alt="drawing" />
<img src="https://github.com/stategen/docs/blob/master/stategenAppSnapshort.png" width="400" alt="drawing" />
</p>   
mobile端    
<img src="https://github.com/stategen/docs/blob/master/stategenWebSnapShort1.png" width="400" alt="drawing" />  
web端    
上面2张图，按通常的开发量需要上千行代码，现在只需要开发10多行代码



- ### 我非常赞同的
  - 重复可能是软件中一切邪恶的根源。—— Robert C.Martin

- ### StateGen?
  - 轻代码or敏捷开发?  **NO**; 
  - 用可视化界面配置生成?  **NO**
  - 改变原开发模式?  **NO**

- ### Stategen架构的构成
  - springboot
    - 直接支持 jar war打包模式
  - spring cloud alibaba
    - nacos,seata,sentinel,dubbo,mybatis|ibatis开箱即用
    - 分布式  微服务+**本地服务**
  - 后端骨架生成器
  - 前端骨架生成器
  - 后端可迭代开发生器(dalgen演化而来dalgenX，全网唯一可支持迭代开发??)
  - 前端开发生成器，可把后端所有任意java api随时一键导出为前端的交互代码(mvvm,reactive,react(umi,dva,saga),flutter(provider),依据模版种类)
  
  - **没有限定使用者集成其它技术**
- ### StateGen中的开发生成器，和市面上那些谈虎色皮的生成器什么区别?
  1. a.后端dalgenx生成器，从大名鼎鼎的支付宝生成器dalgen演化而来,单dalgen可以说把市面上所有的java orm层生成器秒成渣，dalgenX则在此基础上拓展可迭代功能。dalgen只支持ibatis(个人认为:不开源和不支持mybatis使它推广不开来),而dalgenX则可以在ibatis与mybatis之间自由切换.    
    b. dalgenX生成器中的sql相当于Batis sql的用来简化开发的语法糖，它生成代码时，代替肉身查找替换。因为是语法糖，它不参与运行期，不用提心”国产“框架的坑。   
    c.dalgenX生成代码时，会解析已有的java代码，自动增量比对生成，代替肉身增量备份代码。自动维护pojo等一系列代码,源头上做到一个Pojo可以自代替DTO,VO,PO，Pojo本来就是干这些事的，只是其它生成器做不到而已，它也有效治好了DDD模型中的失血模式下的失忆的毛病。   
   2. 前端生成器只是在原controller层api方法上加了个java标注 @State而已，对后端代码零侵入,零工作量.
         它成立的理论基础是:   
            a. 响应式前端，交互和页面是分开的。  
            b. 后端任意一个api,它对应的前端代码：入参、出参对象化，序列化，反序列化，网络调用，状态化都是固定，谁肉身来写都相同的，所以可以用生成器覆盖。   
            c. 前端开发生成器只是**托管intergrade文件夹**下的内容，其它代码只是辅助生成，不再覆盖，使用的同学可无限制优化里面的代码，可以换成自己的理想的前端骨架。    
   3. 个人觉得几个生成器可以减少80%的工作，这还不算主要的，**主要是底层代码规范，上层代码就不会乱**.我见过太多的代码死在不规范上，只是局中人意识不到而已，锅从来都是这一任甩给上一任.

- ### 骨架代码生成流程图
  - 虚线为人工参与点
  - 实线为maven或系统自动装配
  - 粗实线为StateGen自动生成的线路
  - 系统骨架和项目骨架生成器运行时都是**幂等**，在已有的项目上重新运行只会追加，不会覆盖，不用担心手贱的毛病.
  - 图中"trade"指stategen架构中的系统名   app或cms或xxx指项目
  
- ![骨架代码生成流程图](https://github.com/stategen/stategen/blob/master/system_gen_flow.svg)


- ​	直观文件夹树型图:

```
trade (trade相当于微服务中当前服务名、系统名)
├── 1-trade-pojo
├── 2-trade-facade
├── 3-trade-intergrade
├── 4-trade-dao
├── 5-trade-service
├── 6-trade-web-base
├── 7-trade-web-app
│   ├── app-frontend-flutter
│   ├── app-frontend-h5
│   └── WebRoot
├── 7-trade-web-cms
│   ├── cms-frontend-web
│   └── WebRoot
├── 7-trade-web-...
├── opt
│   └── config
│       └── stategen
└── tables
```
为什么是多层的？建议把架构设计7大原则读一遍,这里不纠缠。
- #### 

- #### 骨架快速开始
  - ##### 运行环境

>  服务端/windows(linux类似)
>  > A.	java 1.8+  
>  > B.	maven 3  （3.5.0有bug,请使用3.5.2+）
>  > C.	mysql5.7  
>  > D.	gitbash(安装git2.0 自带,目的是可在windows上执行bash脚本)  
>  > E.	 nacos-server-1.3.2 (因为目前架构中用到的spring cloud alibaba denpencies版本为2.2.3,其中限定nacos client为1.3.3,它与nacos1.4.0-server通信有障碍，本架构用于生产，不在尝鲜版上纠缠，等他们稳定了再升级)
>  > F.	sentinel dashboard-1.8.0    ps：因为在dashboard上操作不能反向持久化到nacos中,开发和生产很不方便，有大神制从原版中修改了代码制了nacos反向持久化版，我稍微忧化可用性和方便性，,https://github.com/stategen/sentinel-dashboard-nacos
>  > 它的启动方式是这样的:
```
 java -Dnacos.server-addr=localhost:8848 -Dserver.port=8880 -Dcsp.sentinel.dashboard.server=localhost:8880 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-nacos-1.8.0.jar
```
>  > G.	seata-server-1.4.0

- ##### 开发环境安装

因项目依赖jars我已经发布maven中央仓库了，不需要使用的同学再辛苦自行编译，只需要git clone dalgenX,这是一个GMAVEN项目,
1.  配置 dalgenX
```
git clone https://github.com/stategen/dalgenx.git
```
2. 设置 DALGENX_HOME 环境变量为 dalgenx所在目录  
3. 将 %DALGENX_HOME% 添加至 PATH 中  

4. Ide中配置（eclipse|myeclipse|idea）xml文件，方便开发时打字提示.

>>location: {DALGENX_HOME}\gen.schemas-1.0.dtd  
>>key type: system Id  
>>key: https://github.com/stategen/dalgenx/blob/master/gen.schemas-1.0.dtd

- ##### 用命令初始化系统及项目/范例
ps: 以下gen.sh 必须在gitbash中运行，不能在cmd中运行。linux可以无需考虑。
1.  帮助
```
gen.sh -h
```
2.  创建系统骨架,注意，骨架|脚手架生成器操作是幂等的，可以多次执行. 
    -e 是当有错误时，输出错误信息.
```
gen.sh system com.mycompany.biz trade -e  
```
>com.mycompany.biz 为包名   
>trade 系统名 /数据库名 dubbo 系统名  
至些，一个StateGen系统分分中创建完成,接下来添加项目，可以无限添加项目，为啥这样搞？它们遵从**同一个数据库只有一套crud服务**的原则.减少扯皮，减少在泡在会议室的时间，谁改完了表，都得改想关的影响点,，好在，因为架构上它们在同一个系统中，ide会帮你及时发现兼容bug

3.  创建app web api 项目  (可选) , （**不想参与前端的同学，涉及到前端的命令参数都可以忽略**）
```
//创建后端项目时，同时创建前端,前端目前有3个模版 h5,web,flutter 
gen.sh project app h5 –e  //带前端
//也可以：
gen.sh project app –e  //不带前端
//**h5和web对应的前端是nodojs项目，ssr模式，因此打包时,maven插件会编译前端**
//**必须安装nojs和yarn才能编译通过,也可到7-tradeApp的pom.xml中，把编译注释掉**

  
//因为app项目上面已创建，生成器会判断，以下命令只创建了一个flutter前端
gen.sh project app flutter –e  

//或者时入子目录
cd 7-trade-web-app
gen.sh client flutter -e //这样也可以创建flutter前端
```

4.  创建cms web 项目 cms指后台管理系统, (可选,也可以以后再创建或不要)
```
gen.sh project cms web –e 
也可以 
gen.sh project cms –e 
```
5.  创建shedule项目,不带前端, (可选,也可以以后再创建或不要)
```
gen.sh project schedule –e  //无前端，可以跑定时任务
```

//变为git版本控制, trade 为git 项目，app-frontend-flutter为trade的子项目,(git子项目其实也是一个独立的git项目，我个人觉得git这点比svn理念先进).
```
sh gitinit.sh //变为git项目
cd app-frontend-flutter
sh git_add_to_parent_as_sub.sh //变为trade 的git子项目
```

6.	环境及表  
>创建trade数据库并运行 运行 trade.sql 也可以建一个空表.
>把opt复制到同盘(tomcat所以盘或者你的ide同盘，这样目的是保持开发和运行环境一致。windows下/opt指的容器同盘目录)根目录下,修改stategen.xml中的数据库配置,只需要关注mysql,nacos，相关配置，zookeeper和redis可以不用管
>7-xxx下的stategen.xml相关的内容合并到/opt/config/stategen/stategen.xml中
>修改gen_config.xml中的开发数据库配置  
>因为一些文件可以用生成器获取，所以不在版本控制里，先后在 gitbatsh中运行 tablebatch.sh 和 dalbatch.sh ,空表则不需要运行.


```
sh ./tablebatch.sh 
sh ./dalbatch.sh 
```

启动TradeAppApplication.java 每个7中都有一个TradeXxxApplication.java

启动成功后控制台可以看到以下信息:

```
	Application  is running! Access URLs:
	servletWebServerFactory  类型	：UndertowServletWebServerFactory:
	Local访问网址: 		http://localhost:8080/tradeApp
	应用访问网址: 		http://192.168.112.1:8080/tradeApp
	Swagger网址: 		http://192.168.112.1:8080/tradeApp/doc/index.html
```
ps: 1.StateGen生成的架构的代码，启动时对端口做了占用检查，因此：一个项目启动多个实例也是没问题(只要不启动太快)，如8080端口,它的配置是这样的
```xml
    <bean class="org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory">
        <property name="contextPath" value="/${spring.application.name}" />
        <!-- 逻辑：如果端口被占用，则往上找直到没有被占用的端口 -->
        <property name="port" value="#{T(util.Net).from(${tradeApp.port:8080})} " />
    </bean>
```
ps: 2. StateGen默认你的项目会越来越大（谁个系统还没有百把张表或者自定义实现spring系统bean）,因此：除了必要的bean是代码创建的外，都是xml配置的，大系统，xml才是王道 .

在nacos上应该可以看到dubbo服务:

![nacos_trade截图](https://github.com/stategen/docs/blob/master/nacos_service_provider_trade.png)

在swagger中 调用一下一个AppController.java中的 testSentinel api

```java
    /***测试限流降级分布式事务*/
    @ApiRequestMappingAutoWithMethodName(method = RequestMethod.GET)
    @SentinelResource()
    public User testSentinel(@ApiParam(value="用户ID",defaultValue="1") @RequestParam() String  userId) {
        //MockUtil只能用于测试，不能打包，执行 mvn package 由 插件 forbiddenapis 检测
        MockUtil.throwRandomException(2);
        User user = this.userService.getUserByUserId(userId);
        return user;
    }
    
```

到Sentinel-dashboard中可以看到



![sentinel-trade](https://github.com/stategen/docs/blob/master/sentinel-trade.png)

设置限流降级,单机阈值设为2,快速在swagger中调用testSentinel,可以看到如下限流返回值

```javascript
{
  "code": 500,
  "exeptionClass": "FlowException",
  "message": "该阶段不支持该操作(限流)，请稍后再试",
  "status": "ERROR",
  "success": false
}
```
上面的message信息可以配置的:
```xml
    <bean class="org.stategen.framework.spring.mvc.SentinelBlockHandler">
        <property name="blockResponseStatus">
            <util:constant static-field="com.mycompany.biz.enums.ResponseStatus.BLOCK" />
        </property>
        <property name="msgFlowException" value="该阶段不支持该操作(限流)，请稍后再试"/>
    </bean>
```

9. 微服务集群相互调用:

   按上面我们再创建一个系统:verify     ,(另一个文件夹),

```
//创建系统
gen.sh system com.mycompany.verify auth -e
//创建系统中的项目
gen.sh project microServ -e
```

mavan打包发到公司私有仓库或者安装到本地仓库: 

```
mvn install|deploy
```

在原来的trade系统中3-trade-intergrade中pom.xml里面，只要引用即可。

```xml
<dependency>
    <groupId>com.mycompany.verify</groupId>
    <artifactId>auth-facade</artifactId>
    <!-- verify 系统每次变更，要改 verfy-facade.version版本号-->
    <version>1.0.0</version>
</dependency>
```
10 .  一个典型Stategen 系统 结构图

![Image](https://github.com/stategen/docs/blob/master/stg-fm-bbr.png) 

- #### 迭代开发流程图

  - 虚线为人工代码参与点

  - 实线为maven或系统自动装配
  - 粗实线为dalgenX自动生成和迭代的线路
  - 从流行程上来看，
    - **dalgenX没有改变原开发模式**
    - **保留之前工作代码成果，新生成代码增量添加**
    - **人工编写的代码可以做到仅限业务**

![迭代开发流程图](https://github.com/stategen/stategen/blob/master/dalgenx_gen_flow.svg)


#### 演示User需求开发 (一键开发，一键迭代,显式代码，所见即所得)
```sql
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `teacher_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `teacher_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码，测试，明文',
  `role_type` varchar(32) DEFAULT NULL COMMENT '用户角色 ADMIN,DEFAULT,DEVELOPER',
  `name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `nickName` varchar(32) DEFAULT NULL COMMENT '别名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `avatar_img_id` varchar(64) DEFAULT NULL COMMENT '头像 ID',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `vali_datetime` datetime DEFAULT NULL COMMENT '认证时间',
  `birthday_date` date DEFAULT NULL COMMENT '出生日期',
  `work_time` time DEFAULT NULL COMMENT '工作时间',
  `province_id` varchar(64) DEFAULT NULL COMMENT '省份 ID',
  `city_id` varchar(64) DEFAULT NULL COMMENT '城市 ID',
  `status` varchar(64) DEFAULT NULL COMMENT '状态 enum',
  `grade` bigint(2) DEFAULT NULL COMMENT '级别',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别',
  `post_address_id` bigint(20) DEFAULT NULL COMMENT '邮寄地址 ID',
  `remark` text,
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `delete_flag` int(1) DEFAULT NULL COMMENT '是否删除 (0:正常，1删除)',
  PRIMARY KEY (`teacher_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `teacher_name` (`teacher_name`),
  KEY `province_id` (`province_id`),
  KEY `city_id` (`city_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

```

1.运行命令生成表sql配置和java代码 (或者在6-${systemName}-web-base/test下运行 DevGennerator.java)
```
gen.sh table user –e
```
>2.检查teacher.xml对应的java类是否正确，去掉?及一行空格
```
gen.sh dal user –e
```
服务的继承关系为 :
**UserServiceImpl implements <<UserService  extends  <<UserServiceTrade**
UserService内的服务都是本地服务，复制接口到 UserServiceTrade中，即可暴露微服务
我个人觉得dubbo还有一个开发优势是微服务异常也可以原文传递

F5刷新eclipse 检查import是否完整  
动做一个controller或者用命令初始化一个controller
```
//只是辅助快速生成一个UserController.java,一但生成后，每二次执行不会覆盖
gen.sh api user cms|app
```

分布式事务Seata已经集成到里面了,使用的地方禁注一下@GlobalTransactional.,如:
```java
    /***测试seata分布式事务*/
    @ApiRequestMappingAutoWithMethodName(method = RequestMethod.GET)
    @GlobalTransactional
    public User testSeata() {
        User user = this.userService.appendUserAge("2");
        return user;
    }
```
分布式id生成器baidu uid也集成在里面,不用再像以前一样往redis或zookeeper内肉身放置
```java
public class UserServiceImpl implements UserService, IdGenerateService<String> {

    @Resource
    private IIdGenerator idGenerator;

    @Override
    public User insert(User user) {
        return userDao.insert(user, this);
    }
end    
```

>5.	eclipse打开查看是否有代码错误


#### 生成前端代码，开发前端（不做前端可以暂时不用看）
>1.	运行test/UmiFacadeProcessor.java   
>2.	webstorm打开前端代码 ，配置webpack解读代码  
>3.	yarn 下载前端依赖  
>4.	fiddler 脚本设置 onBeforeRequest 函数中        

```
        var url:String=oSession.PathAndQuery;
      
        if (oSession.host=="localhost:8000") {         
            if ( url.StartsWith("/tradeCms/api/") || url.StartsWith("/tradeCms/uploads/") ) {
                oSession.host="localhost:8080";
            }            
        } 
        else if (oSession.host=="localhost:8001") {
            if ( url.StartsWith("/tradeApp/api/") || url.StartsWith("/tradeApp/uploads/") ) {
                oSession.host="localhost:8080";
            }            
        }
```

>5.	后端发布到eclipse tomcat中,运行  
>6.	yarn run dev  
>7. 后端代码变化后， 直接运行对应的 XXXFacadeProcessor.java，前端可实时开发编译




#### 帮助

- StateGen(QQ群：728343119)
- 没有人可以擅长所有，StateGen 要做的把好的思想集中起来。欢迎加入，一起飞。
- 目前最缺vue,swift,kotlin,tora状态化模版,

- 增加一篇论文介绍原理:[利用java反射和java-parser制作可以迭代、分布式、全栈代码生成器的研究](https://github.com/stategen/stategen/blob/master/%E5%88%A9%E7%94%A8java%E5%8F%8D%E5%B0%84%E5%92%8Cjava-parser%E5%88%B6%E4%BD%9C%E5%8F%AF%E4%BB%A5%E8%BF%AD%E4%BB%A3%E3%80%81%E5%88%86%E5%B8%83%E5%BC%8F%E3%80%81%E5%85%A8%E6%A0%88%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8%E7%9A%84%E7%A0%94%E7%A9%B6.md)    

#### stategen对服务端代码的增强介绍
  Stategen要做的事，尽量地合理实现一个商业框架（不是开源后阉割版的那种）。一些过时的技术比如osgi摈弃，尽量在spring技术范围内解决。一些拓展点技术（非spring）我个人觉得对业务代码没有帮助而是挖坑（大牛一走，项目搞不下去了）
1.  @Wrap对返回置封装,对业务代码**零侵入**
```java
        //以前的代码是这样地恶心
        @SuppressWarnings("unchecked")
        @ResponseBody
        @RequestMapping("getUserByUserId")
        public Response<User> getUserByUserId(String userId){
            User user = this.userService.getUserByUserId(userId);
            if (user!=null) {
               return new Response<User>(user);
            } else {
                return  (Response<User>) Response.error("用户不存在"); 
            }
        }
```
上面的java代码里到处对返回值封装，现在可以通过以下方式让spring自动封装返回值
```xml
    <!-- Response可以自定义，也即自定义封装 -->
    <bean id="response" class="com.mycompany.biz.domain.Response" 
    scope="prototype"/>
```
```java
    //@warp也可以一次性配在Controller上
    @Wrap
    public class UserController{
        //现在
        @ResponseBody
        @RequestMapping("getUserByUserId")
        //@warp也可以一次性配在Controller上
        @Wrap
        public User getUserByUserId(String userId){
            User user = this.userService.getUserByUserId(userId);
            BusinessAssert.mustNotNull(user, "用户不存在");
            return user;
        }
    }
    
```
```javascript
 /*api返回值,自定义实现Response.java类就可以自定义实现封装*/
{
    message:'成功',
    success:true,
    data:{username:'张三',nickname:'zhangsan',...},
    ...
}
```
```java
        //当@wrap一次性配到controller上时，也可以把个别api除外
        @Wrap(false)
        @ResponseBody
        @RequestMapping("deleteUserById")
        public String deleteUserById(String userId){
            this.userService.delete(userId);
            return userId;
        }
```
2. @ApiRequestMappingAutoWithMethodName 对 @RequestMapping硬编码的处理
```java
    @ResponseBody
    /*这里有硬编码而且允许与methodname不一致，review时，看到里而全是xxxNew,xxxOld,xxxV1,xxxV2
    真有想打人的冲动。
    再比如，当前端反馈getUser有问题，
    后端还要搜一下代码才能定位，代码交接和team衔接时先得跳坑 */
    @RequestMapping("getUser") 
    public User getUserByUserId(String userId){
        User user = this.userService.getUserByUserId(userId);
        return user;
    }
```
```java
    /*现在直接下面标注，它等于@RequestMapping("getUserByUserId")，
    但是不用写硬编码，直接跟方法名走,同时有swagger2::@ApiOperation,@@ResponseBody*/
    @ApiRequestMappingAutoWithMethodName
    public User getUserByUserId(String userId){
        User user = this.userService.getUserByUserId(userId);
        return user;
    }
```
3. 统一错误处理
```java
    //以前是这样地恶心
    //@...省略
    public Object getUserByUserId(String userId){
        try {
            User user = this.userService.getUserByUserId(userId);
            return user;
        } catch (Exception e) {
            logger.error("", e);
            return  Response.error("服务端异常"); 
        }
    }
```
```xml
    <!-- 现在只需要配置一个bean ，在骨架xml中， 这里只是讲解，不需要再添加-->
    <bean class="org.stategen.framework.spring.mvc.CollectExceptionJsonHandler">
        <!-- 注意这里用Class可以避免硬编码，我真的讨厌硬编码，哈哈-->
        <property name="responseStatusClzOfException" 
            value="com.mycompany.biz.enums.ResponseStatus.ERROR" />
    </bean>
```
```java
    //然后，就不用关注异常和错误日志输出了,函数返回值也限定为User，
    //异常和日志由CollectExceptionJsonHandler自动处理，错误也自动包装返回给前端
    //@...省略
    public User getUserByUserId(String userId){
        User user = this.userService.getUserByUserId(userId);
        return user;
    }
```
4.方法鉴权和垂直权限
```java
    //以前是这样的校验,除了恶心之外，token很容易被截获，api被人破了后，得有人背锅跑路
    //@...省略
    public Object getUserByUserId(String userId,String token){
        if (processToken(token)) {
            return Response.error("没有登录")!
        }

        User user = this.userService.getUserByUserId(userId);
        return user;
    }
    
```
```xml
    <!--现在我们可以配一个bean统一处理方法鉴权和垂直权限  ，在骨架xml中， 这里只是讲解，不需要再添加 -->
    <bean id="authCheckerHandlerInterceptor"
        class="org.stategen.framework.spring.mvc.AuthCheckerHandlerInterceptor">
        <!-- 用类不用字符串硬编码 -->
        <property name="responseStatusClzOfCheckFailDefault"
            value="com.mycompany.biz.enums.ResponseStatus.NOT_LOGIN" />
    </bean>
    
    <bean class="com.mycompany.biz.checker.VisitChecker" />
    <bean class="com.mycompany.biz.checker.LoginChecker" />
```
```java
    //以前是这样的校验，token很容易被人截获，架构师背锅跑路
    //@...省略
    /*也可以配到Controller上统一处理,由于token不再时单一值，
    我们可以对token进行先进行防伪造签名校验...， @LoginCheck(exclude=true)例外
    */
    @LoginCheck 
    @VisiterCheck
    ...想验证哪个实现哪个Checker
    //返回值是具体的类，比如User
    public User getUserByUserId(String userId){
        User user = this.userService.getUserByUserId(userId);
        return user;
    }
```
5. Cookie校验.我刚写框架改造我们那个旧系统(每个url后都有个?token=xxxxxx,除了恶心就是不安全,hibernate还把token返回给所有用户，怕怕)，要用到Cookie,我的CTO（真来自国际大厂）反对说:Cookie不安全不能用，我对他打了个比方，门不安全不等于连门都不设让小偷直接进来，我们要想法改造门让它变安全。实际上,cookie作为http协议的一部分，无论是服务端或者客户端都非常成熟的实现，是会不会用的问题，活用cookie可以减少服务端和客户端非常大的工作量。浏览器打开大厂淘宝的cookies看看，它有一个cookie名叫_tb_token_，这个cookie是taobao对其站内其它cookie的签名。  
stategen中的cookieGroup就是对_tb_token_的开源实现，支持混淆码由运维控制。考虑到cookie的多样性，cookie多的情况下，也不易控制，特意给Cookie做了分组，  
以上只是讲把一些值放在cookie里，当然放在head里也可以（放head里生命周期要自己管理），但最好不要拼在url后面或form里
```xml
    <!-- 验证cookie分组 ,该类可以多个，配置不同的分组 -->
    <bean id="loginCookieGroup" class="org.stategen.framework.web.cookie.CookieGroup">
        <property name="cookieTypeClz" value="com.mycompany.biz.enums.CookieType.Login" />
        <property name="httpOnly" value="${loginCookieGroupHttpOnly}"/>
    </bean>
```
```java
    /*代码中这样注入，支持String或枚举拿放cookie，又枚举？哈哈，
    我是真的不喜欢字符串硬编码。系统中，cookie名称是有限的，枚举远比static String更好地限定数据范围，*/
    @Resource
    private CookieGroup<LoginCookieNames> loginCookieGroup;
    
    //放置简单，直接放，签名是由CookieGroup内部自动完成的
    loginCookieGroup.addCookie(LoginCookieNames.userId, loginUser.getUserId());
    
    /*cookie值是有签名的(xx_token)，客户端无法伪造这个值。开发人员也不能反算这个值，
      因为混淆码读取stategen.xml中的，由运维控制的*/
    String userId = this.loginCookieGroup.getCookieValue(LoginCookieNames.userId);
```
需要说明的是：Cookie校验是在filter中进行的。那为啥不在springMVC中呢？打个比方，Cookie校验是防伪造校验，好比总入口大门的保安一眼就能识别来人是否合法，就没必要先搬来各种重型设备再一眼就能识别是否合法，对系统资源利用上的浪费
```java
    //spring web3.0
    @WebFilter(filterName = "CustomMultiFilter", urlPatterns = "/*")
    public static class CustomMultiFilter extends org.stategen.framework.spring.mvc.MultiFilter {
    }
```
6. 环境配置与打包无关。环境配置是运维的冬冬，应该由运维来控制，还有一些是敏感数据，比如**数据库密码**，这些是万万不能给到开发人员的，网上远程删库跑路的悲剧又不是一回两回了，但常用的maven spring打包都不能避免这种坑，我是亲眼到我上一任架构师打个包像做贼一样，打完包还担心得要死（怕环境搞错了）就这么小心还是犯疏忽。stategen把环境变量和日志配置都放到/opt/config/stategen/，由运维控制,一劳永逸，同时支持windows上开发，linux运行，测试通过war还可以直接由手工或jekkins直接扔生产，而不用再打包，避免风险。这期间，开发、测式、运维和气生财。大厂antx.xml也这么处理的，不是我独创
```xml
    <bean id="propertyPlaceholder"
        class="org.stategen.framework.spring.mvc.MultiPropertyPlaceholderConfigurer">
        <property name="locations">
            <list>
                <value>classpath*:application.properties</value>
                <value>file://opt/config/stategen/stategen.xml</value>
            </list>
        </property>
    </bean>
    <!-- stg.2.3.0.RELEASE配在bootstrap.yml中 -->
```
```properties
    #application.properties
    logback.configfile.xml=file://opt/config/stategen/logback-config.xml
```
7. 支持分布式id生成器，目前默认是百度uid-generator,非强制性
```xml
    <!-- 启用baidu uid ,这里可以看出大厂也喜欢用xml显式配置bean,为啥，自己体会 -->
    <import resource="classpath*:uid/cached-uid-spring.xml" />
    <bean id="idGenerator" class="com.mycompany.biz.service.impl.IdGeneratorImpl"/>
```
8. 开关注册 dubbo provider服务。默认是开启的，但有些小系统比如跑定时任务的，它只是微服务的消费者可以不是生产者，这种配置可以做到，配在application.yml则不行.
```xml
    <!-- 反注释 dubbo-provider-spring.xml 中的 -->
    <!-- <import resource="classpath*:context/dubbo-provider-auto-*.xml" /> -->    
    <!-- <import resource="classpath*:context/dubbo-provider-manual-*.xml" /> -->
```

9. 国际化...以后再讲，我觉得也很屌

## 
11. dalgenX生成器支持在ibatis和mybatis语言两种orm之间切换:

```xml 
    <!-- gen_config.xml中 -->
    <!-- ibatis,mybatis,最下面覆盖上面，最下面优先 ，修改顺序后，需要重新运行一次 ./dalbatch.sh 批量生成-->
    <entry key="dao_type">mybatis</entry>
    <entry key="dao_type">ibatis</entry>
```
12. dalgenx借鉴了dart和typescript等先进语言, 有几个语法糖如下，可以减少where条件书写工作量，使sql简洁易懂(注：语法糖会被编译成最终sql，不参与运行期)
```xml
    <!-- user.xml中一条方法，这个是自动生成的，如果手工书写时，有提示帮助-->
    <!-- a.username 不需要写 小驼峰名称，以及jdbctype参数，
    不需要写返回值对照配置，由dalgenX生成User-sqlmap-mapping.xml自动生成 -->
    <operation name="..." remarks="">
        <sql>
            select
            a.user_id,
            a.username,
            a.name,
            a.nickName,
            a.code,
            a.grade,
            a.status
            from user a
            where
            a.delete_flag = 0
            and a.username=? /*必选条件,省略参数名*/
            and a.name like #cstmName# /*必选条件*/
            and a.username=?? /*动态条件,省略参数名*/
            and a.nickName=?#nkName# /*动态条件*/
            and a.code > ? /*必选条件,省略参数名*/
            and a.grade &lt; #gradeList# /*必选条件*/
            and a.grade in ?? /*动态条件,省略参数名*/
            and a.status not in ?#statusList#  /*动态条件*/
        </sql>

    </operation>
```
#####  
1. in语法糖: 

```sql
 a.mobile in ?
```
或
```sql
 a.mobile in #mobiles#
```
会被生成如下:   
mybatis2|ibatis:  (若在gen_config.xml中配置 <entry key="list_subfix">List</entry>，则mobiles-->mobileList,否则复数形式按常用英文规则推导.   
mybatis2|ibatis: ) 
```xml
    a.mobile in
    <iterate property="mobiles" conjunction="," open="(" close=")">
        #mobiles[]#
    </iterate>
```
mybatis3:
```xml
    a.mobile in 
    <foreach collection="mobiles" item="item" separator="," close=")" open="(">
        #{item}
    </foreach>
```
2. ?? 或 ?#paramName# 语法糖：
```sql
 and a.address =??
```
或
```sql
 and a.address =?#address#
```
等同于以下,其中string类型或list类型用isNotEmpty标签封装 ,其它用isNotNull， 条件符支持and 、or，操作符支持 =、!=、<>、>=、<=、like、not like、in、not in,   
mybatis2|ibatis: 
```xml
    <isNotEmpty property="address" prepend="and">
        a.address =#address#
    </isNotEmpty>
```
mybatis3: （@util.Check@isNotEmpty可以gen_config.xml自由配置其它判空函数）
```xml
    <if test="address != null and @util.Check@isNotEmpty(address)">
        and a.address =#{address}
    </if>
```
3. 以上2个合在一起写也可以即 in ??和 in ?#listParamName#,如:
```sql
 and a.mobile in ??
```
或
```sql
 and a.mobile in ?#mobiles#
```
等同于以下形式:    
mybatis2|ibatis: 
```xml
<isNotEmpty property="mobiles" prepend="and">
    a.mobile in 
    <iterate property="mobiles" conjunction="," open="(" close=")">
        #mobiles[]#
    </iterate>
</isNotEmpty>
```
mybatis3:
```xml
    <if test="mobiles != null and @util.Check@isNotEmpty(mobiles)">
        and a.mobile in 
        <foreach collection="mobiles" item="item" separator="," close=")" open="(">
            #{item}
        </foreach>
    </if>
```
3. dalgenX生成mybatis文件时，也完整地实现mapper/daoImpl,襾不是采用mybatis的java代理方式，原因2个，反正生成的代码不需要维护，显式代码安全，2，显式代码调试跟踪断点日志都方便。
```
public class UserDaoImpl  extends SqlDaoSupportBase implements UserDao {
	/**
	 * sql:...略
	 * a.username 对应的参数自动生成小驼峰名称，以及参数类型，函数返囲值 
	 */
	public User getUserByUsername(String username) throws DataAccessException {
	    //HashMap初始化时，大小都自动确定了，节约内存，提高效率。
		Map<String,Object> params = new HashMap<String,Object>(1);
		params.put("username",username);
		/*下面User.getUserByUsername自动插入到生成的sql中 
		select /*User.getUserByUsername*/ ... from ...,方便druid中跟踪sql的执行效率,巴结DBA,哈哈
		*/
		return (User)super.selectOne("User.getUserByUsername",params);
	}
	...
```
- ##### dalgenx将水平权限（数据权限）融入架构中解决

1. 生成规则。水平权限要完全做到绕开暴力尝试,或者避免在别的api中泄露id被利用,显然，采用复杂id(uid、随机)生成方式治标不治本。同时，要兼顾代码速度、迭代、人员权限调整、下面简要地阐述一种水平权限方案，可以直接由dalgenX生成器来生成，大大降低开发成本，非常适合产品需求上的迭代，代码可以做到以不变应万变.
```
   A.定义一个组织架构表比如orgnization，树型数据 orgId, parentId
   B.把用户（即水平权限中的数据操作员）人分配到组织上（org_user表）,
     用户登录后获取自己的orgId即currOrgId,
   C.假设topic表需要水平权限控制，在表的备注中添加 -level(organization) -owner(user) 
      ,让dalgenX识别。
      -level(organization) //水平权限中的组织架构表为organization 
      -owner(user) //水平权限中数据属于指定人员表为user
   D.运行 gen.sh table topic时，会生成 topic_level_h 和 topic_owner_h表创建sql语句，复制出来运行。
   （dalgenX约定后缀为"_h"为水平权限相关的表）   
   E.用户生产数据时(比如topic表)，同时把数据添加到
     topic_level_h和topic_owner_h(由dalgenX显式生成相关的sql和调用java代码)
   F.用户查询，删除，更新数据时，由dalgenX显式生成相关的sql和调用java代码
     和参数：Boolean inclCurrOrgId, Long currOrgId, String currUserId
   G.由程序员在调用topicService的方法时，自由控制inclCurrOrgId，currOrgId，currUserId
```
- ##### dalgenX兼顾避免一些开发习惯上的坑。比如
```java
   /*根据主键查询，一般dao中的方法名是getById,
   我们在很多遗留的代码中，经常review到下面这样的代码，，
   这是开发调用ide自动代码完成功能后，没有改变量名 */
   User byId = userService.getById(id);
   ...
   ...
   byId.method1();
   byId.method2();
   byId.method3();
   //上面的byId没法扯皮，吵不过人家
   
   /*dalgenX根据主键查询，dao中的方法名是getUserById,
   简定开发忘记不改变量名，ide自动完成的变量名： */
   User userById = userService.getById(id); 
   ...
   /*好吧，无论什么时候阅读到下面的代码，
   看到userById也知道其类型是User */
   userById.method1();
   userById.method2();
   userById.method3();   
```
- ##### dalgenX在Service中生成一些符合合成/复用原则（CARP）的java代码，这种开发规范好理解也节约业务层大量开发
```java
public class UserServiceImpl implements UserService {
    
    //收集Bean上的userId,把查询到的user赋值到Bean的User上
    public <D> void assignBeanTo(Collection<D> dests, Function<? super D, String>
        destGetMethod, BiConsumer<D, User> destSetMethod) {
        ...
    }
    //收集Bean上的userIds,把查询到的users赋值到Bean的List<User>上
    public <D, G> void assignBeansTo(Collection<D> dests, Function<? super D, G>
        destGetMethod, BiConsumer<D, List<User>> destSetMethod, 
        BiConsumer<User, List<G>> resultSetQueryIdsFun, Function<? super User, G> resultGetGoupIdFun) {
        ...
    }

    //收到UserId,比如合并到List<Teacher>要
    public <D> void mergeBeanTo(Collection<D> dests, Function<? super D, String>
        destGetMethod) {
        ...
    }
    ...
}

   //调用非常简单，没有循环处理，没有硬编码码，如给List<Topic>每条Topic赋值作者信息
   userService.assignBeanTo(topics, Topic::getAuthorId, Topic::setAuthor);

```


#### 四 、前端生成器
   >1. 生成器适合响应式前端，不是旧式的mvc的jsp,jquery,easyui或者类似的冬冬.  
   >1.  对于后端一个任意给定的api,其对应的前端网络调用、数据状态化、交互代码基本都是确定和没有歧义的，既然是确定的，说明是规有律性的，找到规律就可以实现机器来生成，
   stategen在不增加学习和额外开发成本的情况下找到了这种规律，它避免了以往手工或半手工导致的不规范而增加开发、维护成本。现在stategen可以自动秒撸  
   >1.  经过分析,同一作用域中，任何新调用的api返回值与之前的数据之间的关系只有以下3种，如此，前端状态的自动化代码有了理论基础:
   >>a.  重新加载  
   >>b.  按主键增加或更新  
   >>c.  按主键删除  
   >3.  数据隔离  
     A.  按后端返回值类型隔离,同一个后端controller对应前端的model/provider，以下都是按 userArea 隔离  
       User,  
       List\<User\>   
       PageList\<User\>,  
       @State(area=User.class) public String delete(){}  
     B.  不同的Controller数据已经按model/provider隔离    
   >4. 以前生成代码方式有2种，配置和伪代码:这两种方式增加学习成本，看似节约时间，实际上要花费更多的时间在坑里维护代码，任何基于json或者配置做前端都是太菜了，stategen不这样做.
     StateGen直接通过java反射硬解析后端java代码来生成前端代码，没有任何多余工作环节或学习,不改变开发流程.当然也不存在挖坑  


   >1. 对于任意一个sql,其配置、对应的java代码，参数个数，类型，返回值类型、字段都是确定的，这些以前都是手工或半手工撸出来.
   >2. 业务逻辑都是由调用一个或多个sql组成的
   >3. 市面上代码生成器都覆盖式生成代码,其生成的代码无法预先指定继承、实现接口、类型指定,无法保护已有业务代码成果,
       比如要多写功能相同逻辑的DTO绕开限制，但这违背单一职责框架设置原理，欲使用先入坑.StateGen采用解析已有java
       代码的方式解决上述问题
### 代码生成器难以解决的是问题迭代和增量开发,但是实际的项目开发都是不断地迭代功能和新功能叠加，而stategen非常适合迭代开发   


#### StateGen已经支持flutter   
  采用google 2019 i/o大会上推荐的provider   

1. 在maven deploy｜package阶段，不要附带 ~~ -Dmaven.test.skip=true ~~  
    或者开发时，直接运行调用src/java/test/xxxxxxFacadeProcessor.java也可以直接生成前端代码.
```java
public class FlutterFacadeProcessor extends BaseGenFacadeProcessor {
    public static void main(String[] args) {
        FlutterFacadeProcessor flutterFacadeProcessor = new FlutterFacadeProcessor();
        try {
            logger.info("================== flutter 前端代码生成开始===========================");
            flutterFacadeProcessor.genFacade();
            logger.info("================== flutter 前端代码生成结束===========================");
        } catch (Exception e) {
            logger.error("生成前端代码时出错:", e);
        }
    }
    //...
}    
```
```xml
    <!-- pom.xml -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id><![CDATA[>>>>>>>>>>>>>>>>tradeApp auto generate flutter frondend
                        files  生成 flutter 前端代码 &lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;]]></id>
                        <phase>test</phase>
                        <goals>
                            <goal>java</goal>
                        </goals>
                        <configuration>
                            <mainClass>FlutterFacadeProcessor</mainClass>
                            <classpathScope>test</classpathScope>
                            <cleanupDaemonThreads>false</cleanupDaemonThreads>
                        </configuration>
                    </execution>
                </executions>
            </plugin>    
```

2. 支持任意api、任意返回值、任意参数,不是那种市面上简单的增删除修改。  
```java
@ApiConfig
public class TopicController extends TopicControllerBase {
    
    @ApiRequestMappingAutoWithMethodName
    @State(init = true, dataOpt = DataOpt.APPEND_OR_UPDATE)
    @GenRefresh
    public AntdPageList<Topic> getTopicPageList(
            TopicType topicType,
            Boolean mdrender,
            @ApiParam(hidden = true) Topic topic,
            Pagination pagination) {
        topic.setCreateTimeMax(DatetimeUtil.current());
        PageList<Topic> topicPageList = this.topicService.getPageList(topic,
            pagination.getPageSize(), pagination.getPage());
        topicService.assignTopicExtraProperties(topicPageList.getItems());
        return new AntdPageList<Topic>(topicPageList);
    }
```
以下以typescript和dart为例（Stategen采用freemark模版，也可以生成其它语言样式,懂rxSwift或rxAndroid的朋友也可以协助制作相关代码）.
特别说明，Stategen前端代码生成器,主要功能是通过对前端integrade文件夹的自动托管，让前端的工作量尽量集中在排版和美工上,前端龙骨代码是一次性的，程序员可以自行修改（龙骨代码并非我擅长，再说再好的龙骨代码也不是永久都是最好，所以没有写死，也不把前端朋友限定死,原则就是不挖坑）。  
以下代码都是在integrade文件夹内.
```typescript
//typescript
export default class TopicApis {

  /**
   * POST /api/topic/getTopicPageList
   */
  static getTopicPageList(params: { topicType?: TopicType, mdrender?: boolean, 
    page?: number, pageSize?: number }): AntdPageList<Topic> {
    let requestInit: RequestInitEx = <RequestInitEx>{};
    //tradeAppBaseUrlKey相当于http://domain,这里不写死，方便配置和用fiddler测试
    requestInit.apiUrlKey = tradeAppBaseUrlKey;
    //url永远都跟着后端走，别的团队出文件，咱直接出代码，准确,下同
    requestInit.url = '/api/topic/getTopicPageList';
    requestInit.mediaType = MediaType.FORM;
    requestInit.data = params;
    requestInit.method = Method.POST;
    return Net.fetch(requestInit);
  }
  ...
}
```
```dart
///dart语言
class TopicApis {
  /// POST /api/topic/getTopicPageList
  /// 
  static Future<AntdPageList<Topic>> getTopicPageList({Map<String, dynamic> payload,
    TopicType topicType, bool mdrender, int page, int pageSize }) async {
    var requestInit = RequestInit();
     //tradeAppBaseUrlKey相当于http://domain,这里不写死，方便配置和用fiddler测试
    requestInit.apiUrlKey = tradeAppBaseUrlKey;
    //url永远都跟着后端走，别的团队出文件，咱直接出代码，准确,下同
    requestInit.path = '/api/topic/getTopicPageList';
    requestInit.mediaType = MediaType.FORM;
    payload ??= {};
    if (topicType != null) {
      payload['topicType'] = topicType;
    }
    if (mdrender != null) {
      payload['mdrender'] = mdrender;
    }
    requestInit.data = payload;
    requestInit.method = Method.POST;
    var dest = await NetUtil.fetch(requestInit);
    return AntdPageList.fromJson(dest, Topic.fromJsonList);
  }
  ...
｝
```
包括以上api所依赖的bean,enum,泛型同时生成  
对于flutter,支持多云序列化、反序列化，比flutter插件准备，节省时间，快速迭代
```dart
///dart语言
class Topic with FrontBean {
  /// topicId
  static const String Topic_ID = 'topicId';

  /// 主题ID
  String topicId;

  /// author
  User author;
  //... 
  static Topic fromJson(Map<String, dynamic> json) {
    if (json == null) {
      return null;
    }
    return Topic(
      //多层自动调用反序列化
      author: User.fromJson(json['author']),
      //基本类型采用依赖倒置原则(DIP),不留搞，不挖坑
      authorId: JsonUtil.parseString(json['authorId']),
      //...
    );
  }  
  
  @override
  Map<String, dynamic> toJson() {
    var result = new Map<String, dynamic>();
    if (this.author != null) {
      //多层自动调用序列化
      result['author'] = author.toJson();
    }

    if (this.topicId != null) {
      //基本类型采用依赖倒置原则(DIP),不留搞，不挖坑
      result['topicId'] = JsonUtil.stringToJson(topicId);
    }
    //...
}

/*对于dart语言，它的枚举值实际对应的是数字，表示后端enum值没有意义，
不知道写dart的人脑袋是不是进水了,没有java一样的枚举算什么快速开发？
stategen也完美地避开这个坑 */
class TopicType extends ClassAsEnum<TopicType> {
  const TopicType(value, title) : super(value, title);

  /// 精华
  static const good = TopicType("good", '精华');

  /// 分享
  static const share = TopicType("share", '分享');
  //...
  static Map<String, TopicType> _map = {
    good.value: good,
    share.value: share,
     //...
  };

  static TopicType fromJson(dynamic value) {
    return _map[value];
  }

  static List<TopicType> fromJsonList(List<dynamic> values){
    return JsonUtil.parseList(values, TopicType.fromJson);
  }

  static Map<String, Option> topicTypeOptions ={
    /// 精华
    'good': Option(
      value: TopicType.good,
      label: '精华',
    ),

    /// 分享
    'share': Option(
      value: TopicType.share,
      label: '分享',
    ),
    //...

  };
}
```
前端生成响应式状态管理
```ts
//typescript语言，采用支付宝umi,dva(redux + react-router + redux-saga)
export const topicModel: TopicModel = topicInitModel;
/**  */
topicModel.effects.getTopicPageList = function* ({payload}, {call, put, select}) {
  //为啥不把TopicCommand中的方法直接生成到这里？
  //因为，考虑到方法override时，不过是代码再次组装，而不是再写一遍，这样搞是不是周到、体贴？
  const newPayload = yield TopicCommand.getTopicPageList_effect({payload}, {call, put, select});
  yield put(TopicCommand.getTopicPageList_success_type(newPayload));
};

export class TopicCommand extends BaseCommand {
  /**  */
  static * getTopicPageList_effect({payload}, {call, put, select}) {
    const oldTopicArea = yield select((_) => _.topic.topicArea);
    payload = {page: DEFAULT_PAGE_NUM, pageSize: DEFAULT_PAGE_SIZE, ...payload};
    const topicPageList: AntdPageList<Topic> = yield call(TopicApis.getTopicPageList,
        payload);
    const pagination =topicPageList!.pagination;
    //对上次state,跟据设置前端在状态里自动crud,牛不牛？
    const topics = updateArray(oldTopicArea.list, topicPageList!.list, "topicId");

    const newPayload: TopicState = {
      topicArea: {
        list: topics,
        pagination,
        queryRule: payload,
      },
    };
    return newPayload;
  };
```
```dart
///dat语言，采用目录google官方推荐的provider作为状态管理
abstract class TopicAbstractProvider with ChangeNotifier, BaseProvider, TopicBaseState {
  /// 
  Future<void> getTopicPageList(BuildContext context, {Map<String, dynamic> payload,
    TopicType topicType, bool mdrender, int page, int pageSize }) async {
  //为啥不把TopicCommand中的方法直接生成到这里？
  //因为，考虑到方法override时，不过是代码再次组装，而不是再写一遍，这样搞是不是周到、体贴？
    var newState = await TopicCommand.getTopicPageList(this, payload: payload, topicType:
        topicType, mdrender: mdrender, page: page, pageSize: pageSize);
    mergeState(context, newState);
  }
  //...
}
  
abstract class TopicCommand {

  /// 
  static Future<TopicBaseState> getTopicPageList(TopicAbstractProvider topicState,
    {Map<String, dynamic> payload, TopicType topicType, bool mdrender, int page, int pageSize }) async {
    var oldTopicArea = topicState.topicArea;
    payload ??= {};
    payload = {'pageNum': DEFAULT_PAGE_NUM, 'pageSize': DEFAULT_PAGE_SIZE,  ...payload};
    AntdPageList<Topic> topicPageList = await TopicApis.getTopicPageList(payload: payload,
        topicType: topicType, mdrender: mdrender, page: page, pageSize: pageSize);
    var pagination = topicPageList?.pagination;
    //对上次state,跟据设置前端在状态里自动crud,牛不牛？
    var topicMap = CollectionUtil.appendOrUpdateMap(oldTopicArea?.clone()?.valueMap, 
        Topic.toIdMap(topicPageList.list));

    var newState = _TopicState(
      topicArea: AreaState(
        fetched: true,
        valueMap: topicMap,
        pagination: pagination,
        queryRule: payload,
      ),
    );
    return newState;
  }
  ...
}

```
前端的同学，不懂后端的话，拼老命写mock（有空开发mock还不如直接告诉后端你想要啥，自己敲后端也行，在stategen中敲后端也是分分钟的事）, 你能保证你写的mock与后端一致吗？有了stategen,这些工作是不是完美地解决了?而且，迭代时，直接给你出代码，假如前端排版代码里有兼容问题，**编译器和ide都能帮你发现**。   
还有:stategen也可以跟据设置，生成前端对应的状态初始化，刷新、上一页，下一页的前端代码.      
还有：我真的非常讨厌字符串硬编码，我是亲眼见过同事为了个大小写问题，节假日加班找bug，有了stategen,不要说节假日省了，都没有996了    
还有:...



本说明视频演示请移步[Stategen快速调试开发运行精简教程](https://v.youku.com/v_show/id_XNDIxMzM4ODQzMg==.html?spm=a2h3j.8428770.3416059.1)  
视频中的相关文档，请见 https://github.com/stategen/docs    



#### 打包 前后端 （注意：因为stategen编译前端是在maven test阶段，所以不能添加参数 -Dmaven.test.skip=true）
```
mvn package 
```
## 七、早期视频
[Stategen快速调试开发运行精简教程](https://v.youku.com/v_show/id_XNDIxMzM4ODQzMg==.html?spm=a2h3j.8428770.3416059.1)  
### 详细视频 共6小时
[1.stategen之前 微博 趣图](https://v.youku.com/v_show/id_XNDIwODcxNzk2OA==.html?spm=a2h3j.8428770.3416059.1)          
[2.stategen简介](https://v.youku.com/v_show/id_XNDIwOTk1MjE0MA==.html?spm=a2h3j.8428770.3416059.1)          
[3.stategen依赖环境](https://v.youku.com/v_show/id_XNDIwOTk1Mzc2OA==.html?spm=a2h3j.8428770.3416059.1)          
[4.stategen安装和配置](https://v.youku.com/v_show/id_XNDIwOTc4MTU1Mg==.html?spm=a2h3j.8428770.3416059.1)          
[5.生成stategen系统以及工程](https://v.youku.com/v_show/id_XNDIwOTg5MjQ0MA==.html?spm=a2h3j.8428770.3416059.1)          
[6.stategen服务端开发代码演示(上)](https://v.youku.com/v_show/id_XNDIwOTg5MjM3Mg==.html?spm=a2h3j.8428770.3416059.1)          
[7.stategen服务端开发代码演示_迭代开发(中)](https://v.youku.com/v_show/id_XNDIwOTg5MjM5Ng==.html?spm=a2h3j.8428770.3416059.1)          
[8.stategen.mvc](https://v.youku.com/v_show/id_XNDIwOTkxNDg1Mg==.html?spm=a2h3j.8428770.3416059.1)          
[9.stategen前端简介](https://v.youku.com/v_show/id_XNDIwOTkxNDgyOA==.html?spm=a2h3j.8428770.3416059.1)          
[10.stategen前端dva](https://v.youku.com/v_show/id_XNDIwOTkxNDg2MA==.html?spm=a2h3j.8428770.3416059.1)          
[11.stategen前端form 生成和实现](https://v.youku.com/v_show/id_XNDIwOTkxNDgzNg==.html?spm=a2h3j.8428770.3416059.1)          
[12.stategen运行前后端和开发](https://v.youku.com/v_show/id_XNDIwOTk1Mzc4NA==.html?spm=a2h3j.8428770.3416059.1)          
## 鸣谢
   [react] https://github.com/facebook/react，   
   [ant-design] https://github.com/ant-design/ant-design   
   [dva] https://github.com/dvajs/dva   
   [umi] https://github.com/umijs   
   [rapid-framework] https://github.com/badqiu/rapid-framework    
   [zuiidea] https://github.com/zuiidea/antd-admin   
   [dubbox] https://dangdangdotcom.github.io/dubbox   
   [spring-framework] https://github.com/spring-projects/spring-framework   
   ...
