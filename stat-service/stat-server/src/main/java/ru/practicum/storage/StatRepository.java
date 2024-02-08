package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("select s.app as app, s.uri as uri, count(s.ip) as hits " +
            "from Stat as s " +
            "where " +
            "((:uris) is null or uri in (:uris)) and " +
            "s.timestamp >= :start and s.timestamp <= :end " +
            "group by app, uri " +
            "order by hits desc")
    List<ViewStat> countHits(@Param("start") @NonNull LocalDateTime start,
                             @Param("end") @NonNull LocalDateTime end,
                             @Param("uris") @Nullable Collection<String> uris);

    @Query("select s.app as app, s.uri as uri, count(distinct s.ip) as hits " +
            "from Stat as s " +
            "where " +
            "((:uris) is null or uri in (:uris)) and " +
            "s.timestamp >= :start and s.timestamp <= :end " +
            "group by app, uri " +
            "order by hits desc")
    List<ViewStat> countUniqueHits(@Param("start") @NonNull LocalDateTime start,
                                   @Param("end") @NonNull LocalDateTime end,
                                   @Param("uris") @Nullable Collection<String> uris);

}
