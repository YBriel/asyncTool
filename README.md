# asyncTool

#### 介绍
解决任意的多线程并行、串行、阻塞、依赖、回调的并发框架，可以任意组合各线程的执行顺序，还带全链路回调和超时控制。

其中的A、B、C分别是一个最小执行单元（worker），可以是一段耗时代码、一次Rpc调用等，不局限于你做什么。

该框架，可以将这些worker，按照你想要的各种执行顺序，加以组合编排。最终得到结果。

并且，该框架 **为每一个worker都提供了执行结果的回调** 。譬如A执行完毕后，A的监听器会收到回调，带着A的执行结果（成功、超时、异常）。

根据你的需求，将各个执行单元组合完毕后，开始在主线程执行并阻塞，直到最后一个执行完毕。并且 **可以设置全组的超时时间** 。

 **该框架支持后面的执行单元以前面的执行单元的结果为自己的入参** 。譬如你的执行单元B的入参是ResultA，ResultA就是A的执行结果，那也可以支持。在编排时，就可以预先设定B或C的入参为A的result，即便此时A尚未开始执行。当A执行完毕后，自然会把结果传递到B的入参去。


![输入图片说明](https://images.gitee.com/uploads/images/2019/1225/130619_4d677c35_303698.png "屏幕截图.png")



#### 基本组件
worker：  一个最小的任务执行单元。通常是一个网络调用，或一段耗时操作

callBack：对每个worker的回调。worker执行完毕后，会回调该接口，带着执行成功、失败、原始入参、和详细的结果。

```
/**
 * 每个执行单元执行完毕后，会回调该接口</p>
 * 需要监听执行结果的，实现该接口即可
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface ICallback<T, V> {

    void begin();

    /**
     * 耗时操作执行完毕后，就给value注入值
     *
     */
    void result(boolean success, T param, WorkResult<V> workResult);
}

```

wrapper：组合了worker和callback，是一个最小的调度单元。通过编排wrapper之间的关系，达到组合各个worker顺序的目的。

```
        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);
```

如

![输入图片说明](https://images.gitee.com/uploads/images/2019/1225/132251_b7cfac23_303698.png "屏幕截图.png")
    
  0执行完,同时1和2, 1\2都完成后3。3会等待2完成
  

#### 安装教程

代码不多，直接拷贝包过去即可。

#### 使用说明

1.  3个任务并行

```
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(800, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(1000, workerWrapper, workerWrapper1, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.out.println(getThreadCount());

        System.out.println(workerWrapper.getWorkResult());
//        System.out.println(getThreadCount());
        Async.shutDown();
```



2.  1个执行完毕后，开启另外两个，另外两个执行完毕后，开始第4个

```
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(2000);
        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);
        //3会超时
//        Async.beginWork(3100, workerWrapper);
        //2,3会超时
//        Async.beginWork(2900, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
```


3.  其他的详见test包下的测试类，支持各种形式的组合、编排。
