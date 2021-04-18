package chapter2.domain.pricing;

import chapter2.domain.DiscountPolicy;
import chapter2.domain.Money;
import chapter2.domain.Screening;

public class NoneDiscountPolicy implements DiscountPolicy {

    @Override
    public Money calculateDiscountAmount(Screening Screening) {
        return Money.ZERO;
    }
}
