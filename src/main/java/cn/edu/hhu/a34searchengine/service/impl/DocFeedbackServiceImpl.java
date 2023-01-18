package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.DocFeedbackDao;
import cn.edu.hhu.a34searchengine.service.DocFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocFeedbackServiceImpl implements DocFeedbackService
{

    @Autowired
    DocFeedbackDao docFeedbackDao;
    public void increaseExtractionCount(long docUUID)
    {
        docFeedbackDao.increaseExtractionCount(docUUID);
    }

    @Override
    public void increaseVisitCount(long docUUID)
    {
        docFeedbackDao.increaseVisitCount(docUUID);
    }

    @Override
    public void increasePreference(long docUUID)
    {
        docFeedbackDao.increasePreference(docUUID);
    }

    @Override
    public void decreasePreference(long docUUID)
    {
        docFeedbackDao.decreasePreference(docUUID);
    }

}
