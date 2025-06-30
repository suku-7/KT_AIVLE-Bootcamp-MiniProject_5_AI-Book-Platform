@Data
@ToString
public class StandardSignedupPointCharged extends AbstractEvent {

    private Long id;
    private String userId;
    private Integer standardSignupPoint;
    private Date signupDate;
    private String isKt;

    public StandardSignedupPointCharged(Point aggregate) {
        super(aggregate);
        this.id = aggregate.getPointId(); 
        this.userId = aggregate.getUserId();
        this.standardSignupPoint = aggregate.getStandardSignupPoint();
        this.signupDate = aggregate.getSignupDate();
        this.isKt = aggregate.getIsKt();    
    }

    public StandardSignedupPointCharged() {
        super();
    }
}
//>>> DDD / Domain Event
