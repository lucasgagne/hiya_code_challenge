public class Hangup extends CallEvent {

    public Hangup() {
        super();
    }
    public Hangup(String from, String to, Long timestamp) {
        super(from, to, timestamp);
    }
    
}
