package com.cb.th.claims.cmx.assign;

import com.cb.th.claims.cmx.repository.fnol.assignment.FnolAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class SurveyorAssignmentUpdater {
    private final FnolAssignmentRepository repo;

    @KafkaListener(topics = "${topics.assignmentMade}", groupId = "cmx-updater")
    @Transactional
    public void handle(SurveyorAssignmentMade evt) {
        repo.save(FnolAssignment.from(evt));
        // optionally update FNOL table with surveyor info, notify UI, etc.
    }
}