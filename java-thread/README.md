# JAVA THREAD
## 1. 线程状态
```text
    @see java.lang.Thread.State
```
```text
    https://www.cnblogs.com/gyjx2016/p/11168273.html
    1. 查看进程：
        linux: ps -ef|grep java / top
        java: jps -lmv
    2. 查看线程：
        linux：top -Hp 1999 (10进制转16进制：printf "%x" 1999)
        windows:process explore(工具)
    3. 线程栈：
        jstack -l 1999
    4. jdk工具包：
        jmc
```

## 2. 线程操作
### 2.1 join / sleep

### 2.2 dumpStack
适用于本地调试

### 2.3 yield
A hint to the scheduler that the current thread is willing to yield its current use of a processor. The scheduler is free to ignore this hint.
告诉线程调度器，当前线程愿意释放cpu控制权。调度器可以忽略该提示。
### 2.4 interrupt
+ 线程结束或者还未开始: isInterrupted＝false
+ sleep: InterruptedException – if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown.
线程抛出InterruptedException即会清除interrupted状态。
+ 处于interrupted状态的线程从非阻塞态进入阻塞态也会触发InterruptedException
+ queue.take不一定会进入阻塞状态

一个优雅的死任务大概是这样的:

```java
	Thread thread = new Thread(() -> {
		while (!Thread.currentThread.isInterrupted()) {
			// do work.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
			}
		}
	})
	thread.start();
	
	// 中断
	thread.interrupt();
	
	// jvm使用钩子中断
	Runtime.getRuntime().addShutdownHook(new Thread(() => thread.interrupt();));
```

## 3. ThreadLocal / InheritableThreadLocal
### 3.1 ThreadLocalMap
java 四大引用：
+ 强
+ 软:一般适用于缓存,和jvm设置有关
+ 弱:一般适用于缓存,gc即毁
+ 虚:一般无需关注，gc相关

```java
	ThreadLocal t1 = new ThreadLocal();
	ThreadLocal t2 = new ThreadLocal();
	
	t1.set("1");
	t2.set("kafka");
	
	// 此时，ThreadLocal数据结构大概是这样的：
	Thread.property{ThreadLocal.ThreadLocalMap}.key[t1] = "1";
	Thread.property{ThreadLocal.ThreadLocalMap}.key[t2] = "kafka";
```

## 4. 线程池
### 4.1 线程池参数
核心线程数，最大线程数，非核心线程的不工作时间，阻塞队列，拒绝策略，线程工厂(处理线程名字，守护，优先级)
### 4.2 拒绝策略
+ 去掉头部（最古老元素）
+ 去掉尾部（当前任务忽略）
+ 抛出异常
+ 提交任务的线程执行

### 4.3 线程池工作原理
+ 1. 使用核心线程
+ 2. 核心线程满了，放入队列
+ 3. 队列满了，使用非核心线程
+ 4. 队列满了，非核心线程也启用了，使用拒绝策略

attention: 只有队列满了才会启用非核心线程，如果使用无限队列(比如LinkedBlockingQeque(Integer.MAX_VALUE))，那么非核心线程理论上是不可能被启用的。

### 4.４ 线程池的坑--异常
+ Future
future.get()会抛出ExecutionException，自行处理
+ afterExecute
重写afterExecute(Runnable r, Throwable t)来对线程池的异常做全局处理

## 5. 实践
```text
三个线程轮流打印数字：
线程一：1,2,3
线程二：4,5,6
线程三：7,8,9
线程一：10,11,12
线程二：13,14,15
线程三：16,17,18
......
各打印十次
```
