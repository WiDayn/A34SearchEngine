package cn.edu.hhu.a34searchengine.dao.impl;


import cn.edu.hhu.a34searchengine.dao.PDFCacheDao;
import cn.edu.hhu.a34searchengine.pojo.PDFData;
import cn.edu.hhu.a34searchengine.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;

//缓存分页后的pdf,由于对pdf分页极其耗时,故分页异步进行,在前端点击某个pdf查看详情时分页,这样点击某页详情时,分页的pdf大概率已经做好了
@Repository
public class PDFCacheDaoImpl implements PDFCacheDao
{

    @Autowired
    RedisTemplate<Long, Object> redisTemplate;

    @Value("${setting.PDFData.expireTimeoutMinutes}")
    long expireTimeout;

    @Override
    public boolean isAvailable(long pdfUUID)
    {
        return Boolean.TRUE.equals(redisTemplate.hasKey(pdfUUID)) && redisTemplate.opsForValue().getOperations().getExpire(pdfUUID)>0;
    }

    @Override
    public void add(long pdfUUID, PDFData data)
    {
        Timer timer=new Timer();
        boolean isOK= Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(pdfUUID, data, Duration.ofMinutes(expireTimeout)));
        if(!isOK)           //不ok说明键已经存在,且这个数据近期访问比较频繁, 此时需要延长过期时间
        {
            redisTemplate.expire(pdfUUID,Duration.ofMinutes(expireTimeout));  //刷新过期时间
        }
        timer.stop();
    }

    @Override
    public PDFData get(long pdfUUID)
    {
        Timer timer=new Timer();
        PDFData pdfData = (PDFData) redisTemplate.opsForValue().get(pdfUUID);
        if(pdfData!=null)
            redisTemplate.expire(pdfUUID,Duration.ofMinutes(expireTimeout));    //有人访问说明这个数据近期访问比较频繁,刷新过期时间
        timer.stop();
        return pdfData;     //为null说明已经过期删除,service层需要再次将其放入缓存
    }
}
