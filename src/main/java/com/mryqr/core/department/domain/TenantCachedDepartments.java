package com.mryqr.core.department.domain;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class TenantCachedDepartments {
  private final List<TenantCachedDepartment> departments;
}
