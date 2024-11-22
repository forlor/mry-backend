package com.mryqr.core.common.domain.event;

import static com.mryqr.core.common.domain.AggregateRootType.APP;
import static com.mryqr.core.common.domain.AggregateRootType.ASSIGNMENT;
import static com.mryqr.core.common.domain.AggregateRootType.ASSIGNMENT_PLAN;
import static com.mryqr.core.common.domain.AggregateRootType.DEPARTMENT;
import static com.mryqr.core.common.domain.AggregateRootType.DEPARTMENT_HIERARCHY;
import static com.mryqr.core.common.domain.AggregateRootType.GROUP;
import static com.mryqr.core.common.domain.AggregateRootType.MEMBER;
import static com.mryqr.core.common.domain.AggregateRootType.ORDER;
import static com.mryqr.core.common.domain.AggregateRootType.PLATE;
import static com.mryqr.core.common.domain.AggregateRootType.PLATE_BATCH;
import static com.mryqr.core.common.domain.AggregateRootType.QR;
import static com.mryqr.core.common.domain.AggregateRootType.SUBMISSION;
import static com.mryqr.core.common.domain.AggregateRootType.TENANT;

import com.mryqr.core.common.domain.AggregateRootType;

public enum DomainEventType {
  APP_CREATED(APP),
  APP_DELETED(APP),
  APP_CREATED_FROM_TEMPLATE(APP),
  APP_PAGE_CHANGED_TO_SUBMIT_PER_INSTANCE(APP),
  APP_PAGE_CHANGED_TO_SUBMIT_PER_MEMBER(APP),
  APP_ATTRIBUTES_CREATED(APP),
  APP_ATTRIBUTES_DELETED(APP),
  APP_PAGES_DELETED(APP),
  APP_CONTROLS_DELETED(APP),
  APP_CONTROL_OPTIONS_DELETED(APP),
  APP_GROUP_SYNC_ENABLED(APP),
  SUBMISSION_DELETED(SUBMISSION),
  SUBMISSION_CREATED(SUBMISSION),
  SUBMISSION_UPDATED(SUBMISSION),
  SUBMISSION_APPROVED(SUBMISSION),
  MEMBER_CREATED(MEMBER),
  MEMBER_DELETED(MEMBER),
  MEMBER_NAME_CHANGED(MEMBER),
  MEMBER_DEPARTMENTS_CHANGED(MEMBER),
  MEMBER_ADDED_TO_DEPARTMENT(MEMBER),
  MEMBER_REMOVED_FROM_DEPARTMENT(MEMBER),
  GROUP_CREATED(GROUP),
  GROUP_DEACTIVATED(GROUP),
  GROUP_ACTIVATED(GROUP),
  GROUP_DELETED(GROUP),
  GROUP_MANAGERS_CHANGED(GROUP),
  QR_BASE_SETTING_UPDATED(QR),
  QR_CREATED(QR),
  QR_RENAMED(QR),
  QR_MARKED_AS_TEMPLATE(QR),
  QR_UNMARKED_AS_TEMPLATE(QR),
  QR_DELETED(QR),
  QR_CUSTOM_ID_UPDATED(QR),
  QR_ACTIVATED(QR),
  QR_DEACTIVATED(QR),
  QR_CIRCULATION_STATUS_CHANGED(QR),
  QR_GROUP_CHANGED(QR),
  QR_PLATE_RESET(QR),
  QR_ATTRIBUTES_UPDATED(QR),
  QR_DESCRIPTION_UPDATED(QR),
  QR_GEOLOCATION_UPDATED(QR),
  QR_HEADER_IMAGE_UPDATED(QR),
  PLATE_BOUND(PLATE),
  PLATE_UNBOUND(PLATE),
  PLATE_BATCH_DELETED(PLATE_BATCH),
  PLATE_BATCH_CREATED(PLATE_BATCH),
  TENANT_CREATED(TENANT),
  TENANT_SUBDOMAIN_UPDATED(TENANT),
  TENANT_ACTIVATED(TENANT),
  TENANT_BASE_SETTING_UPDATED(TENANT),
  TENANT_INVOICE_TITLE_UPDATED(TENANT),
  TENANT_RESOURCE_USAGE_UPDATED(TENANT),
  TENANT_PLAN_UPDATED(TENANT),
  TENANT_SUBDOMAIN_READY_STATUS_UPDATED(TENANT),
  TENANT_ORDER_APPLIED(TENANT),
  TENANT_DEACTIVATED(TENANT),
  ORDER_CREATED(ORDER),
  ORDER_WX_PAY_UPDATED(ORDER),
  ORDER_WX_TRANSFER_UPDATED(ORDER),
  ORDER_BANK_TRANSFER_UPDATED(ORDER),
  ORDER_DELIVERY_UPDATED(ORDER),
  ORDER_INVOICE_REQUESTED(ORDER),
  ORDER_INVOICE_ISSUED(ORDER),
  ORDER_REFUND_UPDATED(ORDER),
  ASSIGNMENT_CREATED(ASSIGNMENT),
  ASSIGNMENT_PLAN_DELETED(ASSIGNMENT_PLAN),
  DEPARTMENT_CREATED(DEPARTMENT),
  DEPARTMENT_DELETED(DEPARTMENT),
  DEPARTMENT_MANAGERS_CHANGED(DEPARTMENT),
  DEPARTMENT_RENAMED(DEPARTMENT),
  DEPARTMENT_HIERARCHY_CHANGED(DEPARTMENT_HIERARCHY);

  private final AggregateRootType arType;

  DomainEventType(AggregateRootType arType) {
    this.arType = arType;
  }

  public AggregateRootType getArType() {
    return arType;
  }
}
