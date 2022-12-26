package cn.edu.hhu.a34backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResult
{
    List<String> highlights;

    int pageNumber;

    long parentPdfUuid;

}
