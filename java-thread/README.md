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
### 2.3 yield
A hint to the scheduler that the current thread is willing to yield its current use of a processor. The scheduler is free to ignore this hint.
### 2.4 interrupt
+ 线程结束或者还未开始: isInterrupted＝false
+ sleep: InterruptedException – if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown.
+ 处于interrupted状态的线程从非阻塞态进入阻塞态也会触发InterruptedException
+ queue.take不一定会进入阻塞状态

## 3. ThreadLocal / InheritableThreadLocal
### 3.1 ThreadLocalMap

## 4. 线程池
### 4.1 线程池参数
### 4.2 拒绝策略
### 4.3 线程池工作原理
### 4.４ 线程池的坑--异常
+ Future
+ afterExecute

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
