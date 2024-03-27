class CallEvent {
    String from;
    String to;
    long timestamp;

    public CallEvent() {

    }
    
    public CallEvent(String from, String to, Long timestamp) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }

}