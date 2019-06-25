package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
    private TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {

        this.timeEntryRepository = timeEntryRepository;
    }

   @PostMapping(path = "/time-entries", consumes = "application/json", produces = "application/json")
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry createdEntry = timeEntryRepository.create(timeEntryToCreate);
        return new ResponseEntity(createdEntry,HttpStatus.CREATED);
    }

    @GetMapping(path ="/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable  long timeEntryId) {
        TimeEntry entry = timeEntryRepository.find(timeEntryId);
        if(entry!=null)
            return new ResponseEntity(entry,HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path ="/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        return new ResponseEntity(timeEntryRepository.list(),HttpStatus.OK);
    }

    @PutMapping(path ="/time-entries/{timeEntryId}",consumes = "application/json", produces = "application/json")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry updatedEntry) {
        TimeEntry entry =  timeEntryRepository.update(timeEntryId,updatedEntry);
        if(entry!=null)
            return new ResponseEntity(entry,HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path ="/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
