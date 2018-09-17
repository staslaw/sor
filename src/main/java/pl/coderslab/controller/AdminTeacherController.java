package pl.coderslab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.coderslab.entity.*;
import pl.coderslab.repository.*;
import pl.coderslab.service.TeacherServiceImpl;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/teachers")
public class AdminTeacherController {
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    TeacherServiceImpl teacherService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AdminRepository adminRepository;


    @RequestMapping("")
    public String toTeachers(Model model) {
        model.addAttribute("teachers", teacherRepository.findAllWithSubjectListQuery());
        return "admin/teachers";
    }

    @RequestMapping("/add")
    public String addTeacher1(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "admin/teacherForm";
    }

    @PostMapping("/add")
    public String addTeacher2(@Valid Teacher teacher, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/teacherForm";
        } else {
            Student student = studentRepository.findByUsername(teacher.getUsername());
            Admin admin = adminRepository.findByUsername(teacher.getUsername());
            if (student == null && admin == null) {
                try {
                    teacherService.saveTeacher(teacher);
                    redirectAttributes.addFlashAttribute("message", "Nauczyciel został dodany.");
                    return "redirect:/admin/teachers";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("messageDanger", "Nauczyciel nie został dodany.");
                    return "redirect:/admin/teachers";
                }
            } else {
                redirectAttributes.addFlashAttribute("messageDanger", "Nauczyciel nie został dodany. Podaj inny email.");
                return "redirect:/admin/teachers";
            }
        }
    }

    @RequestMapping("/remove")
    public String removeTeacher(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherRepository.delete(id);
            redirectAttributes.addFlashAttribute("message", "Nauczyciel został usunięty.");
            return "redirect:/admin/teachers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageDanger", "Nie możesz usunąć nauczyciela, jest on powiązany z innymi elementami systemu.");
            return "redirect:/admin/teachers";

        }
    }

    @RequestMapping("/update")
    public String updateTeacher1(@RequestParam Long id, Model model) {
        Teacher teacher = teacherRepository.findOneWithSubjectListQuery(id);
        model.addAttribute("teacher", teacher);
        return "admin/teacherForm";
    }

    @PostMapping("/update")
    public String updateTeacher2(@Valid Teacher teacher, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/teacherForm";
        } else {
            Student student = studentRepository.findByUsername(teacher.getUsername());
            Admin admin = adminRepository.findByUsername(teacher.getUsername());
            if (student == null && admin == null) {
                try {
                    teacherService.saveTeacher(teacher);
                    redirectAttributes.addFlashAttribute("message", "Nauczyciel został zmodyfikowany.");
                    return "redirect:/admin/teachers";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("messageDanger", "Nauczyciel nie został zmodyfikowany.");
                    return "redirect:/admin/teachers";
                }
            } else {
                redirectAttributes.addFlashAttribute("messageDanger", "Nauczyciel nie został zmodyfikowany. Podaj inny email");
                return "redirect:/admin/teachers";
            }
        }
    }

    @RequestMapping("/details")
    public String detailsTeacher(@RequestParam Long id, Model model) {
        Teacher teacher = teacherRepository.findOneWithSubjectListQuery(id);
        model.addAttribute("teacher", teacher);
        return "admin/teacherDetails";
    }


    @ModelAttribute("subjects")
    public List<Subject> subjects() {
        return subjectRepository.findAll();
    }
}
