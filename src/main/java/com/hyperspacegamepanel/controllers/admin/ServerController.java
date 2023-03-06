package com.hyperspacegamepanel.controllers.admin;

import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Server;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.Helper;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.ServerService;

@Controller
@RequestMapping("/admin/server")
public class ServerController extends HelperController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private ServerService serverService;

    @GetMapping("/new")
    public String newServer(Model m) {
        m.addAttribute("server", new Server());
        m.addAttribute("randomftpusername", Helper.randomUsernameGenerator());
        m.addAttribute("randomftppassword", Helper.randomPasswordGenerator());
        m.addAttribute("title", "New Server | HyperSpaceGamePanel");
        return "admin/new_server.html";
    }

    @PostMapping("/new")
    public String processNewServer(@Valid @ModelAttribute Server server, BindingResult bindingResult, @RequestParam Integer machineId, @RequestParam Integer userId, Model m) {

        if(bindingResult.hasErrors()) {
            httpSession.setAttribute("status", "BIND_RESULT_HAS_ERROR");
            m.addAttribute("server", server);
            return "admin/new_server.html";
        }

        Optional<Machine> machine = this.machineRepo.findById(machineId);
        Optional<User> user = this.userRepo.findById(userId);

        if(!machine.isPresent() || !user.isPresent()) {
            httpSession.setAttribute("status", "CANNOT_FIND_THE_MACHINE_OR_USER");
            return "redirect:/admin/server/new";
        } 

        try {

         server = this.serverService.createServer(server, machine.get(), user.get());

         if(server != null) {
            httpSession.setAttribute("status", "SERVER_CREATED_SUCCESSFULLY");
            return "redirect:/admin/server?id="+server.getId();
         }

        } catch(Exception e) {
            String exceptionMessage = e.getMessage();
            if(exceptionMessage == "EXPIRATON_DATE_IS_WRONG") {
                httpSession.setAttribute("status", "EXPIRATON_DATE_IS_WRONG");
            } else {
                e.printStackTrace();
                httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
            }
        }

        httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
        return "redirect:/admin/server/new";
    }

    @GetMapping("")
    public String showServer(Model m) {
        return "admin/server.html";
    }

}
