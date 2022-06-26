package assignment;
import java.util.Random;


import akka.actor.typed.javadsl.*;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.Behavior;


public class BodyActor extends AbstractBehavior<BodyMsg>{

    private P2d pos;
    private V2d vel;
    private double mass;
    private final double FRICTION_FORCE = 0.1;
    private V2d friction = null;
    private double distanceFromBody = 0.0;
    private BodyActor body2;

    private BodyActor(ActorContext<BodyMsg> context) {
        super(context);
        pos = new P2d(0, 0);
        vel = new V2d(0,0);
        mass = new Random().nextDouble()/100;

    }

    @Override
    public Receive<BodyMsg> createReceive() {
        return newReceiveBuilder()
            .onMessage(UpdateBodyPos.class, this::onUpdateBodyPos)
            .onMessage(UpdateBodyVel.class, this::onUpdateBodyVel)
            .onMessage(BodyFrictionForce.class, this::onBodyFrictionForce)
            .onMessage(DistanceBody.class, this::onDistanceBody)
            .build();
    }

    private Behavior<BodyMsg> onUpdateBodyPos(UpdateBodyPos msg) {
        this.getContext().getLog().info("update pos");
        pos.sum(new V2d(vel).scalarMul(0.05));
        return this;
    }

    private Behavior<BodyMsg> onUpdateBodyVel(UpdateBodyVel msg) {
        this.getContext().getLog().info("update vel");
        vel = vel.sum(new V2d(new V2d(new Random().nextDouble()/100, new Random().nextDouble()/100)).scalarMul(new Random().nextDouble()));
        return this;
    }

    private Behavior<BodyMsg> onBodyFrictionForce(BodyFrictionForce msg) {
        this.getContext().getLog().info("friction force");
        friction = new V2d(this.vel.scalarMul(-FRICTION_FORCE));
        return this;
    }

    private Behavior<BodyMsg> onDistanceBody(DistanceBody msg) {
        this.getContext().getLog().info("distance from body");
        double dx = this.pos.getX() - body2.pos.getX();
        double dy = this.pos.getY() - body2.pos.getY();
        double res = dx * dx + dy * dy;
        if (res > 0) {
            distanceFromBody = Math.sqrt(res);
        } else {
            distanceFromBody = 0.0;
        }
        return this;
    }

    public static Behavior<BodyMsg> create() {
        return Behaviors.setup(BodyActor::new);
    }
    
    static public class UpdateBodyPos implements BodyMsg {}
    static public class UpdateBodyVel implements BodyMsg {}
    static public class BodyFrictionForce implements BodyMsg {}
    static public class DistanceBody implements BodyMsg {}
}
