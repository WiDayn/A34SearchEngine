package cn.edu.hhu.a34searchengine.util;

import org.springframework.beans.factory.annotation.Value;

public class SnowFlake {
    // 因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。

    @Value("${settings.worker-id}")
    private final long workerId;

    @Value("${settings.datacenter-id}")
    private final long datacenterId;

    private long sequence;

    private final long workerIdBits = 5L;

    private final long datacenterIdBits = 5L;

    private long lastTimestamp = -1L;

    /**
     * 构造函数
     * @param workerId 机器ID
     * @param datacenterId 机房ID
     * @param sequence 代表一毫秒内生成的多个id的最新序号
     */
    public SnowFlake(long workerId, long datacenterId, long sequence){

        // 检查机房id和机器id是否超过最大值，不能小于0
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId> maxWorkerId ||workerId<0){
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        long maxDatacenterId = ~(-1L << datacenterIdBits);
        if (datacenterId> maxDatacenterId ||datacenterId<0){
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.workerId=workerId;
        this.datacenterId=datacenterId;
        this.sequence=sequence;
    }

    /**
     * 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
     * @return 全局唯一的id
     */
    public synchronized long nextId(){
        // 获取当前的时间戳，单位是毫秒
        long timestamp=timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp<lastTimestamp){
            System.err.printf("clock is moving backwards. Rejecting requests until %d.",lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",lastTimestamp-timestamp));
        }

        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把sequence序号给递增1，最多就是4096
        long sequenceBits = 12L;
        if (lastTimestamp == timestamp) {
            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            long sequenceMask = ~(-1L << sequenceBits);
            sequence = (sequence + 1) & sequenceMask;
            //当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else{ // 时间戳改变，毫秒内序列重置
            sequence=0;
        }

        //上次生成ID的时间截
        lastTimestamp=timestamp;

        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        long twepoch = 1585644268888L;
        long datacenterIdShift = sequenceBits + workerIdBits;
        long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        return ((timestamp- twepoch)<< timestampLeftShift)|
                (datacenterId<< datacenterIdShift)|
                (workerId<< sequenceBits)|sequence;
    }

    /**
     * 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp=timeGen();
        while(timestamp<=lastTimestamp){
            timestamp=timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
