public class Call extends CallEvent{
    public Call() {
        super();
    }
    public Call(String from, String to, Long timestamp) {
        super(from, to, timestamp);
    }
}
