package com.maitrisetcf.domain.ports.out;

import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.domain.models.user.User;

/**
 * Outbound port responsible for generating and storing a subscription bill.
 */
public interface SubscriptionBillPort {

    /**
     * Generates and stores the bill for a successful subscription.
     *
     * @param user         the subscribed user
     * @param subscription the successful subscription
     * @return the relative path of the generated bill inside the upload directory
     */
    String generateSubscriptionBill(User user, UserSubscription subscription);
}
