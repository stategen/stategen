## Stategen 
  ***后端做业务，前端做美工，其它交给stategen来做***    
  ***整合技术不创造技术***
### 介绍
  * 服务端 客户端 代码自动化 框架 ,兼顾 服务端开发，客户端开发，运维，发布，迭代，
  各部门沟通成本，能将开发成本减少到原来的1/5-1/10
  * 服务端使用到的技术 springmvc ibatis dalgenx progen dubbox
  * 客户端使用到的技术 react dedux saga dva umi antd typescript
  * 开发流程：后端-&gt;sql-&gt;dalgenX代码生成-&gt;业务代码介入-&gt;自动dubbo服务提供或引入-&gt;api-&gt;progen生成客户端rpc代码(apis,beans,enums,interfaces,forms代码片段)
  -&gt;前端实现界面美化-&gt;运维编译-&gt;发布->ssr
  
### 开始准备和demo
  * 准备工具(windows上:Gow(.sh脚本用到了linux一些命令,不想为相似的代码写2遍) ,git bash)
  * hosts 中添加 如:   
              192.168.112.127 gitlab  
              192.168.112.128 nexus  
              其中 gitlab指公司代码git托管服务器地址,nexus提maven你公司仓库地址
  * 下载 stategen源码: 
     ```
     git clone --recursive https://github.com/stategen/stategen
 
  *  执行命令将stategen framework发布至你公司maven仓库  
     ````
     mvn clean deploy -Dmaven.test.skip=true
  *  如果只开发业务代码，直接拉dalgenX即可
     ```
     git clone https://github.com/stategen/dalgenx  
  *  环境变量添加 
     ````
     DALGENX_HOME=D:\works\fm\stategen-git\f-dalgenx
  *  同时将 DALGENX_HOME添加至 path中,以便任意在命令窗口 找得到 gen.sh
  *  执行命令生成系统 
     ```
     gen.sh system com.mycompany.biz trade -e 
  *  其中 com.mycompany.biz 指的是java package的名称 ,trade 是你的系统总称,一个stategen系统支持无限项目(后台
     管理系统,API,调度任务，客户后台...)
     执行完会得到如下结构(类似于支付宝sofa框架构,但我觉得比sofa简便，不需要手工配rpc服务):
  ```bash
    trade
    ├── 1-trade-pojo         # 系统中的基本类，bean enum在这里 ，执行deploy会发到maven仓库中
    ├── 2-trade-facade       # 对外提供dubbo服务jar包，执行deploy会发到maven仓库中
    ├── 3-trade-intergrade   # 引用别的dubbox服务,只需要引入相应的jar包即可,stategen对dubbo服务是自适应的,不需要写调用代码。执行deloy时该包及以下包均不会发布到maven仓库
    ├── 4-trade-dao          # 系统中的dao及配置
    ├── 5-trade-service      # 系统中的service,实现继承facade服务和自有的serivce 
    ├── 6-trade-web-base     # 系统中的api base,stagegen项目以该包向下拓展
  ```  
* 执行命令生成项目
  ```
    gen.sh project cms w -e 
* 其中cms(后台管理)指的项目的名项，以springmvc对外提供api http或https服务 w 指的按web模版生成
    
  ```bash
      trade     # 这个是一个git 项目，可以
      ├── 1-trade-pojo         
      ├── 2-trade-facade       
      ├── 3-trade-intergrade   
      ├── 4-trade-dao          
      ├── 5-trade-service      
      ├── 6-trade-web-base     
      ├── 7-trade-web-cms
      ├────cms-frontend  #这是一个 git submodue,在当前目录下执行 ./git_add_to_parent_as_sub.sh 也可手动添加至trade-git中,如需发布让服端单独开发 需置gitlab地址,只前端开始直接拉这个git地址就可以
* 开发前，把dalgenx中2个xml模版配到eclipse或idea中，更于开发
  ```
    ....\gen.schemas-1.0.dtd System Id http://sources.alipay.net/svn/dtd/table-config-1.0.dtd
    ... \dubbo.xsd Namespaces Name http://code.alibabatech.com/schema/dubbo/dubbo.xsd
  
* 每执行一次生成一个项目如
  ```
   gen.sh project app m -e  
*  (m指的是mobile模版,还在制作中，没上传，先不要执行) 如下结构
  ```bash
     trade   
     ├──  ...
     ├── 7-trade-web-app
     ├────app-frontend
  ``` 
 * 把项目中的 中的 trade.sql（和系统名对应，系统叫什么，它就叫什么） 拖到mysql服务器上运行,并创建一个用户，stategen,密码也是 stategen，当然这些可以改为你现有的用户名和密码，  
    /opt/config/stategen/stategenX.xml指的运行环境中的配置，类似于sofa中antX.xml ,    
    gen_config.xml指的是程序员开发中dalgenX运行需要的配置
 * 将 trade下的 opt目录复制到java容器的根目录下,配一些sql,zookeeper,redis,dubbo 地址，开发服务器配开发地址，测试服务器配测试地址，生产配生产，系统运行需要读取这些信息，
   而不是放在war包内,做到一包多用，避免不同环境需要打不同包，减少人为疏忽，这样也可以自动化让运维托管
 * 
   ```
     mvn clean deploy -Dmaven.test.skip=true 
 *  就可以生成相应的war包，拖到相应的平台服务器的java容器（tomcat,jboss...为啥不用springboot呢，我没觉得springboot有啥好处）中就可以，建议配在 jekins中，实现自动化发布  
### 开发或迭代
 * 在sql中设计表，如order_master , 表中必须有 **update_time或 gmt_update字段** ，建议有delete_flag和 create_date或gmt_create字段
 * 执行 
   ```
    gen.sh table order_master -e 
 *  生成 
   ```bash
      trade   
      ├──  ...
      ├── tables
      ├─────order_master.xml
   ``` 
  * 把 order_master.xml打开，把 ? 和一行文字去掉，这主要是 实际开发中，开发人员命名表不规范，而windows 对大小写不敏感,导致svn或git冲突 ,
     当然你公司开发人员命名表非常规范后，也可以在 gen_config.xml配置将该警告去除就不会以下警告  
     ```
      <table sqlName="order_master" className="?OrderMaster">  
      ↑请检查上面className是否正确,按驼峰写法修改类名，将非法字符"?"去掉,并删除本行↑  
     ``` 
  *  变为   
     ```
      <table sqlName="order_master" className="OrderMaster>  
  * 接下来执行 
    ``` 
      gen.sh dal order_master -e  
  * dalgenX 会生成   
      OrderMaster.java,   
      OrderMasterServer.java,   
      OrderMasterServerFacade.java(里面是空的也即空服务,需要对外提供dubbo服务将相应的sql标为facade或手动从OrderMasterServer.java中服制过去),   
      OrderMasterServerImpl.java,    
      OrderMasterControllerBase.java,    
      dubbo服务,以及相应的配置文件.    
  * 和支付宝的dalgen或市面上代码生成器不同是， dalgenX可以迭代开发,也就是你的bean,service可以继承自别的接口或类java标注，也可以添加
