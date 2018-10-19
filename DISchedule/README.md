# DISchedule
对[TBSchedule][1]分布式任务调度进行了简单改造；

前段时间由于工作需要，简单研究了下tbschedule。发现其功能不错，但是真正用起来功能还是有点欠缺：

- **日志无法与现有项目相结合**
- **持续需job数据时，不支持定期执行任务（quatz），只能在某个时间段内执行job**
- **job执行完毕之后，没有回调方法**
- **运行时发生异常之后，没有提供异常处理接口**

-------------------

>通过对源码简单改造，实现了上述功能。此处只有SLEEP模式的改造代码，NOTSLEEP模式未进行改造。代码进行过简单测试，由于自身业务需求，对多线程组、多线程模式下测试过少。


改造介绍：[http://blog.csdn.net/wsmalltiger/article/details/44456891][2]

>执行定期任务，需要将**每次处理完数据后休眠时间(秒)**配置为：负数（例如：-1）


[1]: http://code.taobao.org/p/tbschedule/wiki/index/
[2]: http://blog.csdn.net/wsmalltiger/article/details/44456891
