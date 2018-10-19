package com.taobao.pamirs.schedule.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.TaskItemDefine;

public abstract class IScheduleTaskDealSingleBase<T> implements
		IScheduleTaskDealSingle<T> {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private long startTime = 0l;
	private long id = 0;

	public Long beforeTask(String jobName, String taskParameter,
			String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList,
			int eachFetchDataNum) {
		synchronized (this) {
			id = id + 1;
			startTime = System.currentTimeMillis();
			System.out.println(sdf.format(new Date()) + "\t开始任务：" + id + "=="
					+ "   " + Thread.currentThread().getName() + "   "
					+ jobName + "   " + taskParameter + "==" + ownSign + "=="
					+ taskItemNum + "==" + taskItemList + "=="
					+ eachFetchDataNum);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return new Long(id);
		}
	}

	public void setLogId(Long id) {
	}

	public void afterTask(Long id, Integer getListSize) {
		Date date = new Date();
		long l2 = System.currentTimeMillis();
		System.out.println(sdf.format(date) + "   " + "\t调度结束：" + id + "   "
				+ Thread.currentThread().getName() + "   " + "  耗时：  "
				+ (l2 - startTime));
	}

	public void onException(Long id, Throwable exception) {
		System.out.println(sdf.format(new Date()) + "   " + "\t发生异常：" + id
				+ "   " + Thread.currentThread().getName() + "   "
				+ "   |||||||  " + exception.getMessage());
	}

}
