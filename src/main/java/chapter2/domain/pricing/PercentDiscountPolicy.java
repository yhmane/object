package chapter2.domain.pricing;

import chapter2.domain.DiscountCondition;
import chapter2.domain.DefaultDiscountPolicy;
import chapter2.domain.Money;
import chapter2.domain.Screening;

public class PercentDiscountPolicy extends DefaultDiscountPolicy {
    private double percent;

    public PercentDiscountPolicy(double percent, DiscountCondition... conditions) {
        super(conditions);
        this.percent = percent;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return screening.getMovieFee().times(percent);
    }
}
