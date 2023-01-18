package cn.edu.hhu.a34searchengine.service;

public interface DocFeedbackService
{

    void increaseExtractionCount(long docUUID);

    void increaseVisitCount(long docUUID);

    void increasePreference(long docUUID);

    void decreasePreference(long docUUID);
}
