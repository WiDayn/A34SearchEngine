package cn.edu.hhu.a34searchengine.controller;

import cn.edu.hhu.a34searchengine.service.DocFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("feedback")   //点击次数,拉取文档次数,点赞点踩均属于反馈(feedback)
public class DocFeedbackController
{

    @Autowired
    DocFeedbackService docFeedbackService;

    @PostMapping("{pdfUUID}/extractionCount/increase")
    public void increaseExtractionCount(@PathVariable long pdfUUID)
    {
        docFeedbackService.increaseExtractionCount(pdfUUID);
    }



    @PostMapping("{pdfUUID}/visitCount/increase")
    public void increaseVisitCount(@PathVariable long pdfUUID)
    {
        docFeedbackService.increaseVisitCount(pdfUUID);
    }

    @PostMapping("{pdfUUID}/preference/increase")
    public void increasePreference(@PathVariable long pdfUUID)
    {
        docFeedbackService.increasePreference(pdfUUID);
    }

    @PostMapping("{pdfUUID}/preference/decrease")
    public void decreasePreference(@PathVariable long pdfUUID)
    {
        docFeedbackService.decreasePreference(pdfUUID);
    }

}
