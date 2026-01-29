# JPA

## 기본키 매핑 방법

`@Id` 를 사용하여 테이블의 PK와 매핑 시킬 수 있다. 

PK를 데이터베이스가 자동생성하기 위해서는 `@GeneratedValue` 를 사용한다.

<br/>

### 기본키 자동 생성 전략(4가지)

```java
public enum GenerationType {
    TABLE,
    SEQUENCE,
    IDENTITY,
    UUID,
    AUTO;

    private GenerationType() {}
}

```

@GeneratedValue 어노테이션의 속성을 사용하여 전략을 설정할 수 있다.

- AUTO   
연동된 데이터베이스에 대한 적절한 생선 전략 자동 결정  
mysql: auto_increment, Oracle: sequence..  
- IDENTITY   
jpa 구현체가 기본키 생성을 데이터베이스에게 위엄하며, auto_increment를 이용해 기본키를 생성  
`JPA에서 영속성 관리를 하기 위해서는 PK가 있어야 하기 때문에 insert문은 생성 즉시 날아감`  
- SEQUENCE  
유일한 값을 순서대로 생성하는 시퀀스를 생성한 후, 해당 값을 기본키 할당  
`@SequenceGenerator` 를 사용하여 시퀀스를 생성해주면 된다.  
- TABLE  
데이터베이스에 키 생성 전용 테이블을 만들고 이를 사용하여 기본키 할당  
- UUID  
hibernate6에서부터 지원하며 UUID를 생성하여 기본키를 할당  
예시 ) `550e8400-e29b-41d4-a716-446655440000`  

<br/>

### 기본키에 복합키를 사용해보자

1. @Embeddable & @EmbeddedId
    1. @Embeddable : 복합키로 사용하고자 하는 객체에 선언
    2. @EmbeddedId : 기본키로 선언하는 속성에 선언
2. @IdClass
    1. 사용하고자 하는 클래스 상단에 선언 ex) `@IdClass(Pk.class)` 
    2. 객체 그대로 속성으로 사용되지 않고 객체 내부의 속성이 그대로 풀어서 식별자 클래스에도 선언됨
    3. 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야함.

```java
@Getter
@Entity
@Table(name = "member_detail")
@IdClass(Pk.class)
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetail {

    @Id
    private Long id;
    private String name;
    private int age;
    private String address;

    @Getter
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    class Pk {
        private Long id;
        private String name;
    }
}

```

<br/>

## @Enumerated

Entity 내부에 Enum타입의 속성으로 사용하고자 할 때, 데이터베이스에 어떤 값을 넣어줄지 설정한다.

```java
package jakarta.persistence;

public enum EnumType {
    ORDINAL,
    STRING;

    private EnumType() {
    }
}

```

타입은 두가지가 존재한다.

- ORDINAL : 해당 값의 순서가 int타입으로 저장된다. (ADMIN: 1 / MEMBER: 2)
- STRING : 타입명이 그대로 저장된다. (ADMIN / MEMBER)

```java
public enum MemberType {
    ADMIN, MEMBER;
}
```

<br/>

## 연관관계 매핑

### 방향

- 단방향 : 두 엔티티 중, 한쪽의 엔티티만 참조하고 있음을 의미
- 양방향 : 두 엔티티가 서로 참조하고 있음을 의미

> **연관관계의 주인**
연관관계 관리의 포인트는 누가 외래키를 들고 있느냐이다.
단방향으로 설정하면 참조를 하고있는 엔티티가 외래키를 들게되지만
양방향으로 설정하면 누가 외래키를 들고 있을지 설정해줘야 한다.

양방향 관계에서 주인이 아닌 엔티티의 속성에는 `mappedBy` 를 사용해서 주인을 명시해줘야 한다.
> 

<br/>

### 다중성

- **@ManyToOne**
    - 다대일( N : 1 )
    - 데이터베이스에서는 무조건 다(N)쪽이 외래키를 갖는다.
    - 양방향 관계시 @OneToMany와 같이 사용되며,
    외래키를 갖지 않는 상대쪽 속성에 주인을 명시해줘야 한다.
    `@OneToMany(mappedBy=”속성명”)`
- **@OneToMany**
    - 일대다( 1 : N )
    - List형식으로 다(N) 엔티티를 들고 있다.
    - 데이터베이스 구조상 단방향으로 사용할 일이 없다고 보면된다.
    - save 시 각각의 엔티티가 저장된 뒤, 연관관계 설정을 위한 update문이 한번 더 날아간다. (성능 이슈)
- **@OneToOne**
    - 일대일( 1 : 1 )
    - 단방향도 양방향도 1:1로 사용되며, 양방향 사용 시 주인을 명시해줘야 한다.
- **@ManyToMany**
    - 다대다 ( N : N )
    - 중간테이블이 생성되지만 권장하지 않는 구조.

<br/>

### 영속성 전이 (Persistence CasCade)

- Entity의 영속성 상태 변화를 연관된 Entity에도 함께 적용하는 것
- 연관관계의 다중성 지정 시 cascade 속성으로 설정할 수 있다.
- CascadeType
    - PERSIST : Entity를 영속 객체로 추가할 때 연관된 Entity도 함께 영속 객체로 추가한다.
    - REMOVE : Entity를 삭제할 때 연관된 Entity도 함께 삭제한다.
    - DETACH : Entity를 영속성 컨텍스트에서 분리할 때 연관된 Entity도 함께 분리 상태로 만든다.
    - REFRESH : Entity를 데이터베이스에서 다시 읽어올 때 연관된 Entity도 함께 다시 읽어온다.
    - MERGE : Entity를 준영속 상태에서 다시 영속 상태로 변경할 때 연관된 Entity도 함께 변경한다.
    - ALL : 모든 상태 변화에 대한 연관된 Entity에 함께 적용한다.

<br/>

### Fetch 전략

- FetchType.EAER : 조회 시 즉시 가져옴
- FetchType.LAZY : 참조가 일어나면 가져옴

**기본 Fetch전략**

- *ToOne (@OneToOne, @ManyToOne) : FetchType.EAGER
- *ToMany (@OneToMany, @ManyToMany) : FetchType.LAZY

<br/>

## N+1 문제

Entity에 대해 하나의 쿼리로 N개의 레코드를 가져왔을 때, 

연관관계 Entity를 가져오기 위해 쿼리를 N번 추가적으로 수행하는 문제


### 잘못된 오해

1. N+1 문제는 EAGER Fetch 전략 때문에 발생한다?  
아니다. Fetch전략을 LAZY로 설정했더라도 연관 Entity를 참조하면 그 순간 추가적인 쿼리가 수행된다.  
시점의 차이일 뿐이지 결국 참조가 일어난다고 하면 결국 N+1문제가 발생할 수 있다.
   
2. findAll()메서드는 N+1문제를 발생시키지 않는다?  
Fetch 전략을 적용해서 연관 Entity를 가져오는 것은 오직 단일 레코드에 대해서만 적용된다.  
단일 레코드 조회가 아닌 경우 해당 쿼리를 먼저 수행하고 반환된 레코드 하나하나에 대해 Fetch전략을 적용해 연관 Entity를 가져온다. 그러므로 findAll도 문제를 발생시킬 수 있다.  

<br/>

### Fetch JOIN으로 문제 해결 시 흔히 하는 실수

1. Pagination을 사용할 때, Fetch join을 같이 처리함  
실제로는 모든 레코드를 가져오는 쿼리가 실행됨  
페이지 범위 내에 어느정도 범위가 들어갈지 모르기때문에 전체 레코드를 가져온 다음에 메모리에서 원하는 페이지만 반환함

2. 둘 이상의 컬렉션을 Fetch JOIN - MultipleBagFetchException  
Java의 List타입은 기본적으로 Hibernate의 Bag타입으로 맵핑됨  
Hibernate의 Bag타입은 중복 요소를 허용하는 비순차 컬렉션으로 둘 이상의 컬렉션을 FetchJoin하는 경우  
그 결과로 만들어지는 어느 행이 유효한 중복을 포함하고 있고 어느 행이 그렇지 않은지 판단할 수 없어 예외가 발생됨  
⇒ 해결을 위해서는List를 Set으로 변경한다. @OrderColumn  

<br/>

### 페이징 처리

**Pageable** 

데이터를 페이지 단위로 검색하고 제어하는 인터페이스

페이지 번호, 페이지 크기, 정렬 기준 설정을 통해 원하는 페이지의 데이터를 가져온다.

- 페이지 번호는 0부터 시작한다.

**PageRequest** 

JPA에서 제공하는 구체적인 클래스로, 데이터를 페이지 단위로 검색하고 결과를 제어하는데 사용된다.
⇒ Pageable 구현체

<br/>

### 잘 알려지지 않은 사실

1. **JPA Repository 메서드로 JOIN을 할 수 있다?**
- JPA는 데이터베이스 테이블 간의 관계를 Entity클래스의 속성으로 모델링
- JPA Repository 메서드에서는 “_”를 통해 내부 속성 값을 접근할 수 있다.  
연관관계 시 id가 아닌 내부 속성값으로 조회를 하는 경우 (ex. findAllByMember_name) Join문으로 가져와진다.

2. **Page vs Slice**
Page는 조회 후 count(*)를 한번 더 쿼리하여 totalPage를 가져오지만,  
Slice쿼리의 경우 우리가 설정한 limit보다 1개를 더 가져온 후   
추가적으로 조회한 객체가 있는지 확인한 후 다음 페이지 여부를 반환한다.

3. JPA Repository 메서드로 DTO Projection이 가능하다.  
- **Class 기반(DTO) Projection**  
반환타입에서 DTO를 사용해주면 직접 Projection을 수행해서 반환해준다.  
- **Inteface기반 Projection**  
Class와 동일하지만 인터페이스는 메소드를 선언해주고 사용하면 됨 → String getName(); 하면 name만 가져옴  
인터페이스는 중첩 구조를 지원한다.   
@Value + SpEL(target변수)  
- **Dynamic Projection**  
타입을 직접 넣어 반환받을 수 있다.  
```java
// repository
<T> List<T> findTargetByMember_Id(Long id, Class<T> type);
```

```java
    // test 예시
    @GetMapping("/pro/v2/{memberId}")
    public ResponseEntity<List<PostCommentResponse>> getPostDynamic(@PathVariable("memberId") Long memberId) {
        List<PostCommentInterface> postComments = postRepository.findTargetByMember_Id(memberId,
                PostCommentInterface.class);

        List<PostCommentResponse> response = new ArrayList<>();
        for (PostCommentInterface postComment : postComments) {
            response.add(new PostCommentResponse(postComment));
        }
        return ResponseEntity.ok(response);
    }
```
