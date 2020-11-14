## 截图  
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

### 我非常赞同的
  重复可能是软件中一切邪恶的根源。—— Robert C.Martin
### 我的一些编程总结
  硬编码和不可感知的字符串是项目迭代中的定时炸弹.  
  写在规章制度里的开发规范是被人用来打破的，好的规范应该是从技术上直接框定。  
  最好的沟通是避免沟通。  
  
### Stategen:前后端骨架代码生成器+Stategen架构（基于SpringMVC）+后端代码迭代生成器+前端代码生成器
spring(可选springboot)+springmvc+ibatis(mybatis2|可选mybatis3)+apache.dubbo(zookeeper|nacos)+react+antd(可选antd.mobile)+flutter(可选)

### 你目前的团队是不是有以下问题？如果有的话，可以尝试stategen(QQ群：728343119)
1. 前后端分离式导致前后端代码事实上脱节？ 
1. 前端还在用postman,swagger,mockit这些效率极低的测试工具?
1. 后端疲于撰写各api文档，而前端总是抱怨后端给的文档不完善？
1. 因为架构不成熟，临时方案填坑实则挖坑，重构又没时间？ 
1. 简易生成器，生成前备份代码，生成后手工同步还原代码，总有一天会导致人工疏忽？
1. 人员离开，留下的坑没人填？
1. 迭代牵一发而动全身，问题是到发布前，有哪里忘了动？
1. 业务代码中各种混杂，和程序员绑定关系？
1. 骨架代码中“调优”，和架构师绑定关系？
1. 微服务每次升级有大量不能自动检测的手写变更？
1. 越容易上手骨架，提高的可能性越低？
1. 从数据库到前后端代码的映射，各种变动不能全自动变更？
1. 各种隐式代码，各种隐式配置？
1. 发现迭代问题在测试阶段，时不时线上惊出一身冷汗，不能过早地在ide或编译阶段发现？
1. 人是越来越多，推进越来越慢？    

### 开发成本的构成
1. 开发业务代码的成本
2. 维护、迭代、重构的成本
3. 替换、升级框架中的某种技术的成本

成本越高，开发风险越大。往往，大多数框架/脚手架能大大节约（1）中个成本，但是同时带来几倍于（1）中的成本来维持（2）、（3）中，使开发陷入泥潭和死扣，stateGen要做的就是减少这种风险，所以我开发了StateGen。

### 增加一篇论文介绍原理:[利用java反射和java-parser制作可以迭代、分布式、全栈代码生成器的研究](https://github.com/stategen/stategen/blob/master/%E5%88%A9%E7%94%A8java%E5%8F%8D%E5%B0%84%E5%92%8Cjava-parser%E5%88%B6%E4%BD%9C%E5%8F%AF%E4%BB%A5%E8%BF%AD%E4%BB%A3%E3%80%81%E5%88%86%E5%B8%83%E5%BC%8F%E3%80%81%E5%85%A8%E6%A0%88%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8%E7%9A%84%E7%A0%94%E7%A9%B6.md)    
### 关于Ibatis or MyBatis or hibernate 和dalgenX
1.  SSH架构火了10年，其中hibernate支持自动生成sql的优势功不可没，但是再牛逼程序也不能满足复杂的sql自动生成,于是hibernate允许在java代码里掺杂hql和sql.
1.  上面的本意是给编程带来方便,但是一旦这个大门打开，就不能阻止开发人员进入，当java代码中混入大量的sql或hql后，项目离死也就不远了，除了难以维护，DBA也无法参与后期优化.
1.  当年，同期发展的ibatis被hibernate打得措手不及，由apache的顶级项目变成弃儿.但是正是ibatis的缺点：手写sql,解决了hibernate的先天不足，由是很多人在web2.0时代又重新转向了SSI架构。
1.  但是ibatis需要大写的手写配置，特别是xml配置,于是又出现了MyBatis(我个人认为改名的很大原因是应对apache版权),稍微减轻了一些手写配置的开发工作量。   
1. 后来又出现了MyBatis Plus（国人开发的？），不用写sql，但好像又回到了hibernate？噢耶！
1. 除非有StateGen中的dalgenX... 

### 市面上代码生成器分类
1. 一次性脚手架式：一次性生成/覆盖目标代码,用在项目初始化,如：maven初始化项目、各种网页配置生成。  
2. 持续脚手架式：分步生成/覆盖目标代码,如：MyBatis/Ibatis/Hibernate/ generator、dalgen,各种网页配置生成。  
3. 在已编辑的目标代码上再次生成。 如：StateGen(**全网目前唯一?**).
目前市面上的代码生成器都是前2种，而StateGen三种都有，StateGen开发生成器属于第3种.    
如果生成器只有前面2种，而为了不挖坑,要么只能做比较基本的生成，要么集成当时市面上所有时髦的技术，增加学习成本不说，
很快这些时髦都成为不时髦而且甩不掉的累赘，形成更大的坑，维护成本巨大。 
stategen采用第三种生成方式可以豪无限制地兼容其它技术，所以无需扯一些不需要的技术当噱头、还把挖坑还当卖点。 

### 开发人员对前端代码生成器的担忧
1.  很多crud前端代码生成器是基于后端配置的，也把很多公司拖到坑里，以致于一些前端开发人员一听到名称就害怕和抵触。
1.  原因是这些生成的前端代码没有遵从软件设计原则，也限死前端开发人员。而StateGen前端代码生成器开发目的是作为前端开发人员辅助工具，其中最重要的一条，始终遵守依赖倒置原则(DIP)，它给了开发人员最大的灵活度实现倒置部分代码，因此不会限死前端开发。
## 一：StateGen后端骨架代码初始化、StatGen架构的项目(springMVC，web3.0,也可再一键转换为springBoot)
1. 遵从常用架构设计原则（单一职责、开闭、接口隔离、无环依赖），通过在gen_config.xml配置,也可以将几个jar包合并。**觉得springMVC项目从头到尾只需一个jar可以不用往下看**。
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
2. 无限个web(7-...)，共同依赖相同的dao,遵循一个数据库只能有一套crud代码（**Martin大神的不重复规范**，支付宝框架的也是如此。实际开发中，当需求变了导致表变化时，假如有多套代码操作相同的表，那简直是找死，更何况假如有些代码还不属于当前开发管理,找人吧，开会吧，扯皮吧，最后拖死项目,据说有项目1天的工期因此排了半年），
1. 文件夹以数字形式开头自上而下1-,2-,3-...，方便在eclipse或idea中在项目结构上一目了然依赖关系（看到某大型商业架构dao跑到pojo的上面，每次打开项目都要花一定的时间开闭包原则，很不舒服）
1. 微服务放在war包里，多微服务依赖时，依赖服务可以一起放在java(如tomcat)容器中，调试和发布方便，不用单独启动。
1. facade对应的jar直接可以直接发布到本地公司仓库，它的远程配置自动生成，别的项目集成时，不需要写相关的配置，节约时间减少人工出错。
1. 除1-pojo，2-facade外，其它jar包都以snapshot结尾，进一步避免将有业务代码的jar包发到maven仓库中
1. 所有bean全部显式配置在xml中，项目进行很久管理上都不会乱
   骨架代码中的xml,涉及到bean,通常有2个,通常*-manual-*.xml里面内容为空（满足开发者自定义）,而auto不需要手工修改,每次新bean由dalgenX自动添加.
```
├── src/main/resources
│   ├── *-auto-*.xml
│   ├── *-manual-*.xml
```
1.  frontend采用git子项目管理的方式，迭代中接口和前端版本保持一致，后端可以直接输出相关前端api代码(我本人特烦给前端制作文档或者口头文档，1是文档同步很难，2是文档容易前后端扯皮，3是文档很花时间，与其扯皮不如直接给代码，stategen中的progen可以直接把后端api转换为前端相关的代码，一了百了，后端一行回车搞定前端相关的人工代码)
1. 5-service-impl是对5-service（继承自2-facade中的service)的全部实现，本地service从在本地找，不至于像一些煞笔框架一样也从微服务中找，
1. 2-facade中的service结尾可配置系统名（如UserSerivceTrade），这样可以清楚地理解是本地服务还是远程服务，服务出现问题也好定位或定位到项目owner,
1. 有些情况下，微服务中渴望用到继承(如User属于一个微服务中，Teacher 是当前开发的服务，是对User的拓展，这时继承远比共同复用原则合适)，环顾一下各大型系统好像都没有这么解决,不是写convertor就是用BeanUtils复制到DTO中，但这很容易在迭代中出现版本兼容问题。目前stategen中采用fastjson+dalgenX很好解决远程bean继承问题，fastjson也能保证转换效率,一些依赖jdk版本中AST信息的远程序列化比如kryo（我没深入研究，只是测试没通过）是有问题的，json则不存在。
1.  **stategen生成的后端骨架代码(springMVC)也可以一键转换为springboot**
1. 架构方案全部在springMVC的技术范畴中解决，是技术整合，不是创造技术（我个人认为在非底层上，所谓创造技术有涉嫌重复造轮子和挖坑的嫌疑，我是不会干的!）  
2. 
## 二、stategen对服务端代码的增强介绍
  Stategen要做的事，尽量地合理实现一个商业框架（不是开源后阉割版的那种）。一些过时的技术比如osgi摈弃，尽量在spring技术范围内解决。一些拓展点技术（非spring）我个人觉得对业务代码没有帮助而是挖坑（大牛一走，项目搞不下去了）
1.  @Wrap对返回值封装
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
        @Wrap(exclude=true)
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
8. 开关注册 dubbo服务
```xml
    <!-- 反注释 dubbo-provider-spring.xml 中的 -->
    <!-- <import resource="classpath*:context/dubbo-provider-auto-*.xml" /> -->    
    <!-- <import resource="classpath*:context/dubbo-provider-manual-*.xml" /> -->
```
    
9. 国际化...以后再讲，我觉得也很屌

## 三、  dalgenX后端代码生成器 vs 常用后端代码生成器，为什么要有dalgenX?
1. 一些通用orm生成器各有优缺点，带来方便也带来麻烦，**特别是二次迭代生成、对开发和线上都是灾难**，故统统不能达到我的要求。我个人觉得这其中最好的是支付宝的dalgen,由天才程序员**程立**博士(支付宝CTO/首席架构师,现阿里巴巴CTO，太有钱)开发。可惜，除支付宝外，外界知名度和使用率不高，可能是dalgen不开源吧.  
1. 直到2015年前我在taocode找到dalgen的freemarker简单开源实现，作者是**badqiu**. GMAVEN项目(可以通过简单的groovy语句直接调用)，我喜欢。
1. 上面的dalgen只能算demo,无法用于生产,但是思路完备，我结合工作中完善、改造升级，现在已很好地用于生产，实际使用效果比支持宝的dalgen还方便，特别是**支持迭代开发**，这是代码生成器史上质的飞跃。由是改名叫dalgenX,之所以没有用其它的名子，是向2位天才和前人致敬。     
1.  dalgenX和dalgen一样，以及本架构内的前端生成器、脚手架，都属于开发阶段生成器，只生成预期的目标代码，**不参与编译期和运行期，所见即所得，离开dalgenx生成后的项目也是完整的**，里氏替换原则都不需要。

1. 考虑到项目经常迭代导致表会更改，特意把一个表的sqls分成2个文件,xml文件中为开发手工书写的sql,没有或者没有或少量,xhtml为常用crud及一些便利的sqls,被引用在xml文件中, 以user表为例 user.xml引用user.xml.xhtml为作为自己的一部分，这样迭代时维护的代码量最小最安全。
```
trade
└── tables
│   └── user.xml
│   └── user.xml.xhtml
```
2. dalgenX支持ibatis和mybatis任选其一作为生成目标代码，需要说明是，ibatis(现在叫mybatis2)和mybatis3在github上分二条线同时一直都有官方维护，并不是mybatis3对mybatis2升级的关系，我个人以为mybatis3有一个不能接受的巨坑--ognl表达式，那是ssh特别流行的时候，mybatis急于想榜上struts2这条大腿，然后被struts2带沟里了。另外，mybatis3比mybatis2慢20%（我自己测过，不算权威数据）。
3. dalgenX采用与ibatis相似但比ibatis更为简单sql，一眼能看出效果，使用ibatis的isNotNull,isNotEmpty...等标签，当需要转换为mybatis3的ognl时，自动调用mybatis的官方转换规则转换.
```xml 
    <!-- gen_config.xml中 -->
    <!-- ibatis,mybatis,最下面覆盖上面，最下面优先 ，修改顺序后，需要重新运行一次 ./dalbatch.sh 批量生成-->
    <entry key="dao_type">mybatis</entry>
    <entry key="dao_type">ibatis</entry>
```
```xml
    <!-- user.xml中一条方法，这个是自动生成的，如果手工书写时，有提示帮助-->
    <!-- a.username 不需要写 小驼峰名称，以及jdbctype参数，
    不需要写返回值对照配置，由dalgenX生成User-sqlmap-mapping.xml自动生成 -->
    <operation name="getUserByUsername" multiplicity="one" remarks="">
        <sql>
           select
             a.user_id,
             a.username,
             a.password,
             a.role_type,
             a.name,
             a.nickName,
             a.inter_code,
             a.mobile,
             a.age,
             a.address,
             a.avatar_img_id,
             a.email,
             a.vali_datetime,
             a.birthday_date,
             a.work_time,
             a.province_id,
             a.city_id,
             a.status,
             a.grade,
             a.sex,
             a.post_address_id,
             a.remark,
             a.update_time,
             a.create_time,
             a.delete_flag
           from user a
           where
             a.delete_flag = 0
             and a.username = ?
        </sql>
    </operation>
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
4. **dalgenx支持水平权限**生成规则。水平权限要完全做到绕开暴力尝试,或者避免在别的api中泄露id被利用,显然，采用复杂id(uid、随机)生成方式治标不治本。同时，要兼顾代码速度、迭代、人员权限调整、下面简要地阐述一种水平权限方案，可以直接由dalgenX生成器来生成，大大降低开发成本，非常适合产品需求上的迭代，代码可以做到以不变应万变.
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
5. dalgenX兼顾避免一些开发习惯上的坑。比如
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
6. dalgenX在Service中生成一些符合合成/复用原则（CARP）的java代码，这种开发规范好理解也节约业务层大量开发
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


## 四 、前端生成器
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

## 六、 Stategen快速调试运行

#### 运行环境
>1.	服务端/windows
>>A.	java 1.8  
B.	maven 3  （3.5.0有bug,请使用3.5.2+）
C.	mysql5.7  
D.	gitbash(安装git2.0 自带)  
E.	nodejs8+yarn  
>2.	只开发前端/客户端   
>>A.	nodejs8+yarn｜andriod studio| flutter
  因为stg工程是git管理的， 前端是一个整个git项目的子项目

#### 开发环境安装
因项目依赖jar已经在maven中央仓库，只需要 git dalgenX即可
1.  配置 dalgenX
```
git clone https://github.com/stategen/dalgenx.git
```
>>设置 DALGENX_HOME 环境变量为 dalgenx所在目录  
>>将 %DALGENX_HOME% 添加至 PATH 中  
#### Ide中配置（eclipse\idea）
>>1.  location: {DALGENX_HOME}\gen.schemas-1.0.dtd  
key type: system Id  
key: https://github.com/stategen/dalgenx/blob/master/gen.schemas-1.0.dtd

#### 用命令初始化系统及项目/范例
1.  帮助
```
gen.sh -h
```
2.  创建系统骨架,注意，骨架|脚手架生成器重复运行，不会覆盖已有文件，但会补充不存在的文件
```
gen.sh system com.mycompany.biz trade -e  
```
>>com.mycompany.biz 为包名   
>>trade 系统名 /数据库名 dubbox 系统名    

3.  创建cms web 项目  (可选)
```
gen.sh project cms web –e  
```
>>cms 项目名称
>>web 以web(模版所在的文件夹生成前端) ，目前提供2个模版web|app，不要这个参数，即没有前端

4.  创建shedule项目,不带前端 (可选)	
```
gen.sh project schedule –e  
```

5.  创建app web api 项目  (可选)
```
gen.sh project app h5 –e  
// **视频里是旧版，gen.sh project app app –e ,要把后一个app改为h5 **
//再创建一个客户端 比如 flutter
gen.sh project app flutter –e 

//或者时入子目录
cd 7-trade-web-app
gen.sh client flutter -e

//把项目转换为sprintboot 
gen.sh boot -e

//变为git版本控制, trade 为git 项目，app-frontend-flutter为trade的子项目 
cd app-frontend-flutter
sh git_add_to_parent_as_sub.sh
```

6.	环境及表  
>> 创建trade数据库并运行 运行 trade.sql
>>把opt复制到同盘(tomcat所以盘)根目录下,修改stategen.xml中的数据库配置
>>修改gen_config.xml中的数据库配置  
>>因为一些文件可以用生成器获取，所以不在版本控制里，先后在 gitbatsh中运行 tablebatch.sh 和 dalbatch.sh  
```
./tablebatch.sh 
./dalbatch.sh 
```
8.  sourceTree查看,修改项目提交地址，提交  

#### 一个典型Stategen 系统 结构图
![Image](https://github.com/stategen/docs/blob/master/stg-fm-bbr.png) 


#### 演示Teacher需求开发 (一键开发，一键迭代,显式代码，所见即所得)
```
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `teacher_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '老师ID',
  `teacher_name` varchar(64) DEFAULT NULL COMMENT '老师名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码，测试，明文',
  `role_type` varchar(32) DEFAULT NULL COMMENT '老师角色 ADMIN,DEFAULT,DEVELOPER',
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
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

```
>1.运行命令生成表sql配置和java代码 (或者在6-${systemName}-web-base/test下运行 DevGennerator.java)
```
gen.sh table teacher –e
```
>2.检查teacher.xml对应的java类是否正确，去掉?及一行空格
```
gen.sh dal teacher –e
```
>3.F5刷新eclipse 检查import是否完整  
>4.手动做一个controller或者用命令初始化一个controller
```
gen.sh api teacher cms|app
```
>5.	eclipse打开查看是否有代码错误


#### 生成前端代码，开发前端
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
