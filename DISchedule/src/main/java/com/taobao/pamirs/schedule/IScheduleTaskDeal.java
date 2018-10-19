package com.taobao.pamirs.schedule;

import java.util.Comparator;
import java.util.List;


/**
 * 调度器对外的基础接口
 * @author xuannan
 *
 * @param <T> 任务类型
 */
public interface IScheduleTaskDeal<T> {

/**
 * 根据条件，查询当前调度服务器可处理的任务	
 * @param taskParameter 任务的自定义参数
 * @param ownSign 当前环境名称 
 * @param taskItemNum 当前任务类型的任务队列数量
 * @param taskItemList 当前调度服务器，分配到的可处理队列
 * @param eachFetchDataNum 每次获取数据的数量
 * @return
 * @throws Exception
 */
public List<T> selectTasks(String taskParameter,String ownSign,int taskItemNum,List<TaskItemDefine> taskItemList,int eachFetchDataNum) throws Exception;

/**
 * 获取任务的比较器,主要在NotSleep模式下需要用到
 * @return
 */
public Comparator<T> getComparator();

	/**
	 * @Description: 执行任务前置处理器
	 * @author wangxiaohu wsmalltiger@163.com
	 * @param jobName 任务名称
	 * @param taskParameter 任务参数
	 * @param ownSign 所有者
	 * @param taskItemNum 任务项个数
	 * @param taskItemList 任务项集合
	 * @param eachFetchDataNum 每次获取量
	 * @return 任务id
	 * @throws
	 */
	public Long beforeTask(String jobName, String taskParameter,
			String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList,
			int eachFetchDataNum);

	/**
	 * @Description: 任务后置处理器
	 * @author wangxiaohu wsmalltiger@163.com
	 * @param id 任务id
	 * @param getListSize 获取到集合大小
	 * @throws
	 */
	public void afterTask(Long id, Integer getListSize);

	/**
	 * @Description: 发生异常回调
	 * @author wangxiaohu wsmalltiger@163.com
	 * @param id 任务id
	 * @param exception 异常对象
	 * @throws
	 */
	public void onException(Long id, Throwable exception);

}
