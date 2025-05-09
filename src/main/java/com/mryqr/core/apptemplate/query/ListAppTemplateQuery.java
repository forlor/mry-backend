package com.mryqr.core.apptemplate.query;

import com.mryqr.common.utils.Query;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class ListAppTemplateQuery implements Query {

    @Size(max = 50)
    private final String category;

    @Size(max = 50)
    private final String scenario;

    @Size(max = 50)
    private final String search;

    @Min(1)
    private final int pageIndex;

    @Min(10)
    @Max(100)
    private final int pageSize;

}
