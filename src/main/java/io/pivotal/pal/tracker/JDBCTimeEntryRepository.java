package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;


import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JDBCTimeEntryRepository implements TimeEntryRepository {
    private JdbcTemplate dbTemplate;
    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
    public JDBCTimeEntryRepository(DataSource dataSource) {
        this.dbTemplate = new JdbcTemplate(dataSource);
    }
    public JDBCTimeEntryRepository() {

    }
    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        dbTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(
                    "insert into time_entries (project_id, user_id,date, hours) values (?,?,?,?)",
                    RETURN_GENERATED_KEYS);

                    stmt.setLong(1, timeEntry.getProjectId());
                    stmt.setLong(2, timeEntry.getUserId());
                    stmt.setDate(3, Date.valueOf(timeEntry.getDate()));
                    stmt.setLong(4,timeEntry.getHours());
                    return stmt;
        },generatedKeyHolder);
       return find(generatedKeyHolder.getKey().longValue());

    }

    @Override
    public TimeEntry find(Long id) {
        return dbTemplate.query("select * from time_entries where id=?",new Object[]{id},extractor);
    }

    @Override
    public List list() {

        return dbTemplate.query("select * from time_entries",mapper);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (this.find(id)!=null){
            dbTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(
                        "update time_entries set project_id=?, user_id=?,date=?, hours=? where id=?"
                        );

                stmt.setLong(1, timeEntry.getProjectId());
                stmt.setLong(2, timeEntry.getUserId());
                stmt.setDate(3, Date.valueOf(timeEntry.getDate()));
                stmt.setLong(4,timeEntry.getHours());
                stmt.setLong(5,id);
                return stmt;
            });
            return find(id);
        }
        return null;
    }

    @Override
    public void delete(long id) {
        if (this.find(id)!=null){
            dbTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(
                        "delete from time_entries where id=?"
                );

                stmt.setLong(1,id);
                return stmt;
            });
        }
    }
}
