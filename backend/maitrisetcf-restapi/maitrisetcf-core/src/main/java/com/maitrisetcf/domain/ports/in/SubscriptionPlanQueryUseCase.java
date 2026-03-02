package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlanFilter;

import java.util.List;

public interface SubscriptionPlanQueryUseCase {

    PagedResult<SubscriptionPlan> findAll(SubscriptionPlanFilter filter, int page, int size);

    List<SubscriptionPlan> findAllActive();

    long count(SubscriptionPlanFilter filter);
}
