# 10장 상속과 코드 재사용
---
> 전통적인 상속이란,
> 클래스안에 정의된 인스턴스 변수와 메서드를
> 새로운 클래스에 자동으로 추가하는 것이다

---
## 01. 상속과 중복 코드
중복 코드의 경우 많은 생각을 하게 만든다.
이미 존재하는데도 새로운 코드를 만든 이유는 무엇일까? 아니면 단순한 실수 일까?

### DRY 원칙
> 앤드류 헌트 & 데이비드 토마스
> 프로그래머들은 DRY 원칙을 따라야 한다

* DRY는 ‘반복하지 마라’  Don’t Repeat Yourself의 첫글자들이다
  중복된 코드는 변경을 방해한다. 이것이 중복 코드를 제거해야 하는 가장 큰 이유다


### 중복과 변경
#### 중복 코드 살펴보기
Call.java (개별 통화기간)
```java
public class Call {
	private LocalDateTime from;
	private LocalDateTime to;

	public Call(LocalDateTime from, LocalDateTime to) {
		this.from = from;
		this.to = to;
	}

	public Duration getDuration() {
		return Duration.between(from, to);
	}
	
	public LocalDateTime getFrom() {
		return from;
	}
}
```

Phone.java (일반 요금제 전화기)
```java
public class Phone {
	private Money amount; // 단위요금
	private Duration seconds; // 단위시간
	private List<Call> calls = new ArrayList<>();

	public Phone(Money amount, Duration duration) {
		this.amount = amount;
		this.duration = duration;
	}

	public void call(Call call) {
		calls.add(call);
	}

	public Money calculateFee() {
		Money result = Money.ZERO;

		for (Call call : calls) {
			result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
		}

		return result
	}
}
```

기존 요금제 외에 심야요금제 요구사항이 추가 되었다고 가정해보자

NightlyDiscountPhone.java(심야 요금제 전화기)
```java
public class NightlyDiscountPhone {
	private static final int LATE_NIGHT_HOUR = 22;

	private Money nightlyAmount;
	private Money regularAmount;
	private Duration seconds; // 단위시간
	private List<Call> calls = new ArrayList<>();

	public Phone(Money amount, Duration duration) {
		this.amount = amount;
		this.duration = duration;
	}

	public Money calculateFee() {
		Money result = Money.ZERO;

		for (Call call : calls) {
			if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
				result = result.plus(nightlyAmount(call.getDuration().getSeconds() / seconds.getSeconds()));
			} else {
			result = result.plus(regularAmount(call.getDuration().getSeconds() / seconds.getSeconds()));
			}
		}

		return result
	}
}
```

쉽게 요구사항을 반영했지만, 중복이 많이 발생하였다. 당장은 모르겠지만, 많은 중복은 엄청난 비용을 요구하게 된다

#### 중복 코드 수정하기
통화 요금에 세금을 계산하는 로직을 추가해보자, 그러나 요금을 계산하는 로직은 Phone과 NightlyDiscountPhone 양쪽 모두에 구현돼 있기 때문에 두 클래스를 모두 수정해야 한다
Phone.java
```java
public class Phone {
	...
	private double taxRate;

	public Money calculateFee() {
		Money result = Money.ZERO;

		for (Call call : calls) {
			result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
		}

		return result.plus(result.times(taxRate));
	}
}
```

NightlyDiscountPhone.java
```java
public class NightlyDiscountPhone {
	...
	private double taxRate;

	public Money calculateFee() {
		Money result = Money.ZERO;

		for (Call call : calls) {
			if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
				result = result.plus(nightlyAmount(call.getDuration().getSeconds() / seconds.getSeconds()));
			} else {
			result = result.plus(regularAmount(call.getDuration().getSeconds() / seconds.getSeconds()));
			}
		}

		return result.minus(result.times(taxRate));
	}
}
```

문제는, 중복 코드 수정시 모든 중복 코드를 같이 바꿔줘야 한다는 것이다. 만약, Phone만 수정한채 배포가 된다면 심야요금제의 세금이 포함되지 않고 계산될 것이다.

#### 타입 코드 사용하기
두 클래스 사이의 중복코드를 제거하는 한 가지 방법은 클래스를 하나로 합치는 것이다.
다만, 타입 코드를 사용하게 되면 높은 결합도 문제에 직면하게 된다

Phone.java
```java
public class Phone {
	private static final int LATE_NIGHT_HOUR = 22;
	enum PhoneType { REGULAR, NIGHTLY }

	private PhoneType phoneType;
	private Money nightlyAmount;
	private Money regularAmount;
	private Duration seconds; 
	private List<Call> calls = new ArrayList<>();

	public Phone(Money amount, Duration duration) {
		this(PhoneType.REGULAR, amount, Money.ZERO, Money.ZERO, seconds);
	}

	public Phone(Money nightlyAmount, Money regularAmount, Duration seconds) {
		this(PhoneType.NIGHTLY, Money.ZERO, nightlyAmount , regularAmount, seconds);
	}

	public Phone(PhoneType type, Money amount, nightlyAmount, Money regularAmount, Duration seconds) {
		this.type = type;
		this.amount = amount;
		this.regularAmount = regularAmount;
		this.nightlyAmount = nightlyAmount;
		this.seconds = seconds;
	}

	public Money calculateFee() {
		Money result = Money.ZERO;

		for (Call call : calls) {
			if (type == PhoneType.REGULAR) {
				result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
			} else {
				if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
					result = result.plus(nightlyAmount(call.getDuration().getSeconds() / seconds.getSeconds()));
					} else {
						result = result.plus(regularAmount(call.getDuration().getSeconds() / seconds.getSeconds()));
					}
				}
			}
		}

		return result
	}
}
```


### 상속을 이용해서 중복 코드 제거하기
객체지향은 타입 코드를 사용하지 않고도 중복 코드를 관리할 수 있는 효과적인 방법을 제공한다. 그것은 바로 상속이다.

```java
public class NightlyDiscountPhone extends Phone {
	private static final int LATE_NIGHT_HOUR = 22;

	private Money nightlyAmount;

	public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration duration) {
		super(reguarAmount, seconds);
		this.nightlyAmount = nightlyAmount;
	}

	public Money calculateFee() {
		Money result = super.calculateFee();

		Money nightlyFee = Money.ZERO;
		for (Call call : calls) {
			if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
				nightlyFee = nightlyFee.plus(getAmount().minus(nightlyAmount).times(call.getDuration().getSeconds() / seconds.getSeconds()));
			}
		}

		return result.minus(nightlyFee);
	}
}
```

super 참조를 통해 부모 클래스의 메서드를 호출한다. 10시 이전엔 Phone 요금제를 통해서 계산하고, 10시 이후 심야타임은 NightlyDiscountPhone을 통해 요금을 계산한다

### 강하게 결합된 Phone과 NightlyDiscountPhone
다만, 상속은 부모 클래스와 자식 클래스 사이의 강결합을 발생시킨다. calcaluateFee 메서드를 확인해보면 super를 참조해 가격을 계산하고 차감하는 방식이다. 여기에서 세금을 부과하는 요구사항이 추가 된다면 어떻게 될까?

자식 클래스의 메서드 안에서 super 참조를 이용해 부모 클래스의 메서드를 직접 호출할 경우 두 클래스는 강하게 결합된다. Super 호출을 제거할 수 있는 방법을 찾아 결합도를 제거해야 한다.

---
## 02. 취약한 기반 클래스 문제
부모 클래스의 변경에 의해 자식 클래스가 영향을 받는 현상을 취약한 기반 클래스 문제라고 부른다

### 불필요한 인터페이스 상속 문제
자바의 초기 버전에서 상속을 잘못 사용한 대표적인 사례는 java.util.Properties와 java.util.Stack이다. 두 클래스의 공통점은 부모 클래스에서 상속받은 메서드를 사용할 경우 자식 클래스의 규칙이 위반 될 수 있다는 것이다.

[image:D144F7D2-73D5-4402-8B2A-6DDF31A7C252-1813-000004C63A91644F/F94A9E45-DA34-4452-B809-5A295FE3F94F.png]

```java
Stack<String> stack = new Stack<>();
stack.push("1st");
stack.push("2nd");
stack.push("3rd");

stack.add(0, "4th");
assertEquals("4th", stack.pop()); // 에러!
```

Pop 메서드의 반환값은 “3rd”이기 때문에 에러가 난다. 이유는 vector의 add 메서드를 이용해 스택 맨앞에 “4th”를 추가했고, Stack의 pop은 가장 위에 있는 “3rd”를 꺼냈기 때문이다.
Stack이 규칙을 무너뜨릴 수 있는 여지가 있는 Vector의 퍼블릭 인터페이스까지 함께 상속을 받았다.
상속을 사용할 경우 부모 클래스의 메서드가 자식 클래스의 내부 구조에 대한 규칙을 깨드릴 수 있는 것도 숙지하고 있어야 한다

### 메서드 오버라이딩의 오작용 문제
이펙티브 자바의 나오는 유명한 예이다.
```java
public class InstrumentedHashSet<E> extends HashSet<E> {
	private int addCount = 0;

	@Override
	public boolen add(E e) {
		addCount++;
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		addCount += c.size();
		return super.addAll(c);
	}
}

InstrumentedHashSet<String> languages = new InstrumentedHashSet<>();
languages.addAll(Arrays.asList("java", "Ruby", "Scala"));
```

대부분의 사람들은 addCount의 값이 3이 될 것이라 생각하지만, 실제 값은 6이다. 그 이유는 부모 클래스인 HashSet의 addAll 메서드 안에서 add 메서드를 호출하기 때문이다.

자식 클래스가 부모 클래스의 메서드를 오버라이딩할 경우 부모 클래스가 자신의 메서드를 사용하는 방법에 자식 클래스가 결합될 수 있다.

---
## 03. Phone 다시 살펴보기
### 추상화에 의존하자
NightlyDiscountPhone의 가장 큰 문제점은 Phone에 강하게 결합돼 있기 때문에 Phone이 변경될 경우 함께 변경될 가능성이 높다는 것이다. 이 문제를 해결하는 가장 일반적인 방법은 자식 클래스가 부모 클래스의 구현이 아닌 추상화에 의존하도록 만드는 것이다

### 차이를 메서드로 추출하라
중복 코드 안에서 차이점을 별도의 메서드로 추출해라

먼저, Phone과 NightlyDiscountPhone의 중복과 차이점을 분리한다
[image:7812CCA1-8A0E-4963-BD29-5138A3538AC8-1813-000006F294381BC7/DFE93C7C-C980-4D64-9DE9-96662A690FA0.png]


### 중복 코드를 부모 클래스로 올려라
위의 코드에서 중복된 메서드는 공통화로 빼고, 다른 부분은 추상메서드로 구현해보자
```java
public abstract class Phone {
    private List<Call> calls = new ArrayList<>();
    public Money calculateFee() {
        Money result = Money.ZERO;
        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        return result;
    }
    abstract protected Money calculateCallFee(Call call);
}
```

Phone.java
```java
public class Phone extends RegularPhone {
    private Money amount;
    private Duration seconds;

    public RegularPhone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```

NightlyDiscountPhone.java
```java
public class NightlyDiscountPhone extends AbstractPhone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Money nightlyAmount;
    private Money regularAmount;
    private Duration seconds;

    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        } else {
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
}
```

자식 클래스들 사이의 공통점을 부모 클래스로 옮김으로써 실제 코드를 기반으로 상속 계층을 구성할 수 있었다. 여기서 우리는 추상화가 핵심이라는 것을 알 수 있다


여기서 세금을 추가하게 되면 자식 변수를 추가해야 하는데 이에 따라 자식 클래스의 생성자 로직도 변경이 불가피하다. 다만, 중요한 것은 인스턴스 변수의 추가에 따른
생성자의  수정보다 로직에 대한 변경으로 수정을 최소화 하는 것이 중요하다.
