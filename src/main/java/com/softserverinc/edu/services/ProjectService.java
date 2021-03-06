package com.softserverinc.edu.services;

import com.softserverinc.edu.entities.Project;
import com.softserverinc.edu.entities.ProjectRelease;
import com.softserverinc.edu.entities.User;
import com.softserverinc.edu.entities.enums.UserRole;
import com.softserverinc.edu.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    public Project findById(Long id) {
        return projectRepository.findOne(id);
    }

    public Page<Project> findByTitleContaining(String title, Pageable pageable) {
        return projectRepository.findByTitleContaining(title, pageable);
    }

    public Project findByProjectReleases(ProjectRelease projectRelease) {
        return projectRepository.findByProjectReleases(projectRelease);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.saveAndFlush(project);
    }

    @Transactional
    public void delete(Long id) {
        for(User user: projectService.findById(id).getUsers()){
            user.setRole(UserRole.ROLE_USER);
            user.setProject(null);
            userService.save(user);
        }
        projectRepository.delete(id);
    }

}