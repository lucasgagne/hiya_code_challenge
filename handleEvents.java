import java.util.*;
public class handleEvents {

    private static CallEvent[] getEvents() {
        //TREAT AS A BLACK BOX API CALL, RETURNS CALL EVENTS FOR THE DAY
        CallEvent[] events = {
            new Call("Bob", "Alice", 1711132463L),
            new Call("Carl", "Doug", 1711132465L),
            new Hangup("Alice", "Bob", 1711132467L),
            new Call("Ed", "Frank", 1711132481L),
            new Hangup("Carl", "Doug", 1711132482L),
            new Call("Bob", "Doug", 1711132483L),
            new Hangup("Doug", "Bob", 1711132484L),
            new Hangup("Ed", "Frank", 1711132501L)
        };

        return events;
    }

    public static HashMap<String, Float> getAverageCallLength(CallEvent[] events) {
        //Given call events, get the average call duration for each caller

        //Map person to a hashmap of active calls which tracks who they are calling and when the call started
        HashMap<String, HashMap<String, Long>> activeCalls = new HashMap<>(); 
        //Map person to a list of durations for completed calls
        HashMap<String, LinkedList<Long>> finishedCalls = new HashMap<>();
        for (CallEvent event : events) {
            if (event.getClass() == Call.class) {
                //We are dealing with a call
                
                handleCall(event, activeCalls);
                
            }
            else if (event.getClass() == Hangup.class){
                //we are dealing with a hangup
                handleHangup(event, activeCalls, finishedCalls);
            }
            else {
                //Skip the input and alert there is an invalid entry
                System.out.println("Invalid input");
            }
        }
        //At this point all finished calls tracked, calculate average duration for each caller
        HashMap<String, Float> averageTimes = new HashMap<>();
        LinkedList<Long> curCallList;
        for (String person : finishedCalls.keySet()) {
            curCallList = finishedCalls.get(person);
            Float average = calculateAverage(curCallList);
            averageTimes.put(person, average);

        }
        return averageTimes;
    }

    private static void handleCall(CallEvent event, HashMap<String, HashMap<String, Long>> activeCalls) {
        //put the call event into active call list for the caller
        String caller = event.from;
        String receiver = event.to;
        Long timestamp = event.timestamp;

        HashMap<String, Long> userCalls;
        if (activeCalls.containsKey(caller)) {
            //do something
            userCalls = activeCalls.get(caller);
        }
        else {
            userCalls = new HashMap<>();
        }
        //Mark the recipient and timestamp for the call
        userCalls.put(receiver, timestamp);
        activeCalls.put(caller, userCalls);
    }

    private static void handleHangup(CallEvent event, HashMap<String, HashMap<String, Long>> activeCalls, HashMap<String, LinkedList<Long>> finishedCalls) {
        String to = event.to;
        String from = event.from;
        Long timestamp = event.timestamp;

        //Get call starttime from hashmap active calls
        //Check to
        Long startTimestamp = null;
        String caller = null;
        String receiver = null;
        if (activeCalls.containsKey(to)) {
            if (activeCalls.get(to).containsKey(from)) {
                startTimestamp = activeCalls.get(to).get(from);
                //Delete from from to active calls
                activeCalls.get(to).remove(from); // This call has ended
                caller = to;
                receiver = from;
            }
        }
        if (activeCalls.containsKey(from)) {
            if (activeCalls.get(from).containsKey(to)) {
                startTimestamp = activeCalls.get(from).get(to);
                //Delete from from to active calls
                activeCalls.get(from).remove(to); // This call has ended 
                caller = from;
                receiver = to;
            }
        }
        //check from
        
        if (startTimestamp == null) {
            System.out.println("ERROR hangup before a call");
            return;
        }
        //Calculate the call duration
        Long duration =  timestamp - startTimestamp;
        //Append the call duration to finished calls list for that caller
        LinkedList<Long> callList;
        if (finishedCalls.containsKey(caller)) {
            callList = finishedCalls.get(caller);
        }
        else {
            callList = new LinkedList<>();
        }
        callList.add(duration);
        finishedCalls.put(caller, callList);
    }

    private static Float calculateAverage(LinkedList<Long> list) {
        if (list.isEmpty()) {
            return 0.0f; //if empty return 0
        }
        float sum = 0.0f;
        for (Long num : list) {
            sum += (float) num;
        }
        return  sum / list.size();
    }

    private static LinkedList<String> getCallersAvgBelow(int threshold, HashMap<String, Float> people) {
        LinkedList<String> underAverageCallers = new LinkedList<>();
        for (String caller : people.keySet()) {
            if (people.get(caller) < threshold) {
                underAverageCallers.add(caller);
            }
        }
        return underAverageCallers;

    }

    public static void main(String[] args) {
        CallEvent[] events = getEvents(); //Get our events for the day/time period etc
       
        HashMap<String, Float> avgTimes = getAverageCallLength(events);
        //get list of names with average calls times under 5 seconds
        LinkedList<String> names = getCallersAvgBelow(5, avgTimes);

        //Print out names with average call time under 5
        for (String name: names) {
            System.out.println(name);
        }

        //Check averages for each caller
        for (String caller : avgTimes.keySet()) {
            System.out.println("Caller: " + caller + "; Average time: " + String.valueOf(avgTimes.get(caller)));
        }
        
    }
}
