package com.hyperspacegamepanel.controllers.admin;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.DataCenteralizedController;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.machine.MachineDetails;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.MachineDetailsRepository;
import com.hyperspacegamepanel.services.MachineService;
import com.jcraft.jsch.JSchException;

@Controller
@RequestMapping("/admin/machine")
public class MachineController extends DataCenteralizedController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private HttpSession httpSession;

    // showing page where user can add new machine to the database.
    @GetMapping("/new")
    public String newMachine(Model m) {
        m.addAttribute("machine", new Machine());
        m.addAttribute("title", "New Machine | HyperSpaceGamePanel");
        return "admin/machine_module/new_machine.html";
    }

    /*
     * creating new machine.
     */
    @PostMapping("/create-new")
    public String processNewMachine(@Valid @ModelAttribute Machine machine, BindingResult bindingResult, Model m) throws JSchException, IOException {
        if (bindingResult.hasErrors()) return "admin/machine_module/new_machine.html";

        try {

            Machine createdMachine = this.machineService.createMachine(machine).join();
            return "redirect:/admin/machine/view/"+createdMachine.getId();

        } catch (Exception e) {
            if(e.getMessage().equals("WRONG_CREDENTIALS_FOR_MACHINE_TO_CONNECT")) {
                httpSession.setAttribute("status", new Alert("Unable to connect, Wrong credentials for vps to connect, please try again.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/machine/new";
            }
            if(e.getMessage().equals("MACHINE_ALREADY_EXISTS_WITH_PROVIDED_IP_ADDRESS")) {
                httpSession.setAttribute("status", new Alert("Machine already exists with provided ip address, please add any other.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/machine/new/";
            }
            httpSession.setAttribute("status", new Alert("Something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
        }
        return "redirect:/admin/machine/new/";
    }

    // showing machine
    @GetMapping("/view/{machineId}")
    public String showMachine(@PathVariable(required = false) Integer machineId, @RequestParam(required = false) String action, HttpSession httpSession, Model m) { 

        if(machineId == null) {
            httpSession.setAttribute("status", new Alert("Cannot find the machine.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/machines";
        }

        try {

            Machine machine = this.machineService.getMachine(machineId).join();
            // this machineInfo is cacheable however we'll find another solution in future to avoid call machineInfo each time if user request.
            Map<String, String> machineInfo = this.machineService.getMachineInfo(machine.getId()).join();

            machineInfo.forEach((key, value) -> {
                m.addAttribute(key, value);
            });

            m.addAttribute("machine", machine);
            m.addAttribute("title", machine.getName() + " | HyperSpaceGamePanel");
        } catch (Exception e) {
            if(e.getMessage().contains("Machine not found")) {
                httpSession.setAttribute("status", new Alert("Cannot find the machine", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/machines";
            }
            httpSession.setAttribute("status", new Alert("Something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/machines";
        }

        return "admin/machine_module/machine.html";
    }

}
