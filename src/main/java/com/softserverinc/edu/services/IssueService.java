package com.softserverinc.edu.services;

import com.softserverinc.edu.entities.*;
import com.softserverinc.edu.entities.enums.IssuePriority;
import com.softserverinc.edu.entities.enums.IssueStatus;
import com.softserverinc.edu.entities.enums.IssueType;
import com.softserverinc.edu.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    public Issue findById(Long id) {
        return issueRepository.findOne(id);
    }

    public List<IssueStatus> getAvaliableStatusesForStatus(IssueStatus status) {
        List<IssueStatus> result = new ArrayList<>();
        switch (status) {
            case OPEN:
                result.add(IssueStatus.IN_PROGRESS);
                result.add(IssueStatus.INVALID);
                return result;
            case IN_PROGRESS:
                result.add(IssueStatus.OPEN);
                result.add(IssueStatus.QA_VALIDATION);
                result.add(IssueStatus.INVALID);
                return result;
            case INVALID:
                result.add(IssueStatus.OPEN);
                return result;
            case QA_VALIDATION:
                result.add(IssueStatus.OPEN);
                result.add(IssueStatus.RESOLVED);
                return result;
            case RESOLVED:
                result.add(IssueStatus.OPEN);
                result.add(IssueStatus.INVALID);
                return result;
            default:
                return result;
        }
    }

    @Transactional
    public Page<Issue> findByProjectRelease(ProjectRelease projectRelease, Pageable pageable) {
        return issueRepository.findByProjectRelease(projectRelease, pageable);
    }

    @Transactional
    public Page<Issue> findByReleaseAndIssueTitle(ProjectRelease projectRelease, String searchedString, Pageable pageable) {
        return issueRepository.findByProjectReleaseAndTitleContaining(projectRelease, searchedString, pageable);
    }

    public boolean isStatusChanged(Issue changedIssue) {
        Issue oldIssue = findById(changedIssue.getId());
        return !(oldIssue.getStatus().equals(changedIssue.getStatus()));
    }

    public boolean isAssigneeChanged(Issue changedIssue) {
        Issue oldIssue = findById(changedIssue.getId());
        return !(oldIssue.getAssignee().equals(changedIssue.getAssignee()));
    }

    public List<Issue> findByAssignee(User assignee, Pageable pageable) {
        return issueRepository.findByAssignee(assignee, pageable);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    @Transactional
    public Issue save(Issue issue) {
        return issueRepository.saveAndFlush(issue);
    }

    @Transactional
    public void delete(Long id) {
        issueRepository.delete(id);
    }

    @Transactional
    public Issue update(Issue issue) {
        return issueRepository.saveAndFlush(issue);
    }

    public Page<Issue> findByTitleContaining(String title, Pageable pageable) {
        return issueRepository.findByTitleContaining(title, pageable);
    }

    public Page<Issue> findAll(Pageable pageable) {
        return issueRepository.findAll(pageable);
    }

    @Transactional
    public Page<Issue> findByProject(Project project, Pageable pageable) {
        return issueRepository.findByProject(project, pageable);
    }

}
