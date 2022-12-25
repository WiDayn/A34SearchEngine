package cn.edu.hhu.a34backend.service.impl;

import cn.edu.hhu.a34backend.param.UploadParam;
import cn.edu.hhu.a34backend.service.UploadService;
import cn.edu.hhu.a34backend.utils.DividePDF;
import cn.edu.hhu.a34backend.utils.SnowFlake;
import cn.edu.hhu.a34backend.vo.ErrorCode;
import cn.edu.hhu.a34backend.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    @Value("${setting.pdf-file-path}")
    private String uploadPath;

    @Value("${setting.pdf-temp-path}")
    private String tempPath;

    @Value("${setting.work-id}")
    private int workerId;

    @Value("${setting.datacenter-id}")
    private int datacenterId;

    @Override
    public Result uploadPDF(UploadParam uploadParam) {
        SnowFlake snowFlake=new SnowFlake(workerId,datacenterId,1);

        String saveName = String.valueOf(snowFlake.nextId());

        String saveFilePath = uploadPath + "/" + saveName + ".pdf";

        BASE64Decoder base64Decoder = new BASE64Decoder();

        try {
            byte[] decodedBytes = base64Decoder.decodeBuffer(uploadParam.getData());

            File file = new File(saveFilePath);

            FileOutputStream fop = new FileOutputStream(file);

            fop.write(decodedBytes);

            fop.flush();

            fop.close();

            DividePDF.divide(saveFilePath, saveName, tempPath);

        } catch (Exception e) {
            Result.fail(ErrorCode.SYSTEM_EXCEPTION.getCode(), ErrorCode.SYSTEM_EXCEPTION.getMsg());

            e.printStackTrace();
        }

        return Result.success(null, "Success");
    }
}
