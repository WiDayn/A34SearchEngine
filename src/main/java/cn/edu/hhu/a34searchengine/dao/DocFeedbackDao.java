package cn.edu.hhu.a34searchengine.dao;

public interface DocFeedbackDao
{

    void increaseExtractionCount(long docUUID);

    void increaseVisitCount(long docUUID);

    void increasePreference(long docUUID);

    void decreasePreference(long docUUID);
}
