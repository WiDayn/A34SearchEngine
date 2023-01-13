package cn.edu.hhu.a34searchengine.util;

public class Timer
{
    private long start;

    public Timer()
    {
        StackTraceElement caller=Thread.currentThread().getStackTrace()[2];
        System.out.println("Timer STARTED at " + caller);
        start=System.currentTimeMillis();
    }

    public void start()
    {
        StackTraceElement caller=Thread.currentThread().getStackTrace()[2];
        System.out.println("Timer STARTED at " + caller);
        start=System.currentTimeMillis();
    }

    public void stop()
    {
        long duration=System.currentTimeMillis()-start;
        System.out.println("Timer STOPPED at " + Thread.currentThread().getStackTrace()[2]+ " duration:"+duration+"ms");
    }
}
