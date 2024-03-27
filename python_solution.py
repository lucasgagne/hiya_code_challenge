import pandas as pd
import numpy as np

events = [
    "call Bob Alice 1711132463",
    "call Carl Doug 1711132465",
    "hangup Alice Bob 1711132467",
    "call Ed Frank 1711132481",
    "hangup Carl Doug 1711132482",
    "call Bob Doug 1711132483",
    "hangup Doug Bob 1711132484",
    "hangup Ed Frank 1711132501"
]

def addEvent(event, callData, callLogs, callersList):
    event = event.split()
    assert len(event) == 4, "Invalid input"  
    eventType, frm, to, timestamp = event[0], event[1], event[2], int(event[3])
    
    if eventType == "call":
        callersList.add(frm)
        people = sorted([frm, to])
        newRow = {'type': [eventType], 'caller': [frm], 'people': [people], 'timestamp': [timestamp]} 
        temp_frame = pd.DataFrame(newRow)
        callData = pd.concat([callData, temp_frame], ignore_index = True)
        
    elif eventType == "hangup":
        people = sorted([frm, to])
        #remove from table
        temp_frame = callData[callData['people'].apply(lambda x: x == people)]
       
        call_start_time = temp_frame["timestamp"].iloc[0]
        callData = callData.drop(temp_frame.index)
        duration = timestamp - call_start_time 
        caller = temp_frame["caller"].iloc[0]
        callLogs.loc[len(callLogs)] = [caller, duration]
        #add into new table
    else:
        raise "ERROR invalid data entry"
    
    return callData

columns = ['type', 'caller', 'people', 'timestamp']
callData = pd.DataFrame(columns=columns) #data frame to track active calls/ activity

columns = ["caller", "call time"]
callLogs = pd.DataFrame(columns = columns) #data frame to track finished calls and their durations

callersList = set()

for event in events:
    callData = addEvent(event, callData, callLogs, callersList)
    print("\n Event added: ")
    print(callData)
    print(callLogs)

    

def get_average_calltime(person, callLogs):
    return np.mean(callLogs[callLogs["caller"].apply(lambda x: x == person)]["call time"])

def get_averages_under(threshold, callLogs, callersList):
    finArr = []
    for caller in callersList:
        if get_average_calltime(caller, callLogs) < threshold:
            finArr.append(caller)
    return finArr

print(get_averages_under(5, callLogs, callersList))
