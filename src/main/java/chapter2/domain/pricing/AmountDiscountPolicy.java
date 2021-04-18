package chapter2.domain.pricing;

import chapter2.domain.DiscountCondition;
import chapter2.domain.DiscountPolicy;
import chapter2.domain.Money;
import chapter2.domain.Screening;

public class AmountDiscountPolicy extends DiscountPolicy {
    private Money discountAmount;

    public AmountDiscountPolicy(Money discountAmount, DiscountCondition... conditions) {
        super(conditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return discountAmount;
    }
}
