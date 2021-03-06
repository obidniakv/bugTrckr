package com.softserverinc.edu.controllers;

import com.softserverinc.edu.constants.PageConstant;
import com.softserverinc.edu.entities.Project;
import com.softserverinc.edu.entities.User;
import com.softserverinc.edu.entities.enums.UserRole;
import com.softserverinc.edu.services.IssueService;
import com.softserverinc.edu.services.ProjectReleaseService;
import com.softserverinc.edu.services.ProjectService;
import com.softserverinc.edu.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class ProjectController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectReleaseService releaseService;

    @Autowired
    private IssueService issueService;

    @GetMapping("/projects")
    public String listOfProjects(@PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageable,
                                 ModelMap model, Principal principal) {
        if (principal != null) {
            model.addAttribute("loggedUser", userService.findByEmailIs(principal.getName()));
        }
        model.addAttribute("listOfProjects", projectService.findAll(pageable));
        return "projects";
    }

    @PostMapping("projects/search")
    public String projectSearchByTitle(@RequestParam String title, Model model,
                                       @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageable) {
        model.addAttribute("listOfProjects", projectService.findByTitleContaining(title, pageable));
        return "projects";
    }

    @PreAuthorize("@projectSecurityService.hasPermissionToViewProject(#projectId)")
    @GetMapping("projects/project/{projectId}")
    public String projectPage(@PathVariable @P("projectId") Long projectId, Model model,
            @Qualifier("release") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableRelease,
            @Qualifier("user") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableUser,
            @Qualifier("issue") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableIssue) {
        usersRolesInProject(model);
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("usersList", userService.findUsersInProjectPageable(project, false, 1, pageableUser));
        model.addAttribute("releaseList", releaseService.findByProject(project, pageableRelease));
        model.addAttribute("listOfIssues", issueService.findByProject(project, pageableIssue));
        return "project";
    }

    @PostMapping("/projects/project/{projectId}/users_search")
    public String searchUsersInProjects(@PathVariable("projectId") Long projectId, Model model,
            @RequestParam String searchedParam,
            @RequestParam UserRole role,
            @RequestParam String searchedString,
            @Qualifier("release") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableRelease,
            @Qualifier("user") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableUser,
            @Qualifier("issue") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableIssue) {
        usersRolesInProject(model);
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("usersList", userService.searchByUsersInProject(project, searchedParam, role, searchedString,
                pageableUser));
        model.addAttribute("releaseList", releaseService.findByProject(project, pageableRelease));
        model.addAttribute("listOfIssues", issueService.findByProject(project, pageableIssue));
        return "project";
    }

    @PostMapping("/projects/project/{projectId}/releases/search")
    public String searchByReleaseTitle(@PathVariable Long projectId, @RequestParam String searchedString, Model model,
            @Qualifier("release") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableRelease,
            @Qualifier("user") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableUser,
            @Qualifier("issue") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableIssue) {
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("usersList", userService.findUsersInProjectPageable(project, false, 1, pageableUser));
        model.addAttribute("releaseList", releaseService.searchByTitle(project, searchedString, pageableRelease));
        model.addAttribute("listOfIssues", issueService.findByProject(project, pageableIssue));
        return "project";
    }

    @PostMapping("/projects/project/{projectId}/search_issues")
    public String searchByIssueTitle(@PathVariable Long projectId, @RequestParam String searchedString, Model model,
                  @Qualifier("release") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableRelease,
                  @Qualifier("user") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableUser,
                  @Qualifier("issue") @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageableIssue) {
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("usersList", userService.findUsersInProjectPageable(project, false, 1, pageableUser));
        model.addAttribute("releaseList", releaseService.findByProject(project, pageableRelease));
        model.addAttribute("listOfIssues", issueService.findIssuesByProject(project, searchedString ,pageableIssue));
        return "project";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/projects/add")
    public String addProject(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("formaction", "new");
        return "project_form";
    }

    @PostMapping("/projects/add")
    public String addProjectPost(@ModelAttribute @Valid Project project, BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            return "project_form";
        }
        if (project.getId() == null) {
            redirectAttributes.addFlashAttribute("msg", String.format("%s added successfully!", project.getTitle()));

        } else {
            redirectAttributes.addFlashAttribute("msg", String.format("%s updated successfully!", project.getTitle()));
        }
        projectService.save(project);
        return "redirect:/projects/project/" + project.getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/projects/{id}/remove")
    public String removeProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("msg", "Project was deleted");
        projectService.delete(id);
        return "redirect:/projects";
    }

   @PreAuthorize("@projectSecurityService.hasPermissionToProjectManagement(#projectId)")
    @GetMapping("/projects/{id}/edit")
    public String editProject(@PathVariable @P("projectId") Long id, Model model) {
        model.addAttribute("project", projectService.findById(id));
        model.addAttribute("formaction", "edit");
        return "project_form";
    }

    @PreAuthorize("@projectSecurityService.hasPermissionToProjectManagement(#projectId)")
    @GetMapping("/projects/project/{projectId}/usersWithoutProject")
    public String usersWithoutProject(@PathVariable @P("projectId") Long projectId, Model model,
                                      @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS)Pageable pageable){
        model.addAttribute("userList", userService.findNotDeletedUsersByRole(UserRole.ROLE_USER, false, 1, pageable));
        model.addAttribute("project", projectService.findById(projectId));
        usersRolesInProject(model);
        return "users_without_project";
    }

    @PostMapping("/projects/project/{projectId}/usersWithoutProject/search")
    public String searchUsersWithoutProjects(@RequestParam String searchedParam, @RequestParam String searchedString,
                                             @PathVariable Long projectId, Model model,
                                             @PageableDefault(PageConstant.AMOUNT_PROJECT_ELEMENTS) Pageable pageable) {
        usersRolesInProject(model);
        model.addAttribute("project", projectService.findById(projectId));
        model.addAttribute("userList", userService.searchByUsersWithoutProject(searchedParam, searchedString,
                pageable));
        return "users_without_project";
    }

    @PreAuthorize("@projectSecurityService.hasPermissionToProjectManagement(#projectId)")
    @GetMapping("/projects/project/{projectId}/removeUser/{userId}")
    public String removeUserFromProject(@PathVariable Long userId, @PathVariable @P("projectId") Long projectId,
                                        @Param("principal") Principal principal,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.findOne(userId);
        userService.deleteFromProject(userId);
        redirectAttributes.addFlashAttribute("msg", String.format("%s %s was removed from project",
                user.getFirstName(), user.getLastName()));
        return "redirect:/projects/project/" + projectId;
    }

    @PreAuthorize("@projectSecurityService.hasPermissionToProjectManagement(#projectId)")
    @GetMapping("/projects/project/{projectId}/usersWithoutProject/{userId}/changeRole")
    public String changeUserRoleGet(@PathVariable @P("projectId") Long projectId, @PathVariable Long userId,
                                    RedirectAttributes redirectAttributes) {
        User user = userService.findOne(userId);
        Project project = projectService.findById(projectId);
        if(project.getUsers().isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", String.format("%s %s was added as Project Manager",
                    user.getFirstName(), user.getLastName()));
            userService.changeUserRole(user, project, UserRole.ROLE_PROJECT_MANAGER);
            return "redirect:/projects/project/" + projectId;
        }
        userService.changeUserRole(user, project, null);
        redirectAttributes.addFlashAttribute("msg", String.format("%s %s's role was changed to %s",
                user.getFirstName(), user.getLastName(), user.getRole()));
        return "redirect:/projects/project/" + projectId;
    }

    @PostMapping("/projects/project/{projectId}/usersWithoutProject/{userId}/selectRole")
    public String selectUserRole(@ModelAttribute("role") UserRole role,
                                 @PathVariable Long projectId, @PathVariable Long userId,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findOne(userId);
        Project project = projectService.findById(projectId);
        redirectAttributes.addFlashAttribute("msg", String.format("%s %s %s was added to %s ", role,
                user.getFirstName(), user.getLastName(), project.getTitle()));
        userService.changeUserRole(user, project, role);
        return "redirect:/projects/project/" + projectId;
    }

    private void usersRolesInProject(Model model) {
        model.addAttribute("DEV", UserRole.ROLE_DEVELOPER);
        model.addAttribute("QA", UserRole.ROLE_QA);
        model.addAttribute("PM", UserRole.ROLE_PROJECT_MANAGER);
    }
}