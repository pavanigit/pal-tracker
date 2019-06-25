package io.pivotal.pal.tracker;

import ch.qos.logback.core.joran.spi.ElementSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long,TimeEntry> myInmemoryRepo = new HashMap<Long,TimeEntry>();
    private AtomicInteger timeEntryId= new AtomicInteger(0) ;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        long nextId = this.timeEntryId.incrementAndGet();
        TimeEntry repoEntry = new TimeEntry(nextId,timeEntry.getProjectId(),timeEntry.getUserId(),timeEntry.getDate(),timeEntry.getHours());
        myInmemoryRepo.put(nextId,repoEntry);
        return repoEntry;
    }

    @Override
    public TimeEntry find(long id) {
        if(myInmemoryRepo!=null&& !myInmemoryRepo.isEmpty()&&myInmemoryRepo.containsKey(id))
            return myInmemoryRepo.get(id);
        else
            return null;
    }

    @Override
    public List list() {
        return new ArrayList(myInmemoryRepo.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(myInmemoryRepo!=null && myInmemoryRepo.get(id)!=null) {
            TimeEntry repoEntry = new TimeEntry(id,timeEntry.getProjectId(),timeEntry.getUserId(),timeEntry.getDate(),timeEntry.getHours());
            myInmemoryRepo.replace(id,repoEntry);
            return repoEntry;
        }
        return null;
    }

    @Override
    public void delete(long id) {
        if(myInmemoryRepo!=null && myInmemoryRepo.get(id)!=null) {
           myInmemoryRepo.remove(id);
        }
    }
}
