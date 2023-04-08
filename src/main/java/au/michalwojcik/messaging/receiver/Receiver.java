package au.michalwojcik.messaging.receiver;

import au.michalwojcik.messaging.receiver.resolver.Resolver;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;

import java.util.Collection;

/**
 * @author michal-wojcik
 */
public class Receiver {

    private Collection<Resolver> resolvers;

    public Receiver(Collection<Resolver> resolvers) {
        this.resolvers = resolvers;
    }

    @SqsListener(value = "${message.receiver.queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receive(Message<String> message) {

    }
}
