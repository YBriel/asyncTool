package my;

import com.jd.platform.async.callback.ICallback;
import com.jd.platform.async.callback.IWorker;
import com.jd.platform.async.worker.WorkResult;
import com.jd.platform.async.wrapper.WorkerWrapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author： yuzq
 * Description:
 * Date: 2020/7/13   20:49
 */
public class TeacherWorker implements IWorker<String,String>, ICallback<String,String> {

    @Override
    public void begin() {
        System.out.println("老师的开始执行！");
    }

    @Override
    public void result(boolean success, String param, WorkResult<String> workResult) {
        System.out.println("老师success:"+success);
        System.out.println("老师param:"+param);
        System.out.println("老师workResult:"+workResult.toString());
        System.out.println();
    }

    @Override
    public String action(String object, Map<String, WorkerWrapper> allWrappers) {
        try {
            System.out.println("老师正在休眠...");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("老师休眠结束...");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("老师的结束执行！");
        return "teacher";
    }

    @Override
    public String defaultValue() {
        return "default teacher..";
    }
}
