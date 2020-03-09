##  一切为了迭代!!!  
spring(可选springboot)+springmvc+ibatis(mybatis2|可选mybatis3)+apache.dubbo(可选)+react+antd(可选antd.mobile)+flutter(可选)
**全栈**骨架生成器+一键迭代开发生成器

应用一个架构/生成器的成本应包括几个部分  

1. 开发业务代码的成本
2. 维护、迭代、重构的成本
3. 替换、升级框架中的某种技术的成本

成本越高，开发风险越大。往往，大多数框架/脚手架能大大节约（1）中个成本，但是同时带来几倍于（1）中的成本来维持（2）、（3）中，使开发陷入泥潭和死扣，statege要做的就是解决这种风险。

### 增加一篇论文介绍原理:[利用java反射和java-parser制作可以迭代、分布式、全栈代码生成器的研究](https://github.com/stategen/stategen/blob/master/%E5%88%A9%E7%94%A8java%E5%8F%8D%E5%B0%84%E5%92%8Cjava-parser%E5%88%B6%E4%BD%9C%E5%8F%AF%E4%BB%A5%E8%BF%AD%E4%BB%A3%E3%80%81%E5%88%86%E5%B8%83%E5%BC%8F%E3%80%81%E5%85%A8%E6%A0%88%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8%E7%9A%84%E7%A0%94%E7%A9%B6.md)    

#### 市面上代码生成器分类  
1. 一次性脚手架式：一次性生成/覆盖目标代码,用在项目初始化,如：maven初始化项目、各种网页配置生成。  
2. 持续脚手架式：分步生成/覆盖目标代码,如：MyBatis/Ibatis/Hibernate/ generator、dalgen,各种网页配置生成。  
3. 在已编辑的目标代码上再次生成。 如：StateGen(全网目前唯一?).
目前市面上的代码生成器都是前2种，而StateGen三种都有，StateGen开发生成器属于第3种.    
如果生成器只有前面2种，而为了不挖坑,要么只能做比较基本的生成，要么集成当时市面上所有时髦的技术，增加学习成本不说，
很快这些时髦都成为不时髦而且甩不掉的累赘，形成更大的坑，维护成本巨大。 
stategen采用第三种生成方式可以豪无限制地兼容其它技术，所以无需扯一些不需要的技术当噱头、还把挖坑还当卖点。  

####关于dalgenX   

1. 一些通用orm生成器各有优缺点，带来方便也带来麻烦，特别是二次生成、对开发和线上都是灾难，故统统不能达到我的要求。我个人觉得这其中最好的是支付宝的dalgen,由天才程序员**程立**博士(支付宝CTO/首席架构师,现阿里巴巴CTO)开发。可惜，除支付宝外，知名度不高，而且dalgen不开源.  
1. 直到2015年前我在taocode找到dalgen的freemarker简单开源实现，作者是**badqiu**. 
1. 上面的dalgen只能算demo,无法用于生产,但是思路完备，我结合工作中完善、改造升级，现在已很好地用于生产，实际使用效果比支持宝的dalgen还方便很多，特别是**支持迭代开发**，这是代码生成器史上质的飞跃。由是改名叫dalgenX,之所以没有用其它的名子，是向2位天才和前人致敬。     
1.  dalgenX和dalgen一样，以及本架构内的前端生成器、脚手架，都属于开发阶段生成器，只生成预期的目标代码，不参与编译期和运行期， 
## StateGen构成:
###后端及全栈道架构   

1.  spring|springboot|ibatis|mybatis|dubbo 
1. ​ 几种技术可以挑选，全部采用显示bean,离开架构师也能看得懂的架构 
1.  每个web项目可以支持0-n个前端，前端为后端的git子项目。
### 关于前端生成器及原理
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


### 关于后端生成器
   >1. 对于任意一个sql,其配置、对应的java代码，参数个数，类型，返回值类型、字段都是确定的，这些以前都是手工或半手工撸出来.
   >2. 业务逻辑都是由调用一个或多个sql组成的
   >3. 市面上代码生成器都覆盖式生成代码,其生成的代码无法预先指定继承、实现接口、类型指定,无法保护已有业务代码成果,
       比如要多写功能相同逻辑的DTO绕开限制，但这违背单一职责框架设置原理，欲使用先入坑.StateGen采用解析已有java
       代码的方式解决上述问题
 ### 代码生成器难以解决的是问题迭代和增量开发,但是实际的项目开发都是不断地迭代功能和新功能叠加，而stategen非常适合迭代开发    

# StateGen已经支持flutter   
  采用google 2019 i/o大会上推荐的provider
##  最好的沟通是避免沟通   
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
**上面2张图，按通常的开发量需要上千行代码，现在只需要开发10多行代码**  
本说明视频演示请移步[Stategen快速调试开发运行精简教程](https://v.youku.com/v_show/id_XNDIxMzM4ODQzMg==.html?spm=a2h3j.8428770.3416059.1)  
视频中的相关文档，请见 https://github.com/stategen/docs    

### Stategen快速调试运行

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
>1.运行命令生成表sql配置和java代码
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


#### 打包 前后端
```
mvn package 
```

### 其它
#### 开启 dubbox, 
>反注释 dubbo-provider-spring.xml 中的
```
    <!-- <import resource="classpath*:context/dubbo-provider-auto-*.xml" /> -->
    <!-- <import resource="classpath*:context/dubbo-provider-manual-*.xml" /> -->
```
### Stategen中 spring bean都是显示配置的 
### StategenMvc框架几处用法,可以避免在业务中频繁地写与业务无关的代码，这些用法是自定义配置的
#### MultiFilter.java 配置在web.xml，它对客户端的请求CookieGroup检测是否伪造,CookieGroup可以配置多个,便于业务分组.每个CookieGroup下的所有cookie都有一个离伪造的token签名，
```
<filter>
    <filter-name>frameworkMultiFilter</filter-name>
    <filter-class>org.stategen.framework.spring.mvc.MultiFilter</filter-class>
</filter>
```
```
<!-- 验证cookie分组 ,该类可以多个，配置不同的分组 -->
<bean id="loginCookieGroup" class="org.stategen.framework.web.cookie.CookieGroup">
    <property name="cookieTypeClz" value="com.mycompany.biz.enums.CookieType.Login" />
    <property name="httpOnly" value="${loginCookieGroupHttpOnly}"/>
</bean>
```

#### @Wrap 对Spring Controller中@Requestbody 返回值按指定的类再包装,便于前端兼容
比如:
```
    <bean id="response" class="com.mycompany.biz.domain.Response" scope="prototype">
    </bean>
```
包装后的
```
{
message:'成功'
success:true,
data:xxxx
...
}
```

#### CollectExceptionJsonHandler.java 对业务异常、运行异常统一处理
```
    <bean class="org.stategen.framework.spring.mvc.CollectExceptionJsonHandler">
        <property name="responseStatusClzOfException" value="com.mycompany.biz.enums.ResponseStatus.ERROR" />
    </bean>
```

####各种自定义的标注如 @LoginCheck .... 按指定的方式检查权限
```
    <bean class="com.mycompany.biz.checker.VisitChecker" />
    <bean class="com.mycompany.biz.checker.LoginChecker" />
```

####@ApiRequestMappingAutoWithMethodName
在SpringMvc,以方法名作为路径，避免写路径硬编码，

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
### 鸣谢
   [react] https://github.com/facebook/react，   
   [ant-design] https://github.com/ant-design/ant-design   
   [dva] https://github.com/dvajs/dva   
   [umi] https://github.com/umijs   
   [rapid-framework] https://github.com/badqiu/rapid-framework    
   [zuiidea] https://github.com/zuiidea/antd-admin   
   [dubbox] https://dangdangdotcom.github.io/dubbox   
   [spring-framework] https://github.com/spring-projects/spring-framework   
   ...
