##  最好的沟通是避免沟通   
![Image](https://github.com/stategen/docs/blob/master/stategenAppSnapshort.png)  
mobile端  
![Image](https://github.com/stategen/docs/blob/master/stategenWebSnapShort1.png)  
web端  
本说明视频演示请移步[Stategen快速调试开发运行精简教程](https://v.youku.com/v_show/id_XNDIxMzM4ODQzMg==.html?spm=a2h3j.8428770.3416059.1)  
视频中的相关文档，请见 https://github.com/stategen/docs    
>**stategen不是创造技术，而是对现有常用j2ee技术的框架级开放式整合**   
>**开放式意思是和别的技术做兼容，而不是使用stategen后限定使用其它技术**  
>**无论使用哪种整合，必不可少的是基本代码的开发，通过dalgenX代码生成器可以减少前后端50-90%的工作量**  
>**dalgenX不同于常用代码生成器，它是可迭代、开放式代码生成器，可以在现有代码的基础上随迭代再次生成,再不是覆盖生成，保留之前的开发成果**  
>**dalgenX可很好地解决DDD技术中领域模型/pojo的胖、瘦问题，避免大量DTO的编写**
>**最好的沟通是避免沟通,stategen可以避免大量后端、分布式、前后端文档编写，减少相互扯皮**

### Stategen快速调试运行

#### 运行环境
>1.	服务端/windows
>>A.	java 1.8  
B.	maven 3  
C.	mysql5.7  
D.	gitbash(安装git2.0 自带)  
E.	nodejs8+yarn  
>2.	单纯的前端/客户端开发  
>>A.	nodejs8+yarn  
  因为stg工程是git管理的， 前端是一个整个git项目的子项目

#### 开发环境安装
>1.	Install dubbox/或者发布dubbox至公司的私有仓库  
```
git clone https://github.com/dangdangdotcom/dubbox.git
mvn install –Dmaven.test.skip=true -e
```
>2.	Install stategen/或者发布stategen至公司的私有仓库
```
git clone --recursive https://github.com/stategen/stategen
mvn install –Dmaven.test.skip=true -e
```
(上面2步在有公司的私有maven仓库的情况下，只要发布一次就可以,并不是每台开发机上都要配置)
>3. 配置 dalgenX
```
git clone https://github.com/stategen/dalgenx.git
```
>>设置 DALGENX_HOME 环境变量为 dalgenx所在目录  
>>将 %DALGENX_HOME% 添加至 PATH 中  
#### Ide中配置（eclipse\idea）
>1.	location: {DALGENX_HOME}\gen.schemas-1.0.dtd  
key type: system Id  
key: https://github.com/stategen/dalgenx/blob/master/gen.schemas-1.0.dtd
>2.	location: {DALGENX_HOME}\dubbo.xsd    
key type: namespaces Name   
key: http://code.alibabatech.com/schema/dubbo/dubbo.xsd  


#### 用命令初始化系统及项目/范例
>1.创建系统
```
gen.sh system com.mycompany.biz trade -e  
```
>>com.mycompany.biz 为包名   
>>trade 系统名 /数据库名 dubbox 系统名    

>2.创建cms项目	
```
gen.sh project cms web –e  
```
>>cms 项目名称
>>web 以web(模版所在的文件夹生成前端) ，目前提供2个模版web|app，不要这个参数，即没有前端
	
>3.创建app项目	
```
gen.sh project app app –e  
```
>4.创建shedule项目,不带前端	
```
gen.sh project schedule –e  
```

>5.gitbash 中 运行  xxx-frontend目录下git_add_to_parent_as_sub.sh，变为git项目和子项目   
>6.sourceTree查看文件并提交  
>7.	环境及表  
>> 创建trade数据库并运行 运行 trade.sql，,city hoppy region province user_hoppy topic*都是演示表  
>>把opt复制到同盘根目录下  
>>修改gen_config.xml中的数据库配置  
>>先后在 gitbatsh中运行 tablebatch.sh 和 dalbatch.sh 批量生成缺失的文件  
>8.sourceTree查看文件并提交  

#### 一个典型Stategen 系统 结构图
![Image](https://github.com/stategen/docs/blob/master/stg-fm-bbr.png) 


#### 演示Teacher需求开发
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
>4.	fiddler 脚本设置 onBeforeRequest  
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
