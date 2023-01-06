package cn.edu.hhu.a34searchengine.param;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
@Data
public class UploadParam {

    @NotEmpty(message="标题不能为空")
    String title;

    @NotEmpty(message="数据为空")
    String data;
}
