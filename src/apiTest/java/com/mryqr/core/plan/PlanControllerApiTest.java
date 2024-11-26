package com.mryqr.core.plan;

import static com.mryqr.core.plan.domain.PlanType.ADVANCED;
import static com.mryqr.core.plan.domain.PlanType.BASIC;
import static com.mryqr.core.plan.domain.PlanType.FLAGSHIP;
import static com.mryqr.core.plan.domain.PlanType.FREE;
import static com.mryqr.core.plan.domain.PlanType.PROFESSIONAL;
import static com.mryqr.core.plan.query.QEnabledFeature.GEO_PREVENT_FRAUD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.mryqr.BaseApiTest;
import com.mryqr.core.plan.query.QListPlan;
import org.junit.jupiter.api.Test;

public class PlanControllerApiTest extends BaseApiTest {

  @Test
  public void should_fetch_all_plans_info() {
    List<QListPlan> planInfos = PlanApi.listPlans();
    QListPlan freePlan = planInfos.get(0);
    QListPlan basicPlan = planInfos.get(1);
    QListPlan advancedPlan = planInfos.get(2);
    QListPlan professionalPlan = planInfos.get(3);
    QListPlan flagshipPlan = planInfos.get(4);
    assertEquals(FREE, freePlan.getType());
    assertEquals(BASIC, basicPlan.getType());
    assertEquals(ADVANCED, advancedPlan.getType());
    assertEquals(PROFESSIONAL, professionalPlan.getType());
    assertEquals(FLAGSHIP, flagshipPlan.getType());

    assertEquals(1, freePlan.getLevel());
    assertEquals(2, basicPlan.getLevel());
    assertEquals(3, advancedPlan.getLevel());
    assertEquals(4, professionalPlan.getLevel());
    assertEquals(5, flagshipPlan.getLevel());

    assertTrue(flagshipPlan.getAllFeatures().contains(GEO_PREVENT_FRAUD));
  }
}
