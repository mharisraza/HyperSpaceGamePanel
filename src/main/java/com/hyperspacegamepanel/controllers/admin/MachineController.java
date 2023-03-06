package com.hyperspacegamepanel.controllers.admin;

import java.io.IOException;
import java.util.Map;
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
import com.hyperspacegamepanel.entities.MachineDetails;
import com.hyperspacegamepanel.helper.VPSConnector;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.MachineDetailsRepository;
import com.hyperspacegamepanel.services.MachineService;
import com.hyperspacegamepanel.services.VPSService;
import com.hyperspacegamepanel.services.impl.VPSServiceImpl;
import com.jcraft.jsch.JSchException;

@Controller
@RequestMapping("/admin/machine")
public class MachineController extends HelperController {

    

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private MachineDetailsRepository machineDetailsRepo;

    @Autowired
    private MachineService machineService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private VPSConnector connector;

    // global variable for VPS machine info
    private String totalRam;
    private String totalStorage;
    private String hostname;
    private String location;
    private String totalCPUs;
    private String cpuProcessorName;
    private String uptime;


    // showing page where user can add new machine to the database.
    @GetMapping("/new")
    public String newMachine(Model m) {
        m.addAttribute("machine", new Machine());
        m.addAttribute("title", "New Machine | HyperSpaceGamePanel");
        return "admin/new_machine.html";
    }

    /*  processing and handling logic for newly added machine via
    VPSConnector and VPSService.
    */
    @PostMapping("/create-new")
    public String processNewMachine(@Valid @ModelAttribute Machine machine, BindingResult bindingResult) throws JSchException, IOException {
        if(bindingResult.hasErrors()) {
            return "admin/new_machine.html";
        }

        try {

        this.connector.connect(machine);
        VPSService vpsService = new VPSServiceImpl(this.connector, machine);

        if(this.connector.isConnected()) {

            MachineDetails machineDetails = new MachineDetails();

            Map<String, String> machineInfo = vpsService.getMachineInfo();

            machineDetails.setHostname(machineInfo.get("hostname"));
            machineDetails.setCpuProcessor(machineInfo.get("cpu_name"));
            machineDetails.setLocation(machineInfo.get("location"));
            machineDetails.setTotalCPUs(machineInfo.get("total_cpus"));
            machineDetails.setTotalRam(machineInfo.get("total_ram"));

            String totalStorage = machineInfo.get("total_storage");
            machineDetails.setTotalStorage(totalStorage.substring(totalStorage.indexOf("/") + 1, totalStorage.indexOf("(")));
            machineDetails.setMachine(machine);

          Machine createdMachine = this.machineService.createMachine(machine);
          this.machineDetailsRepo.save(machineDetails);
          httpSession.setAttribute("status", "MACHINE_ADDED_SUCCESSFULLY");
          return "redirect:/admin/machine?id=" + createdMachine.getId();
        }

        } catch (Exception e) {

         String exceptionMessage = e.getMessage();

         if(exceptionMessage != null) {
            if(exceptionMessage.equalsIgnoreCase("Auth fail") || exceptionMessage == "Auth fail") {
                httpSession.setAttribute("status", "WRONG_CREDENTIALS_CONNECTION_FAILED");
                return "redirect:/admin/machines";
             }
         }

         httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
         e.printStackTrace();
        }
        return "redirect:/admin/machines";
    }

    // showing machine details with provided machine Id
    @GetMapping("")
    public String getMachine(@RequestParam(name = "id", required = false) Integer machineId, @RequestParam(required = false) String action, Model m) {
        
        if(machineId == null) {
            httpSession.setAttribute("status", "CANT_FIND_MACHINE");
            return "redirect:/admin/machines";
        }

        Optional<Machine> machine = this.machineRepo.findById(machineId);
        if(!machine.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_MACHINE");
            return "redirect:/admin/machines";
        }

        VPSService vpsService = new VPSServiceImpl(this.connector, machine.get());
        getMachineInfo(vpsService);

        if(action != null) {

            switch(action) {

                case "restart":
                vpsService.restartMachine();
                httpSession.setAttribute("status", "VPS_RESTARTED_SUCCESSFULLY");
                return "redirect:/admin/machine?id="+machineId;

                // first disconnect the vps connection
                // then delete it from database.

                case "delete":
                this.connector.disconnect();
                this.machineRepo.delete(machine.get());
                httpSession.setAttribute("status", "MACHINE_DELETED_SUCCESSFULLY");
                return "redirect:/admin/machines";
            }
        }

        // setting attribute for machine information
        m.addAttribute("vps_totalRam", this.totalRam);
        m.addAttribute("vps_totalStorage", this.totalStorage);
        m.addAttribute("vps_hostname", this.hostname);
        m.addAttribute("vps_cpuName", this.cpuProcessorName);
        m.addAttribute("vps_uptime", this.uptime);
        m.addAttribute("vps_location", this.location);
        m.addAttribute("vps_totalCPUS", this.totalCPUs);


        m.addAttribute("machine", machine.get());
        m.addAttribute("title", machine.get().getName() + " | HyperSpaceGamePanel");
        return "admin/machine.html";
    }

    // updating hostname of the machine
    @PostMapping("/updateHostname")
    public String updateHostName(@RequestParam(required = false) Integer machineId, @RequestParam(required = false) String hostname) {
        try {

            if(machineId == null) {
                httpSession.setAttribute("status", "CANT_FIND_MACHINE");
                return "redirect:/admin/machines";
            }
    
            Optional<Machine> machine = this.machineRepo.findById(machineId);
            if(!machine.isPresent()) {
                httpSession.setAttribute("status", "CANT_FIND_MACHINES");
                return "redirect:/admin/machines";
            }
    
            if(hostname == null) {
                httpSession.setAttribute("status", "HOST_NAME_IS_EMPTY");
                return "redirect:/admin/machine?id="+machineId;
            }
    
            if(hostname.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                httpSession.setAttribute("status", "HOSTNAME_CONTAINING_SPECIAL_CHAR");
                return "redirect:/admin/machine?id="+machineId;
            }
    
            VPSService vpsService = new VPSServiceImpl(this.connector, machine.get());
            vpsService.updateHostname(hostname);
    
            httpSession.setAttribute("status", "HOSTNAME_CHANGED_SUCCESSFULLY");

        } catch(Exception e) {
            e.printStackTrace();
            httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
        }

        return "redirect:/admin/machine?id="+machineId;

    }


    public void getMachineInfo(VPSService vpsService) {
             Map<String, String> machineInfo = vpsService.getMachineInfo();
             this.totalRam = machineInfo.get("total_ram");
             this.totalStorage = machineInfo.get("total_storage");
             this.cpuProcessorName = machineInfo.get("cpu_name");
             this.totalCPUs = machineInfo.get("total_cpus");
             this.hostname = machineInfo.get("hostname");
             this.location = machineInfo.get("location");
             this.uptime = machineInfo.get("uptime");
    }



}
