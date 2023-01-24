package cn.edu.hhu.a34searchengine.dao.impl;

import cn.edu.hhu.a34searchengine.dao.DocFeedbackDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocFeedbackDaoImpl implements DocFeedbackDao
{

    @Qualifier("ForDocFeedback")
    @Autowired
    RedisTemplate<String,Object> redisTemplate;


    @Value("${redis.doc-feedback.key-prefix.extraction-count}")
    private String extractionCountPrefix;

    @Value("${redis.doc-feedback.key-prefix.visit-count}")
    private String visitCountPrefix;

    @Value("${redis.doc-feedback.key-prefix.preference}")
    private String preferencePrefix;

    @Value("${redis.doc-feedback.key-prefix-separator}")
    private String separator;


    @Override
    public void increaseExtractionCount(long docUUID)
    {
        redisTemplate.opsForValue().increment(extractionCountPrefix + separator + String.valueOf(docUUID));
    }

    @Override
    public void increaseVisitCount(long docUUID)
    {
        redisTemplate.opsForValue().increment(visitCountPrefix + separator + String.valueOf(docUUID));
    }

    @Override
    public void increasePreference(long docUUID)
    {
        redisTemplate.opsForValue().increment(preferencePrefix + separator + String.valueOf(docUUID));
    }

    @Override
    public void decreasePreference(long docUUID)
    {
        redisTemplate.opsForValue().decrement(preferencePrefix + separator + String.valueOf(docUUID));
    }
}
