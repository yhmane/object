# 11장 합성과 유연한 설계
---

상속과 합성은 객체지향 프로그래밍에서 가장 널리 사용되는 코드 재사용 기법이다.

* 상속은 부모 클래스를 재사용한다.
* 합성은 전체를 표현하는 객체가 부분을 표현하는 객체를 포함해서 부분 객체의 코드를 재사용 한다.
* 상속 관계는 is-a 관계라고 부르고 합성 관계는 has-a 관계라고 부른다.

상속과 합성은 이처럼 재사용 기법으로 많이 쓰이는 방법이지만 대체로 합성을 권하고 있다. 간략하게 이유를 살펴보고 합성에 대해 알아보도록 하자

먼저, 상속은 자식클래스 정의에 부모 클래스의 이름을 덧붙이는 것만으로 코드를 재사용할 수 있다. 부모클래스의 정의를 물려 받으며 코드를 추가하거나 재정의함으로써 기존 코드를 쉽게 확장할 수 있다. 그러나 상속을 제대로 활용하기 위해서는 부모 클래스의 내부 구현에 대해 상세히 알아야 하기 때문에 자식과 부모 사이의 결합도가 높아질 수 밖에 없다. 결과적으로 코드를 재사용하기 쉬운 방버이기 하지만 결합도가 높아지는 치명적인 단점이 있다.

합성은 구현에 의존하지 않는다는 점에서 상속과 다르다. 합성은 내부에 포함된 객체의 구현이 아닌 퍼블릭 인터페이스에 의존한다. 따라서, 합성을 이용하면 포함된 객체의 내부 구현이 변경되더라도 영향을 최소화 할 수 있기 때문에 더 안정적인 코드를 얻을 수 있다.

상속 관계는 클래스 사이의 정적인 관계인데 비해 합성 관계는 객체 사이의 동적인 관계이다. 코드 작성 시점에 결정한 상속 관계는 변경이 불가능하지만 합성 관계는 실행 시점에 동적으로 변경할 수 있다. 따라서, 상속 대신 합성을 사용하면 변경하기 쉽고 유연한 설계를 얻을 수 있다.

## 01. 상속을 합성으로 변경하기
### 불필요한 인터페이스 상속 문제
10장에서 알아본 Stack이다. Stack은 Vector를 상속 받아서 사용했기 때문에 문제가 발생하였다. 상속을 제거하고 Vector를 내부 변수로 바꾸어 주자.
```java
public class Stack<E> {
	private Vector<E> elements = new Vector<>();
	public E push(E item) {
		elements.addElement(item);
		return item;
	}

	public E pop() {
		if (elements.isEmpty()) {
			throw new EmptyStackException();
		}
		return elements.remove(elements.size() -1);
	}
}
```

이제 Stack의 public 인터페이스엔 10장에서 문제가 되었던 add와 같은 퍼블릭 인터페이스가 포함되지 않게 되었다. 이렇듯 상속을 합성 관계로 변경함으로써 Stack의 규칙이 어겨졌던 것을 막을수 있게 된다

### 메서드 오버라이딩의 오작용 문제
마찬가지로 10장에서 보았던 예제를 같이 살펴보자. InstrumentedHashSet의 경우 부모의 메서드를 호출하여 addCount가 원하는 결과를 같지 못하는 문제가 있었다.

다만, 여기서는 해당 퍼블릭 메서드를 사용해야 하기 때문에  위에서 사용하였던 내부 인스턴스 변수로 사용할 수 없다. 이번에는 인터페으스를 이용하면 해결할 수 있다.

```java
public class InstrumentedHashSet<E> implements Set<E> {
	private int addCount = 0;
	private Set<E> set;

	@Override
	public boolen add(E e) {
		addCount++;
		return set.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		addCount += c.size();
		return set.addAll(c);
	}
}
```

HashSet에 대한 구현 결합도는 제거 되었고, 퍼블릭 인터페이스는 그대로 유지할 수 있게 되었다.

### 부모 클래스와 자식 클래스의 동시 수정 문제
마찬가지로 10장에서 살펴보았던 예를 같이 보도록 하겠다
```java
public class PersonalPlaylist {
	private Playlist playlist = new Playlist();

	public void append(Song song) {
		playlist.append(song);
	}

	public void remove(Song song) {
		playlist.getTracks().remove(song);
		playlist.getSingers().remove(song.getSinger());
	}
}
```

합성으로 변경하더라도 가수별 노래목록을 유지하기 위해 Playlist와 PersonalPlaylist를 함께 수정해주어야 하는 문제는 해결 되지는 않았다.
그렇다 하더라도 여전히 합성을 사용하는게 좋은데, 향후에 Playlist의 내부 구현을 변경하더라도 파급효과를 최대한 PersonalPlaylist 내부로 캡슐화할 수 있기 때문이다.

간단하게 상속을 합성으로 변경하는 것에 대해 알아보았는데, 여기서 기억해야 할 것은 합성은 상속과 비교해 안정성과 유연성이라는 장점을 제공한다. 또한, 구현이 아니라 인터페이스에 의존하면 설계가 유연해진다는 것이다.

---
## 02. 상속으로 인한 조합의 폭발적인 증가
상속으로 인해 결합도가 높아지면 코드를 수정하는데 필요한 작업의 양이 과도하게 늘어나게 된다. 일반적으로 다음과 같은 두 가지 문제점이 발생한다
* 하나의 기능을 추가하거나 수정할때 불필요하게 많은 수의 클래스를 수정한다
* 단일 상속만 지원하는 언어에서는 상속으로 인해 오히려 중복 코드의 양이 늘어날 수 있다

### 기존 정책과 부가 정책 조립하기
10장에 있었던 핸드폰 요금제를 살펴보고, 추가로 부가정책(세금, 기본요금할인)을 추가한다고 가정해보자
* 기본정책으로 - 일반요금제, 심야요금제가 존재
* 세금 정책은 기본 정책 계산이 끝난후 계산이 끝난 결과에 세금을 부과
* 세금 정책은 Optional 하다
* 부가정책은 임의의 순서로 적용 가능하다
  [image:E22E2F19-6389-4A2D-863E-1AA63C4F9427-1747-00000972C0D3885C/CCF53784-DED4-4760-89CE-83A7B255D867.png]

위 가정으로 조합 가능한 요금 계산방법이다. 이것을 상속으로 구현해본다고 가정해보자.

[image:01835FA9-1E96-406F-829E-47476BA7741D-1747-0000098FAE24937D/DF787D4F-E892-4970-A31B-40BE1C4F3CCE.png]
위 조합으로 구성된 클래스가 1번이고, 새로운 부가정책을 추가하게 된다면 2번과 같은 방식으로 추가가 될것이다. 자세한 내용은 생략하였지만 상속으로 인해 조합이 추가 된다면 엄청난 고통이 수반될 것은 뻔한 그림이다.

---
## 03. 합성 관계로 변경하기
상속 관계는 컴파일타임에 결정되고 고정되기 때문에 코드를 실행하는 도중에 변경할 수 없다. 따라서 여러 기능을 조합해야 하는 설계에 상속을 이용하면 모든 조합별로 클래스를 추가해주어야 한다. 이것을 클래스 폭발 문제라 한다. 합성을 이용하면 런타임에 객체의 관계를 변경할 수 있기 때문에 유연한 설계가 가능해진다.

### 기본 정책 합성하기
실행 시점에 합성 관계를 이용해 정책들을 조합할 수 있게 인터페이스를 만들어 줍니다
정책, RatePolicy.java
```java
public interface RatePolicy {
	Money calculateFee(Phone phone);
}
```

기본 정책, BasicRatePolicy.java
```java
public abstract class BasicRatePolicy implements RatePolicy {
	@Override
	public Money calculateFee(Phone phone) {
		Money result = Money.ZERO;
	
		for (Call call : phone.getCalls()) {
			result.plus(calculateCallFee(call));
		}
		return result;
	}
	
	protected abstract Money calculateCallFee(Call call);
}
```

일반요금제, RegularPolicy.java
```java
public class RegularPolicy extends BasicRatePolicy {
	private Money amount;
	private Duration seconds;

	public RegularPolicy(Money amount, Duration seconds) {
		this.amount = amount;
		thiis.seconds = seconds;
	}

	@Override
	protected Money calculateCallFee(Call call) {
		return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
	}
}
```

심야요금제, NightlyDiscountPolicy.java
```java
public class NightlyDiscountPolicy extends BasicRatePolicy {
	private static final int LAST_NIGHT_HOUR = 22;
	private Money nightlyAmount
	private Money regularAmount;
	private Duration seconds;

	public NightlyDiscountPolicy(Money nightlyAmount, Money regularAmount, Duration seconds) {
		this.nightlyAmount = nightlyAmount;	
		this.regularAmount = regularAmount;
		thiis.seconds = seconds;
	}

	@Override
	protected Money calculateCallFee(Call call) {
		if (call.getFrom().getHour() >= LAST_NIGHT_HOUR) {
			return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
		}
	
		return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
	}
}
```

기본 정책을 이용해 요금을 계산할 수 있도록 Phone을 수정하자
```java
public class Phone {
	private RatePolicy ratePolicy;
	private List<Call> calls = new ArrayList<>();

	public Phone(RatePolicy ratePolicy) {
		this.ratePolicy = ratePolicy;
	}

	public List<Call> getCalls() {
		return Collections.unmodifiableList(calls);
	}

	public Money calculateFee() {
		return ratePolicy.calculateeFee(this);
	}
}
```

Phone 내부에 RatePolicy에 대한 참조가 포함돼어 있다는 것에 주목하자. 이것이 바로 합성이다. Phone이 다양한 요금 정책과 협력할 수 있어야 하므로 요금 정책의 타입이 RatePolicy라는 인터페이스로 정의되어 있다는 것에도 주목하자.

[image:D8D280B2-AB3B-47BA-8E1C-75B6EB9362DF-816-00000148B2BFC7B5/36F1AE98-9492-4BA3-A52B-483CEA959F81.png]

```java
Phone regularPhone = new Phone(new RegularPolicy(Money.wons(10), Duration.ofSecons(10)));
Phone nightlyPhone = new Phone(new NightlyDiscountPolicy(Money.wons(5), Money.wons(10), Duration.ofSecons(10)));
```

### 부가 정책 적용하기
위에서 만들었던 RatePolicy 인터페이스를 이용하여 정책을 추가해보자
부가정책, AddiionalRatePolicy.java
```java
public abstract class AddiionalRatePolicy implements RatePolicy {
	private RatePolicy next;
	public AddiionalRatePolicy(RatePolicy next) {
		this.next = next;
	}

	@Override
	public Money calculateFee(Phone phone) {
		Money fee = next.calucateFee(phone);
		return afterCalcurated(fee);
	}

	abstract protected Moeny afterCalcurated(Money fee);
}
```

세금정책, TaxablePolicy.java
```java
public class TaxablePolicy extends AdditionalRatePolicy {
    private double taxRatio;
    public TaxablePolicy(double taxRatio, RatePolicy next) {
        super(next);
        this.taxRatio = taxRatio;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.plus(fee.times(taxRatio));
    }
}
```

기본요금 할인 정책, RateDiscountablePolicy.java
```java
public class RateDiscountablePolicy extends AdditionalRatePolicy {

    private Money discountAmount;
    public RateDiscountablePolicy(Money discountAmount, RatePolicy next) {
        super(next);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}
```


[image:0480AC78-7C66-4FAF-8073-7D343A73B151-816-000001C6893AAC37/8B0518D3-4583-4F8A-9A79-BDE45E5B4D92.png]
완성된 다이어그램을 살펴보자. 아까보다 좀더 명확하게 정책 구분이 되고, 추가에 대한 클래스 폭발 문제도 개선되었다.

### 새로운 정책 추가하기
합성의 진가는 정책을 추가하거나 수정할 때 발휘 된다. 아래의 다이어그램처럼 간단히 클래스만 추가해주면 된다
[image:2B607BFD-F44C-49F8-B4B0-3BB84B44A5F9-816-000001EAD540C9A9/E6DF36F6-BDD8-4D83-8B9A-FDDB8560C3F9.png]

### 객체 합성이 클래스 상속보다 더 좋은 방법이다
객체지향에서 코드를 재사용하기 위해 가장 널리 사용되는 방법은 상속이다. 하지만 상속은 코드 재사용을 위한 우아한 해결책은 아니다. 부모 클래스의 세부적인 구현에 자식 클래스가 강하게 결합하기 때문에 수정/추가에 대해 번거로움이 발생한다.

코드를 재사용하면서 건던한 결합도를 유지할 수 있는 더 좋은 방법은 합성이다. 상속이 구현을 재사용하는데 비해 합성은 객체의 퍼블릭 인터페이스를 재사용하기 때문이다.

---
## 참조
* 조영호님의 오브젝트 “11장 합성과 유연한 설계”
#책/Object/chapter11