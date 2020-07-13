package my;

import com.jd.platform.async.executor.Async;
import com.jd.platform.async.wrapper.WorkerWrapper;
import dependnew.User;

import java.util.concurrent.ExecutionException;

/**
 * Author： yuzq
 * Description:
 * Date: 2020/7/13   20:54
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {



    PeopleWorker peopleWorker=new PeopleWorker();
    StudentWorker studentWorker=new StudentWorker();
    TeacherWorker teacherWorker=new TeacherWorker();

    WorkerWrapper<String, String> wrapper1 = new WorkerWrapper.Builder<String, String>()
            .worker(peopleWorker)
            .callback(peopleWorker)
            .build();

    WorkerWrapper<String, String> wrapper2 = new WorkerWrapper.Builder<String, String>()
            .worker(studentWorker)
            .callback(studentWorker)
            .build();

    WorkerWrapper<String, String> wrapper3 = new WorkerWrapper.Builder<String, String>()
            .worker(teacherWorker)
            .callback(teacherWorker)
            .build();

    Async.beginWork(2000,wrapper1,wrapper2,wrapper3);
//    Async.beginWork(2000,wrapper2);
//    Async.beginWork(2000,wrapper3);

    Async.shutDown();
    }


}
